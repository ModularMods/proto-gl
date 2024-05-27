bl_info = {
    "name": "Export PMOD",
    "blender": (2, 80, 0),
    "category": "Import-Export",
    "description": "Export to PMOD format",
    "author": "Protoxy22",
    "version": (0, 0, 1),
    "location": "File > Export > PMOD (.pmod)"
}

import bpy
from bpy_extras.io_utils import ExportHelper
from bpy.props import StringProperty, BoolProperty
from bpy.types import Operator
import struct

class ExportPMOD(Operator, ExportHelper):
    """Export to the PMOD format"""
    bl_idname = "export_scene.pmod"
    bl_label = "Export PMOD"
    bl_options = {'PRESET'}

    filename_ext = ".pmod"

    filter_glob: StringProperty(
        default="*.pmod",
        options={'HIDDEN'},
        maxlen=255,
    )

    def execute(self, context):
        return write_pmod(context, self.filepath)

def menu_func_export(self, context):
    self.layout.operator(ExportPMOD.bl_idname, text="PMOD (.pmod)")

def register():
    bpy.utils.register_class(ExportPMOD)
    bpy.types.TOPBAR_MT_file_export.append(menu_func_export)

def unregister():
    bpy.utils.unregister_class(ExportPMOD)
    bpy.types.TOPBAR_MT_file_export.remove(menu_func_export)

if __name__ == "__main__":
    register()


def write_pmod(context, filepath):
    scene = context.scene
    with open(filepath, 'wb') as f:
        # Write header
        f.write(b'PMOD')  # Magic number
        print("Magic number: PMOD")

        has_armature = any(obj.type == 'ARMATURE' for obj in scene.objects)
        has_nodes = any(obj.type != 'ARMATURE' for obj in scene.objects)

        # Set flags based on the presence of armatures and nodes
        flags = 0
        if has_armature:
            flags |= 0b00000010  # Armature present
        if has_nodes:
            flags |= 0b00000001  # Nodes present

        # Write version and flags in big-endian format
        version_flags = struct.pack('>BBB', 1, 0, flags)
        f.write(version_flags)
        print(f" Version and flags: {1, 0, flags}")

        # Determine number of nodes (excluding armatures)
        num_nodes = len([obj for obj in scene.objects if obj.type != 'ARMATURE'])

        # Armature related information
        if has_armature:
            armature = next((obj for obj in scene.objects if obj.type == 'ARMATURE'), None)
            bones = armature.data.bones
            # Write the number of joints and nodes in big-endian format
            joint_node_counts = struct.pack('>BB', len(bones), num_nodes)
            f.write(joint_node_counts)
            print(f"  Joints and nodes: {len(bones), num_nodes}")

            # Write skeleton name
            skeleton_name = armature.name.encode() + b'\0'
            f.write(skeleton_name)
            print(f"  Skeleton name: {armature.name}")

            for bone in bones:
                bone_name = bone.name.encode() + b'\0'
                f.write(bone_name)
                print(f"   Bone name: {bone.name}")

                parent_name = bone.parent.name.encode() + b'\0' if bone.parent else b'root\0'
                f.write(parent_name)
                print(f"   Parent name: {bone.parent.name if bone.parent else 'root'}")

                # Inverse bind matrix in big-endian format
                matrix = bone.matrix_local.inverted()
                matrix_data = struct.pack('>16f', *matrix)
                f.write(matrix_data)
                print(f"   Matrix: {list(matrix)}")

        else:
            # Write zero for number of joints and number of nodes in big-endian format
            zero_joint_node_counts = struct.pack('>BB', 0, num_nodes)
            f.write(zero_joint_node_counts)
            print(f"  Zero joint and node counts: {0, num_nodes}")

        # Iterate through each object and export nodes that are not armatures
        for obj in scene.objects:
            if obj.type != 'ARMATURE':
                node_name = obj.name.encode() + b'\0'
                f.write(node_name)
                print(f" Node name: {obj.name}")

                parent_name = obj.parent.name.encode() + b'\0' if obj.parent and obj.parent.type != 'ARMATURE' else b'root\0'
                f.write(parent_name)
                print(f" Parent name: {obj.parent.name if obj.parent and obj.parent.type != 'ARMATURE' else 'root'}")

                loc, rot, scale = obj.location, obj.rotation_quaternion, obj.scale
                loc_data = struct.pack('>3f', *loc)
                rot_data = struct.pack('>4f', *rot)
                scale_data = struct.pack('>3f', *scale)
                f.write(loc_data)
                f.write(rot_data)
                f.write(scale_data)
                print(f" Location: {list(loc)}")
                print(f" Rotation: {list(rot)}")
                print(f" Scale: {list(scale)}")

                # Determine number of meshes contained in this node
                num_meshes = 1
                num_meshes_data = struct.pack('>I', num_meshes)
                f.write(num_meshes_data)
                print(f" Number of meshes: {num_meshes}")

                # Export meshes
                if obj.type == 'MESH':
                    mesh = obj.to_mesh(preserve_all_data_layers=True, depsgraph=context.evaluated_depsgraph_get())
                    mesh.calc_loop_triangles()

                    # Write the mesh name
                    mesh_name = obj.data.name.encode() + b'\0'
                    f.write(mesh_name)
                    print(f"  Mesh name: {obj.data.name}")

                    vertices = []
                    normals = []
                    uvs = []
                    uv_layer = mesh.uv_layers.active.data if mesh.uv_layers.active else None

                    for vertex in mesh.vertices:
                        vertices.extend([vertex.co.x, vertex.co.y, vertex.co.z])
                        normals.extend(vertex.normal[:])

                    if uv_layer:
                        for loop in mesh.loops:
                            uv = uv_layer[loop.index].uv
                            uvs.extend([uv.x, uv.y])

                    # Collect mesh data
                    num_indices = len(mesh.loop_triangles) * 3
                    num_vertices = len(mesh.vertices)
                    num_uvs = len(uvs)

                    # Pack indices and vertices count in big-endian format
                    mesh_counts = struct.pack('>IIIII', num_indices, num_vertices, num_uvs, 0, 0)  # Number of indices, vertices, joints, weights (assuming no skeletal data here)
                    f.write(mesh_counts)
                    print(f"  Mesh counts: {num_indices, num_vertices, num_uvs, 0, 0}")

                    # Gather indices
                    indices = []
                    for tri in mesh.loop_triangles:
                        indices.extend(tri.vertices)
                    # Pack indices in big-endian format
                    indices_data = struct.pack(f'>{num_indices}I', *indices)
                    f.write(indices_data)
                    print(f"  Indices: {indices}")

                    # Pack vertices, UVs, and normals in big-endian format
                    vertices_data = struct.pack(f'>{len(vertices)}f', *vertices)
                    uvs_data = struct.pack(f'>{len(uvs)}f', *uvs)
                    normals_data = struct.pack(f'>{len(normals)}f', *normals)
                    f.write(vertices_data)
                    f.write(uvs_data)
                    f.write(normals_data)
                    print(f"  Vertices: {vertices}")
                    print(f"  UVs: {uvs}")
                    print(f"  Normals: {normals}")

                    # Clean up the temporary mesh
                    obj.to_mesh_clear()

        return {'FINISHED'}

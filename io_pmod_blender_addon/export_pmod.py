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
        has_armature = any(obj.type == 'ARMATURE' for obj in scene.objects)
        has_nodes = any(obj.type != 'ARMATURE' for obj in scene.objects)

        # Set flags based on the presence of armatures and nodes
        flags = 0
        if has_armature:
            flags |= 0b00000010  # Armature present
        if has_nodes:
            flags |= 0b00000001  # Nodes present

        # Write version and flags in big-endian format
        f.write(struct.pack('>BBB', 1, 0, flags))

        # Determine number of nodes (excluding armatures)
        num_nodes = len([obj for obj in scene.objects if obj.type != 'ARMATURE'])

        # Armature related information
        if has_armature:
            armature = next((obj for obj in scene.objects if obj.type == 'ARMATURE'), None)
            bones = armature.data.bones
            # Write the number of joints and nodes in big-endian format
            f.write(struct.pack('>BB', len(bones), num_nodes))
            # Write skeleton name
            f.write(armature.name.encode() + b'\0')

            for bone in bones:
                f.write(bone.name.encode() + b'\0')
                parent_name = bone.parent.name.encode() + b'\0' if bone.parent else b'root\0'
                f.write(parent_name)
                # Inverse bind matrix in big-endian format
                matrix = bone.matrix_local.inverted()
                f.write(struct.pack('>16f', *matrix))  # Note '>16f' for big-endian

        else:
            # Write zero for number of joints and number of nodes in big-endian format
            f.write(struct.pack('>BB', 0, num_nodes))

        # Iterate through each object and export nodes that are not armatures
        for obj in scene.objects:
            if obj.type != 'ARMATURE':
                f.write(obj.name.encode() + b'\0')
                parent_name = obj.parent.name.encode() + b'\0' if obj.parent and obj.parent.type != 'ARMATURE' else b'root\0'
                f.write(parent_name)
                loc, rot, scale = obj.location, obj.rotation_quaternion, obj.scale
                f.write(struct.pack('>3f', *loc))
                f.write(struct.pack('>4f', *rot))
                f.write(struct.pack('>3f', *scale))

                # Determine number of meshes contained in this node
                num_meshes = 1
                f.write(struct.pack('>I', num_meshes))  # Write number of meshes in big-endian format

                # Export meshes
                if obj.type == 'MESH':
                    mesh = obj.to_mesh(preserve_all_data_layers=True, depsgraph=context.evaluated_depsgraph_get())
                    mesh.calc_loop_triangles()

                    # Write the mesh name
                    f.write(obj.data.name.encode() + b'\0')

                    # Collect mesh data
                    num_indices = len(mesh.loop_triangles) * 3
                    num_vertices = len(mesh.vertices)
                    # Pack indices and vertices count in big-endian format
                    f.write(struct.pack('>III', num_indices, num_vertices, 0))  # Number of indices, vertices, joints (assuming no skeletal data here)

                    # Gather indices
                    indices = []
                    for tri in mesh.loop_triangles:
                        indices.extend(tri.vertices)
                    # Pack indices in big-endian format
                    f.write(struct.pack(f'>{num_indices}I', *indices))

                    # Gather and write vertices, normals, and UVs
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

                    # Pack UVs, vertices, and normals in big-endian format
                    f.write(struct.pack(f'>{len(uvs)}f', *uvs))
                    f.write(struct.pack(f'>{len(vertices)}f', *vertices))
                    f.write(struct.pack(f'>{len(normals)}f', *normals))

                    # Clean up the temporary mesh
                    obj.to_mesh_clear()

        return {'FINISHED'}

package net.modularmods.protogl.loader;

import net.modularmods.protogl.gl.Mesh;
import net.modularmods.protogl.gl.MeshData;
import net.modularmods.protogl.loader.data.JointData;
import net.modularmods.protogl.loader.data.NodeData;
import net.modularmods.protogl.loader.data.ModelData;
import net.modularmods.protogl.utils.IOUtils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/**
 * Handles loading of PModel data from a file, supporting both skeletal animations and static models.
 */
public class PModelLoader {

    /**
     * Loads a PMod file and constructs a PModel object based on its contents.
     * @param classLoader The class loader to use to load the file.
     * @param file The path to the PMod file to load.
     * @return A fully constructed PModel object.
     */
    public static ModelData loadPMod(ClassLoader classLoader, String file) {
        try (DataInputStream dis = new DataInputStream(Objects.requireNonNull(classLoader.getResourceAsStream(file)))) {
            // Check the magic number to confirm it's a valid PMOD file
            if (!IOUtils.readMagicNumber(dis).equals("PMOD")) {
                throw new IOException("Invalid file format");
            }

            // Read file version and flags
            byte majorVersion = IOUtils.readByte(dis, "Major version");
            byte minorVersion = IOUtils.readByte(dis, "Minor version");
            byte flags = IOUtils.readByte(dis, "Flags");

            // Determine whether the model includes armature or nodes
            boolean hasArmature = (flags & 0x02) != 0;
            boolean hasNodes = (flags & 0x01) != 0;

            // Read counts of joints, nodes, and materials
            int jointsCount = IOUtils.readByte(dis, "Number of joints");
            int nodesCount = IOUtils.readByte(dis, "Number of nodes");

            JointData[] jointData = null;
            String skeletonName = null;
            if (hasArmature) {
                jointData = new JointData[jointsCount];
                skeletonName = IOUtils.readString(dis, "Skeleton name");
                for (int i = 0; i < jointsCount; i++) {
                    String jointName = IOUtils.readString(dis, "Joint name");
                    String parentJointName = IOUtils.readString(dis, "      Joint parent");
                    float[] invBindMatrix = IOUtils.readFloats(dis, 16, "           InvBindMatrix");
                    jointData[i] = new JointData(jointName, parentJointName, invBindMatrix);
                }
            }

            NodeData[] nodeData = new NodeData[nodesCount];

            if (hasNodes) {
                for (int i = 0; i < nodesCount; i++) {
                    String nodeName = IOUtils.readString(dis, "Node name");
                    String parentNodeName = IOUtils.readString(dis, "      Parent name");
                    float[] translation = IOUtils.readFloats(dis, 3, "           Translation");
                    float[] rotation = IOUtils.readFloats(dis, 4, "           Rotation");
                    float[] scale = IOUtils.readFloats(dis, 3, "           Scale");
                    int numMeshes = IOUtils.readInt(dis, "           Number of meshes");

                    MeshData meshData = null;

                    if (numMeshes > 0) {
                        for (int j = 0; j < numMeshes; j++) {
                            String meshName = IOUtils.readString(dis, "               Mesh name");
                            int numIndices = IOUtils.readInt(dis, "               Number of indices");
                            int numVertices = IOUtils.readInt(dis, "               Number of vertices");
                            int numJoints = IOUtils.readInt(dis, "               Number of joints");
                            int numWeights = IOUtils.readInt(dis, "               Number of weights");

                            int[] indices = IOUtils.readInts(dis, numIndices, "               Indices");
                            float[] vertices = IOUtils.readFloats(dis, numVertices * 3, "               Vertices");
                            float[] uvs = IOUtils.readFloats(dis, numVertices * 2, "               UVs");
                            float[] normals = IOUtils.readFloats(dis, numVertices * 3, "               Normals");

                            if (numJoints > 0) {
                                int[] joints = IOUtils.readInts(dis, numJoints, "               Joints");

                                // Conversion from int to float for joints is a temporary solution
                                float[] jointsFloat = new float[joints.length];
                                for (int k = 0; k < joints.length; k++) {
                                    jointsFloat[k] = (float) joints[k];
                                }

                                float[] weights = IOUtils.readFloats(dis, numWeights, "               Weights");

                                // Create MeshData for skinned meshes
                                meshData = new MeshData(vertices, uvs, normals, indices, jointsFloat, weights);
                            } else {
                                // Create MeshData for static meshes
                                meshData = new MeshData(vertices, uvs, normals, indices);
                            }
                        }
                    }
                    nodeData[i] = new NodeData(nodeName, parentNodeName, translation, rotation, scale, meshData);
                }
            }

            if (hasArmature) {
                return new ModelData(nodeData, skeletonName, jointData);
            } else {
                return new ModelData(nodeData);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("IO error reading file: " + e.getMessage(), e);
        }
    }
}

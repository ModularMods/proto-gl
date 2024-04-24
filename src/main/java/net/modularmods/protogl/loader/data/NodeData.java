package net.modularmods.protogl.loader.data;

import lombok.Getter;
import net.modularmods.protogl.gl.Mesh;
import net.modularmods.protogl.gl.MeshData;

/**
 * Represents a node in a scene graph or hierarchy, typically used in 3D applications to manage objects in a scene.
 * Each node can have a transformation, a link to a parent node, and possibly a Mesh attached to it for rendering.
 */
@Getter
public class NodeData {

    private String nodeName;        // Name of the node
    private String parentNodeName;  // Name of the parent node
    private float[] translation;    // Translation vector of the node
    private float[] rotation;       // Rotation quaternion of the node
    private float[] scale;          // Scaling vector of the node
    private MeshData meshData;              // Mesh object attached to the node (optional)

    /**
     * Constructs a NodeData object, encapsulating the transformation properties and optional mesh data.
     * @param nodeName Name of the node.
     * @param parentNodeName Name of the node's parent.
     * @param translation 3D translation vector.
     * @param rotation 3D rotation represented as a quaternion.
     * @param scale 3D scaling vector.
     * @param meshData Mesh if the node displays geometry.
     */
    public NodeData(String nodeName, String parentNodeName, float[] translation, float[] rotation, float[] scale, MeshData meshData) {
        this.nodeName = nodeName;
        this.parentNodeName = parentNodeName;
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
        this.meshData = meshData;
    }
}

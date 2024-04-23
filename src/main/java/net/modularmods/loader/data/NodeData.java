package net.modularmods.loader.data;

import lombok.Getter;
import net.modularmods.gl.Mesh;

/**
 * Represents a node in a scene graph or hierarchy, typically used in 3D applications to manage objects in a scene.
 * Each node can have a transformation, a link to a parent node, and possibly a Mesh attached to it for rendering.
 */
@Getter
public class NodeData {

    private String nodeName;        // Name of the node
    private String parentNodeName;  // Name of the parent node
    private String materialName;    // Name of the material associated with the node's mesh
    private float[] translation;    // Translation vector of the node
    private float[] rotation;       // Rotation quaternion of the node
    private float[] scale;          // Scaling vector of the node
    private Mesh mesh;              // Mesh object attached to the node (optional)

    /**
     * Constructs a NodeData object, encapsulating the transformation properties and optional mesh data.
     * @param nodeName Name of the node.
     * @param parentNodeName Name of the node's parent.
     * @param materialName Name of the material used if the node has a Mesh.
     * @param translation 3D translation vector.
     * @param rotation 3D rotation represented as a quaternion.
     * @param scale 3D scaling vector.
     * @param mesh Mesh instance if the node displays geometry.
     */
    public NodeData(String nodeName, String parentNodeName, String materialName, float[] translation, float[] rotation, float[] scale, Mesh mesh) {
        this.nodeName = nodeName;
        this.parentNodeName = parentNodeName;
        this.materialName = materialName;
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
        this.mesh = mesh;
    }
}

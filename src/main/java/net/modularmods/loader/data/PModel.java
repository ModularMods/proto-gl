package net.modularmods.loader.data;

import lombok.Getter;

/**
 * Represents a 3D model that can consist of multiple nodes and potentially include an armature for skeletal animation.
 */
@Getter
public class PModel {

    private NodeData[] nodeData;  // Array of NodeData objects representing different parts of the model
    private boolean hasArmature;  // Flag indicating whether the model includes an armature for skeletal animation
    private JointData[] jointData; // Array of JointData objects for the model's skeletal structure, if applicable
    private String skeletonName;  // Name of the skeleton, if the model is rigged

    /**
     * Constructs a PModel with nodes only, primarily used for static models without skeletal animation.
     * @param nodeData Array of NodeData objects defining the structure and geometry of the model.
     */
    public PModel(NodeData[] nodeData) {
        this.nodeData = nodeData;
        this.hasArmature = false;  // Default to no armature, indicating a static model
        this.jointData = null;     // No joints data since the model is static
        this.skeletonName = null;  // No skeleton associated with a static model
    }

    /**
     * Constructs a PModel with nodes and an armature, used for models that include skeletal animation.
     * @param nodeData Array of NodeData objects defining the structure and geometry of the model.
     * @param skeletonName Name of the skeleton associated with this model.
     * @param jointData Array of JointData objects detailing the joints in the skeleton.
     */
    public PModel(NodeData[] nodeData, String skeletonName, JointData[] jointData) {
        this.nodeData = nodeData;
        this.hasArmature = true;   // Indicate that this model includes an armature
        this.jointData = jointData; // Set the joint data for skeletal animation
        this.skeletonName = skeletonName; // Set the name of the skeleton
    }
}

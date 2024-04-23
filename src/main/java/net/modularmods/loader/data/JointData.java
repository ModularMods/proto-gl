package net.modularmods.loader.data;

import lombok.Getter;

/**
 * Represents joint data for a skeleton in skeletal animation.
 * This includes the joint's name, its parent's name, and the inverse bind matrix necessary for animations.
 */
@Getter
public class JointData {

    private String jointName;          // Name of the joint
    private String parentJointName;    // Name of the parent joint
    private float[] invBindMatrix;     // Inverse bind matrix used for transforming vertex data to this joint's space

    /**
     * Constructs a JointData object to hold data related to a single joint in a skeletal system.
     * @param jointName Name of the joint.
     * @param parentJointName Name of the parent joint. Can be null if this is a root joint.
     * @param invBindMatrix Inverse bind matrix for the joint, typically used in skinning calculations.
     */
    public JointData(String jointName, String parentJointName, float[] invBindMatrix) {
        this.jointName = jointName;
        this.parentJointName = parentJointName;
        this.invBindMatrix = invBindMatrix;
    }
}

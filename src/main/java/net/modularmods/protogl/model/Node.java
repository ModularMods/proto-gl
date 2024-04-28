package net.modularmods.protogl.model;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Node {

    public String nodeName;

    // Getters and setters for the new fields
    @Getter
    public List<Node> children;

    public String parentName;

    @Setter
    @Getter
    public Node parent;

    protected Vector3f position;
    protected Quaternionf rotation;
    protected Vector3f scale;

    @Getter
    protected Matrix4f modelMatrix;

    private boolean isTransformDirty = true;

    public Node(String nodeName) {
        this.nodeName = nodeName;
        this.children = new ArrayList<>();
        this.position = new Vector3f(0f, 0f, 0f);
        this.rotation = new Quaternionf();
        this.scale = new Vector3f(1f, 1f, 1f);
        this.modelMatrix = new Matrix4f();
        updateModelViewMatrix();
    }

    // Method to add a child node
    public void addChild(Node child) {
        children.add(child);
        child.parent = this;
    }

    /**
     * Recursively collects all child nodes.
     *
     * @return A list of all child nodes.
     */
    public List<Node> getAllChildren() {
        List<Node> allChildren = new ArrayList<>();
        getAllChildrenRecursive(this, allChildren);
        return allChildren;
    }

    /**
     * Helper method to recursively traverse and collect child nodes.
     *
     * @param node        The current node to process.
     * @param allChildren The list where child nodes are collected.
     */
    private void getAllChildrenRecursive(Node node, List<Node> allChildren) {
        for (Node child : node.children) {
            allChildren.add(child);
            getAllChildrenRecursive(child, allChildren);
        }
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        isTransformDirty = true;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.rotationXYZ(x, y, z);
        isTransformDirty = true;
    }

    public void setRotation(float x, float y, float z, float w) {
        this.rotation = new Quaternionf(x, y, z, w);
    }

    public void setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
        isTransformDirty = true;
    }

    public void rotate(float angle, float x, float y, float z) {
        Quaternionf deltaRotation = new Quaternionf().rotationAxis(angle, x, y, z);
        rotation.mul(deltaRotation);
        isTransformDirty = true;
    }

    protected void updateModelViewMatrix() {
        if (isTransformDirty) {
            this.modelMatrix.identity()
                    .translate(position)
                    .rotate(rotation)
                    .scale(scale);
            isTransformDirty = false;
        }
    }

    public void render(Matrix4f parentMatrix) {
        updateModelViewMatrix(); // Only updates if transform is dirty
        Matrix4f transform = new Matrix4f(parentMatrix).mul(this.modelMatrix);

        for (Node child : this.children) {
            child.render(transform); // Render child nodes
        }
    }

}
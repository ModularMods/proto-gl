package net.modularmods.protogl.model;

import net.modularmods.protogl.gl.Mesh;
import org.joml.Matrix4f;

public class MeshNode extends Node {

    public Mesh mesh;

    public MeshNode(String nodeName) {
        super(nodeName);
    }

    @Override
    public void render(Matrix4f parentMatrix) {
        updateModelViewMatrix(); // Only updates if transform is dirty
        Matrix4f transform = new Matrix4f(parentMatrix).mul(this.modelMatrix);

        if (this.mesh != null) {
            this.mesh.render(); // Render the mesh
        }

        for (Node child : this.children) {
            child.render(transform); // Render child nodes
        }
    }
}

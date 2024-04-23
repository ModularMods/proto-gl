package net.modularmods.protogl.gl;

import lombok.Getter;
import net.modularmods.protogl.gl.buffers.VertexArrayObject;
import net.modularmods.protogl.render.IRenderable;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import static org.lwjgl.opengl.GL11.*;

/**
 * Represents a mesh with optional skinning capabilities (for animations), encapsulating a VertexArrayObject (VAO)
 * and methods for rendering and managing mesh data.
 */
public class Mesh implements IRenderable {

	private final VertexArrayObject vao; // Encapsulates the vertex array object handling all the vertex data

	@Getter
	private final int boneTransformsUBO; // Uniform Buffer Object (UBO) for bone transformations if the mesh is skinned

	public boolean isSkinned; // Flag indicating whether the mesh has skinning enabled

	public int MAX_BONES = 20;

	/**
	 * Constructs a Mesh with a specified VAO and skinning status.
	 * @param vao VertexArrayObject containing all vertex and attribute data for this mesh.
	 * @param isSkinned Boolean indicating if the mesh is skinned.
	 */
	public Mesh(VertexArrayObject vao, boolean isSkinned) {
		this.vao = vao;
		this.isSkinned = isSkinned;

		// Initialize the UBO for bone transformations if the mesh is skinned
		if (isSkinned) {
			this.boneTransformsUBO = GL15.glGenBuffers(); // Generate a buffer
			GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, boneTransformsUBO); // Bind the buffer
			// Allocate buffer memory and initialize it for dynamic data updates
			GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, (long) MAX_BONES * 16 * Float.BYTES, GL31.GL_DYNAMIC_DRAW);
			GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0); // Unbind the buffer
		} else {
			this.boneTransformsUBO = -1; // Set to -1 if not skinned
		}
	}

	/**
	 * Getter for the VertexArrayObject.
	 * @return the associated VertexArrayObject.
	 */
	public VertexArrayObject getVAO() {
		return vao;
	}

	/**
	 * Cleans up resources, specifically the VAO and potentially the bone transformation UBO.
	 */
	public void cleanup() {
		vao.cleanup(); // Clean up the VAO
		// If skinned, delete the UBO
		if (isSkinned) {
			GL15.glDeleteBuffers(boneTransformsUBO);
		}
	}

	/**
	 * Factory method to create a Mesh from MeshData.
	 * @param data MeshData containing all necessary data to configure the VAO and check for skinning.
	 * @return a new Mesh object.
	 */
	public static Mesh load(MeshData data) {
		VertexArrayObject vao = new VertexArrayObject();
		vao.setActiveAttributeCount(data.getActiveAttributeCount()); // Configure attributes count

		int[] indices = data.getIndices();

		if (indices != null) {
			vao.storeData(data.getVertexCount(), indices, data.getAttributes()); // Store indexed data
		} else {
			vao.storeData(data.getVertexCount(), data.getAttributes()); // Store non-indexed data
		}

		return new Mesh(vao, data.getJoints() != null); // Return new Mesh, isSkinned determined by presence of joints
	}

	/**
	 * Updates the UBO for bone transformations with new data.
	 * @param boneTransforms Array of floats representing the bone transformations.
	 */
	public void updateBoneTransforms(float[] boneTransforms) {
		if (isSkinned) {
			GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, boneTransformsUBO); // Bind the buffer
			GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, boneTransforms); // Update buffer data
			GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0); // Unbind the buffer
		}
	}

	/**
	 * Implements rendering of the mesh according to the IRenderable interface. This includes binding the VAO,
	 * enabling necessary attributes, and handling the draw call.
	 */
	@Override
	public void render() {
		int activeAttributeCount = vao.getActiveAttributeCount(); // Store the count in a local variable

		vao.bind(); // Bind the VAO

		// If the mesh is skinned, bind the bone transforms UBO
		if (isSkinned) {
			GL31.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 0, boneTransformsUBO); // Bind UBO at binding point 0
		}

		// Bind the required attributes
		for (int i = 0; i < activeAttributeCount; i++) {
			vao.bindAttribute(i);
		}

		glDrawElements(GL_TRIANGLES, vao.getElementCount(), GL_UNSIGNED_INT, 0); // Issue the draw call

		// Unbind the attributes
		for (int i = 0; i < activeAttributeCount; i++) {
			vao.unbindAttribute(i);
		}

		// Unbind the bone transforms UBO if the mesh is skinned
		if (isSkinned) {
			GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 0, 0); // Unbind UBO
		}

		vao.unbind(); // Unbind the VAO
	}
}

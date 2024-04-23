package net.modularmods.protogl.gl.buffers;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.GL30;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages a collection of VertexBufferObjects and provides methods to handle vertex array objects (VAOs)
 * in OpenGL. This includes creating, binding, unbinding, and cleaning up VAOs and their associated VBOs.
 */
public class VertexArrayObject {
	private final int id;  // Unique identifier for the OpenGL Vertex Array Object

	private List<VertexBufferObject> vbos = new ArrayList<>(); // List to store all associated Vertex Buffer Objects (VBOs)
	private VertexBufferObject indexVBO; // Special VBO for index data, used in indexed drawing

    /**
     * -- GETTER --
     *  Returns the count of elements this VertexArrayObject can draw.
     *
     * @return the number of elements
     */
    @Getter
    private int elementCount; // Tracks the number of elements (vertices or indices) that this VAO can draw

	@Getter
	@Setter
	private int activeAttributeCount; // Keeps track of the number of active vertex attributes

	/**
	 * Constructor that creates a new Vertex Array Object (VAO) and generates its unique ID via OpenGL.
	 */
	public VertexArrayObject() {
		id = GL30.glGenVertexArrays();
	}

    /**
	 * Binds this VertexArrayObject so that it becomes the current VAO in use.
	 */
	public void bind() {
		GL30.glBindVertexArray(id);
	}

	/**
	 * Enables a vertex attribute.
	 * @param attribute the index of the attribute to enable
	 */
	public void bindAttribute(int attribute) {
		GL30.glEnableVertexAttribArray(attribute);
	}

	/**
	 * Disables a vertex attribute.
	 * @param attribute the index of the attribute to disable
	 */
	public void unbindAttribute(int attribute) {
		GL30.glDisableVertexAttribArray(attribute);
	}

	/**
	 * Unbinds the current VertexArrayObject, making no VAO currently bound.
	 */
	public void unbind() {
		GL30.glBindVertexArray(0);
	}

	/**
	 * Cleans up the resources by deleting all associated VBOs and the VAO itself.
	 */
	public void cleanup() {
		vbos.forEach(VertexBufferObject::cleanup); // Cleanup all VBOs
		indexVBO.cleanup(); // Cleanup the index VBO specifically
		GL30.glDeleteVertexArrays(id); // Finally, delete the VAO
	}

	/**
	 * Stores vertex data along with index data for indexed drawing.
	 * @param vertexCount the count of vertices
	 * @param indices the indices used for indexed drawing
	 * @param data the vertex data arrays corresponding to various attributes (positions, colors, etc.)
	 */
	public void storeData(int vertexCount, int[] indices, float[]... data) {
		bind(); // Bind the VAO for data storage
		storeVertexData(vertexCount, data); // Store the vertex data
		storeIndices(indices); // Store the indices data
		unbind(); // Unbind the VAO

		elementCount = indices.length; // Update the element count to the number of indices
	}

	/**
	 * Stores only vertex data without index data.
	 * @param vertexCount the count of vertices
	 * @param data the vertex data arrays for various attributes
	 */
	public void storeData(int vertexCount, float[]... data) {
		bind(); // Bind the VAO
		storeVertexData(vertexCount, data); // Store the vertex data
		unbind(); // Unbind the VAO

		elementCount = vertexCount; // Set the element count to the number of vertices
	}

	/**
	 * Stores vertex data into the VAO by creating VBOs for each attribute and configuring them.
	 * @param vertexCount the number of vertices
	 * @param data the arrays of vertex data for different attributes
	 */
	private void storeVertexData(int vertexCount, float[]... data) {
		int[] attributeSizes = {3, 2, 3, 4, 4}; // Example sizes: position=3, texture coordinates=2, normals=3, joint indices=4, weights=4

		for (int i = 0; i < data.length; i++) {
			storeDataInAttributeList(i, attributeSizes[i], data[i]);
		}
	}

	/**
	 * Creates and binds an index VBO for storing indices, which are used for indexed drawing.
	 * @param indices the array of indices
	 */
	private void storeIndices(int[] indices) {
		indexVBO = new VertexBufferObject(GL30.GL_ELEMENT_ARRAY_BUFFER);
		indexVBO.bind();
		indexVBO.storeData(indices);
	}

	/**
	 * Stores array data into a specific vertex attribute list, by creating a VBO and configuring the attribute pointer.
	 * @param attributeId the attribute index
	 * @param attributeSize the number of components in the attribute
	 * @param data the data array
	 */
	private void storeDataInAttributeList(int attributeId, int attributeSize, float[] data) {
		VertexBufferObject vbo = new VertexBufferObject(GL30.GL_ARRAY_BUFFER);
		vbos.add(vbo);
		vbo.bind();

		vbo.storeData(data);
		GL30.glVertexAttribPointer(attributeId, attributeSize, GL30.GL_FLOAT, false, 0, 0);

		vbo.unbind();
	}
}

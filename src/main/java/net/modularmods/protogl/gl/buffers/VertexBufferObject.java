package net.modularmods.protogl.gl.buffers;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Represents an OpenGL Vertex Buffer Object (VBO) that stores vertex data for rendering. This class provides
 * methods to handle the creation, binding, data storage, and deletion of buffer objects.
 */
public class VertexBufferObject {
	private final int id; // Unique identifier for the OpenGL buffer
	private final int type; // Type of buffer (e.g., GL_ARRAY_BUFFER, GL_ELEMENT_ARRAY_BUFFER)

	/**
	 * Constructs a VertexBufferObject with a specific buffer type.
	 * @param type The type of buffer to be created (e.g., GL_ARRAY_BUFFER, GL_ELEMENT_ARRAY_BUFFER).
	 */
	public VertexBufferObject(int type) {
		this.id = GL30.glGenBuffers(); // Generate a new buffer object
		this.type = type; // Set the buffer type
	}

	/**
	 * Binds this buffer as the current buffer of its type.
	 */
	public void bind() {
		GL30.glBindBuffer(type, id); // Bind the buffer with the specified type
	}

	/**
	 * Unbinds any buffer of this type.
	 */
	public void unbind() {
		GL30.glBindBuffer(type, 0); // Unbind the buffer by binding to zero
	}

	/**
	 * Stores data in this buffer from a FloatBuffer.
	 * @param data The FloatBuffer containing the data to store.
	 */
	public void storeData(FloatBuffer data) {
		GL30.glBufferData(type, data, GL30.GL_STATIC_DRAW); // Store the buffer data statically
	}

	/**
	 * Stores data in this buffer from a primitive float array.
	 * @param data The array of floats to store in the buffer.
	 */
	public void storeData(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length); // Create a FloatBuffer
		buffer.put(data); // Put data into the buffer
		buffer.flip(); // Flip the buffer for reading from the beginning

		storeData(buffer); // Store the data from the FloatBuffer into this buffer
	}

	/**
	 * Stores data in this buffer from an IntBuffer.
	 * @param data The IntBuffer containing the data to store.
	 */
	public void storeData(IntBuffer data) {
		GL30.glBufferData(type, data, GL30.GL_STATIC_DRAW); // Store the buffer data statically
	}

	/**
	 * Stores data in this buffer from a primitive int array.
	 * @param data The array of integers to store in the buffer.
	 */
	public void storeData(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length); // Create an IntBuffer
		buffer.put(data); // Put data into the buffer
		buffer.flip(); // Flip the buffer for reading from the beginning

		storeData(buffer); // Store the data from the IntBuffer into this buffer
	}

	/**
	 * Cleans up the buffer by deleting it from the GPU's memory.
	 */
	public void cleanup() {
		GL30.glDeleteBuffers(id); // Delete the buffer
	}
}

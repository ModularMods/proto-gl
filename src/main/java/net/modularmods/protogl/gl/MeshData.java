package net.modularmods.protogl.gl;

import lombok.Getter;
import lombok.Setter;

/**
 * Encapsulates all necessary data for constructing a mesh, including vertices, texture coordinates (UVs),
 * normals, joint indices for skeletal animation, and weights. Provides various constructors to handle
 * different types of mesh data based on what attributes are needed.
 */
public class MeshData {
	@Getter
	private float[] vertices;  // Array of vertex positions
	@Getter
	private float[] uvs;       // Array of UV coordinates for texturing
	@Getter
	private float[] normals;   // Array of normals for lighting calculations
	@Setter @Getter
	private float[] joints;    // Array of joint indices for skeletal animation
	@Setter @Getter
	float[] weights;           // Array of weights for skeletal animation influence
	@Getter
	private int[] indices;     // Array of indices defining how vertices are connected

	@Getter
	private int activeAttributeCount = 0;  // Count of active attributes provided in the data

	/**
	 * Constructor for fully animated mesh data with vertices, UVs, normals, indices, joints, and weights.
	 * @param vertices Array of vertex positions.
	 * @param uvs Array of UV coordinates.
	 * @param normals Array of normal vectors.
	 * @param indices Array of indices.
	 * @param joints Array of joint indices for skeletal animation.
	 * @param weights Array of weights for skeletal animation.
	 */
	public MeshData(float[] vertices, float[] uvs, float[] normals, int[] indices, float[] joints, float[] weights) {
		this.vertices = vertices;
		this.uvs = uvs;
		this.normals = normals;
		this.joints = joints;
		this.weights = weights;
		this.indices = indices;

		activeAttributeCount = 5;  // Attributes: vertices, UVs, normals, joints, weights
	}

	/**
	 * Constructor for mesh data with vertices, UVs, normals, and indices.
	 * @param vertices Array of vertex positions.
	 * @param uvs Array of UV coordinates.
	 * @param normals Array of normal vectors.
	 * @param indices Array of indices.
	 */
	public MeshData(float[] vertices, float[] uvs, float[] normals, int[] indices) {
		this.vertices = vertices;
		this.uvs = uvs;
		this.normals = normals;
		this.indices = indices;

		activeAttributeCount = 3;  // Attributes: vertices, UVs, normals
	}

	/**
	 * Constructor for mesh data with vertices, UVs, and indices.
	 * @param vertices Array of vertex positions.
	 * @param uvs Array of UV coordinates.
	 * @param indices Array of indices.
	 */
	public MeshData(float[] vertices, float[] uvs, int[] indices) {
		this.vertices = vertices;
		this.uvs = uvs;
		this.indices = indices;

		activeAttributeCount = 2;  // Attributes: vertices, UVs
	}

	/**
	 * Constructor for mesh data with vertices and indices.
	 * @param vertices Array of vertex positions.
	 * @param indices Array of indices.
	 */
	public MeshData(float[] vertices, int[] indices) {
		this.vertices = vertices;
		this.indices = indices;

		activeAttributeCount = 1;  // Attribute: vertices
	}

	/**
	 * Constructor for mesh data with only vertices.
	 * @param vertices Array of vertex positions.
	 */
	public MeshData(float[] vertices) {
		this.vertices = vertices;

		activeAttributeCount = 1;  // Attribute: vertices
	}

	/**
	 * Computes and returns the number of vertices based on the array length.
	 * @return the count of vertices (each vertex represented by three floats for x, y, z coordinates).
	 */
	public int getVertexCount() {
		return vertices.length / 3;
	}

	/**
	 * Collects and returns all attribute arrays based on what is available.
	 * @return a two-dimensional array of floats, where each sub-array represents an attribute.
	 */
	public float[][] getAttributes() {
		float[][] attributes = new float[activeAttributeCount][];

		int curIndex = 0;
		if (vertices != null) attributes[curIndex++] = vertices;
		if (uvs != null) attributes[curIndex++] = uvs;
		if (normals != null) attributes[curIndex++] = normals;
		if (joints != null) attributes[curIndex++] = joints;
		if (weights != null) attributes[curIndex++] = weights;

		return attributes;  // Returns the organized array of attributes for processing
	}
}

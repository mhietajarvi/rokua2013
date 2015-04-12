package renderer.object;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import renderer.shader.Attribute;

public class DrawableBase implements Drawable {

	int drawMode = -1;
	int vertexCount = 0;
	int vao = -1;
	int vbo = -1;
	int ibo = -1;
	int indexCount = 0;

	private static FloatBuffer put(Object floats, FloatBuffer buf) {
		if (floats instanceof float[][]) {
			for (float[] value : (float[][]) floats) {
				buf.put(value);
			}
		} else if (floats instanceof float[][][]) {
			for (float[][] subArray : (float[][][]) floats) {
				for (float[] value : subArray) {
					buf.put(value);
				}
			}
		} else {
			throw new IllegalArgumentException("Every data pair must end with " + float[][][].class + " or "
					+ float[][].class);
		}
		return buf;
	}

	private static int attribLen(Object floats) {
		if (floats instanceof float[][]) {
			return ((float[][]) floats)[0].length;
		} else if (floats instanceof float[][][]) {
			return ((float[][][]) floats)[0][0].length;
		}
		throw new IllegalArgumentException("Every data pair must end with " + float[][][].class + " or "
				+ float[][].class);
	}

	private static int vertexCount(Object floats) {
		if (floats instanceof float[][]) {
			return ((float[][]) floats).length;
		} else if (floats instanceof float[][][]) {
			int len = 0;
			for (float[][] subArray : (float[][][]) floats) {
				len += subArray.length;
			}
			return len;
		}
		throw new IllegalArgumentException("Every data pair must end with " + float[][][].class + " or "
				+ float[][].class);
	}

	/**
	 * @param drawMode
	 *            e.g, GL11.GL_TRIANGLES, GL11.GL_TRIANGLE_STRIP, GL11.GL_TRIANGLE_FAN
	 * @param indices
	 *            vertex indices
	 * @param attribs
	 *            Sequence of (Attribute, float[][]) or (Attribute, float[][][])
	 */
	public void init(int drawMode, short[] indices, Object... attribs) {

		indexCount = indices.length;
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);

		ShortBuffer shortBuffer = BufferUtils.createShortBuffer(indices.length);
		shortBuffer.put(indices);
		shortBuffer.flip();
		ibo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, shortBuffer, GL15.GL_STREAM_DRAW);

		init(drawMode, attribs);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	/**
	 * @param drawMode
	 *            e.g, GL11.GL_TRIANGLES, GL11.GL_TRIANGLE_STRIP, GL11.GL_TRIANGLE_FAN
	 * @param attribs
	 *            Sequence of (Attribute, float[][]) or (Attribute, float[][][])
	 */
	public void init(int drawMode, Object... attribs) {

		this.drawMode = drawMode;
		vertexCount = -1;
		if ((attribs.length & 1) != 0) {
			throw new IllegalArgumentException("attribs array must have even number of elements");
		}
		int attribCount = attribs.length / 2;

		int sz = 0;
		for (int i = 0; i < attribCount; i++) {
			int count = vertexCount(attribs[2 * i + 1]);
			if (vertexCount < 0) {
				vertexCount = count;
			} else if (vertexCount != count) {
				throw new RuntimeException(
						"all attribute arrays must specify same number of attribute values (= number of vertices)");
			}
			sz += count * attribLen(attribs[2 * i + 1]);
		}

		FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(sz);

		int[] offsets = new int[attribCount];
		for (int i = 0; i < attribCount; i++) {
			offsets[i] = floatBuffer.position();
			put(attribs[2 * i + 1], floatBuffer);
		}
		floatBuffer.flip();

		if (vao < 0) {
			vao = GL30.glGenVertexArrays();
			GL30.glBindVertexArray(vao);
		}

		// create buffer and upload all vertex attributes
		vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatBuffer, GL15.GL_STREAM_DRAW);

		for (int i = 0; i < attribCount; i++) {
			int index = ((Attribute) attribs[2 * i]).ordinal();
			GL20.glVertexAttribPointer(index, attribLen(attribs[2 * i + 1]), GL11.GL_FLOAT, false, 0, offsets[i] * 4);
			GL20.glEnableVertexAttribArray(index);
		}

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	@Override
	public void destroy() {
		GL30.glDeleteVertexArrays(vao);
		GL15.glDeleteBuffers(vbo);
	}

	@Override
	public void draw() {
		GL30.glBindVertexArray(vao);
		if (indexCount > 0) {
			GL11.glDrawElements(drawMode, indexCount, GL11.GL_UNSIGNED_SHORT, 0);
		} else {
			GL11.glDrawArrays(drawMode, 0, vertexCount);
		}
	}

	@Override
	public void drawInstanced(int count) {
		GL30.glBindVertexArray(vao);
		if (indexCount > 0) {
			GL31.glDrawElementsInstanced(drawMode, indexCount, GL11.GL_UNSIGNED_SHORT, 0, count);
		} else {
			GL31.glDrawArraysInstanced(drawMode, 0, vertexCount, count);
		}
	}
}

package test;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;


public class DrawableBase implements Drawable {

	int vao;
	int vbo;

	private int vertexCount;

	public void init(Attribute[] types, float[][]... values) {

		vertexCount = values[0].length;
		
		int sz = 0;
		for (float[][] d : values) {
			if (d.length != vertexCount) {
				throw new RuntimeException("all attributes must be defined for all vertices");
			}
			sz += d.length * d[0].length;
		}
		FloatBuffer attribs = BufferUtils.createFloatBuffer(sz);

		int[] offsets = new int[values.length];
		for (int i = 0; i < types.length; i++) {
			offsets[i] = attribs.position();
			Util.put(values[i], attribs);
		}
		attribs.flip();

		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		// create buffer and upload all vertex attributes
		vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, attribs, GL15.GL_STREAM_DRAW);
		
		for (int i = 0; i < types.length; i++) {
			GL20.glVertexAttribPointer(types[i].ordinal(), values[i][0].length, GL11.GL_FLOAT, false, 0, offsets[i]*4);
			GL20.glEnableVertexAttribArray(types[i].ordinal());
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
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, vertexCount);
	}

	@Override
	public void drawInstanced(int count) {
		GL30.glBindVertexArray(vao);
		GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, vertexCount, count);
	}
}

package renderer.object;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import renderer.shader.Attribute;

public class Rect implements Drawable {

	int vao;
	int vbo;

	public Rect(float width, float height, float z) {

		float x = width / 2;
		float y = height / 2;

		FloatBuffer b = BufferUtils.createFloatBuffer(4 * 3);
		b.put(new float[] { x, y, z });
		b.put(new float[] { x, -y, z });
		b.put(new float[] { -x, y, z });
		b.put(new float[] { -x, -y, z });
		b.flip();

		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		// create buffer and upload all vertex attributes
		vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, b, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(Attribute.POSITION_3F.ordinal(), 3, GL11.GL_FLOAT, false, 0, 0);
		GL20.glEnableVertexAttribArray(Attribute.POSITION_3F.ordinal());
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	@Override
	public void draw() {

		GL30.glBindVertexArray(vao);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
	}

	@Override
	public void drawInstanced(int count) {
		GL30.glBindVertexArray(vao);
		GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, count);
	}

	@Override
	public void destroy() {

		GL30.glDeleteVertexArrays(vao);
		GL15.glDeleteBuffers(vbo);
	}

}

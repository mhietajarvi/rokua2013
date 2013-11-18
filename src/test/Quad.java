package test;

import org.lwjgl.opengl.GL11;

/**
 * A 2d shape with 4 corners at |r|,|r|,0.
 */
public class Quad extends DrawableBase {

	public Quad(float r) {

		float[] fc = new float[]{ 0.5f, 0.5f, 0.6f, 1.0f};
		
		init(GL11.GL_TRIANGLE_STRIP,
			Attribute.POSITION_3F, new float[][] {
				{ -r,-r, 0 },
				{ -r, r, 0 },
				{  r,-r, 0 },
				{  r, r, 0 },
			},
			Attribute.NORMAL_3F, new float[][] {
				{  0, 0, 1 },
				{  0, 0, 1 },
				{  0, 0, 1 },
				{  0, 0, 1 }
			},
			Attribute.COLOR_4F, new float[][] {
				fc, fc, fc, fc
			},
			Attribute.TEXTURE_3F, new float[][] {
				{  0, 0, 0 },
				{  0, 1, 0 },
				{  1, 0, 0 },
				{  1, 1, 0 },
			});
	}
}

package test;



/**
 * A shape with 4 corners at |r|,|r|,0.
 */
public class Quad extends DrawableBase {

	public Quad(float r) {

		float[] fc = new float[]{ 0.5f, 0.5f, 0.6f, 1.0f};
		
		init(new Attribute[]{
				Attribute.POSITION_3F,
				Attribute.NORMAL_3F,
				Attribute.COLOR_4F,
				Attribute.TEXTURE_3F,
				},
				new float[][] {
					{  r, r, 0 },
					{  r,-r, 0 },
					{ -r,-r, 0 },
					{ -r, r, 0 }
				},
				new float[][] {
					{  0, 0, 1 },
					{  0, 0, 1 },
					{  0, 0, 1 },
					{  0, 0, 1 }
				},
				new float[][]
					{ fc, fc, fc, fc },
				new float[][] {
					{  1, 1, 0 },
					{  1, 0, 0 },
					{  0, 0, 0 },
					{  0, 1, 0 },
				}
				);
	}
}

package test;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;


public class Cube extends SimpleRenderable {

	//private FloatBuffer vertexBuffer;
	//private ShortBuffer indexBuffer;
	
	private final FloatBuffer positions;
	private final FloatBuffer colors;
	private final FloatBuffer normals;
	

	/**
	 * A shape with 8 corners at |r|,|r|,|r|.
	 */
	public Cube(float r) {
		
		// build rendundant cube data from coordinates, faces and normals
		Vector3f[] c = Util.toVector3f(new float[] {
				 r, r, r,
				 r,-r, r,
				-r,-r, r,
				-r, r, r,
				 r, r,-r,
				 r,-r,-r,
				-r,-r,-r,
				-r, r,-r,
		});
		short[][] faces = new short[][] {
				{ 0,1,2,3 },
				{ 4,5,1,0 },
				{ 7,6,5,4 },
				{ 3,2,6,7 },
				{ 4,0,3,7 },
				{ 1,5,6,2 }
		};
		float[][] faceColors = new float[][] {
				{ 1.0f, 0.0f, 0.0f, 1.0f },
				{ 0.0f, 1.0f, 0.0f, 1.0f },
				{ 0.0f, 0.0f, 1.0f, 1.0f },
				{ 1.0f, 1.0f, 0.0f, 1.0f },
				{ 0.0f, 1.0f, 1.0f, 1.0f },
				{ 1.0f, 0.0f, 1.0f, 1.0f }
		};
		
		Vector3f[] faceNormals = new Vector3f[faces.length];
		
		int triCount = 0;
		for (int i = 0; i < faces.length; i++) {
			short[] f = faces[i];
			faceNormals[i] = Util.normal(c[f[0]], c[f[1]], c[f[2]]);
			triCount += f.length - 2;
		}
		
		// each tri has 3 vertices, each vertex component is 3 or 4 floats
		float[][][] pos = new float[triCount][3][3];
		float[][][] nrm = new float[triCount][3][3];
		float[][][] clr = new float[triCount][3][4];

		int t = 0;
		for (int i = 0; i < faces.length; i++) {
			short[] f = faces[i];
			Vector3f n = faceNormals[i];
			float[] color = faceColors[i];
			for (int j = 1; j < f.length - 1; j++) {
				if (j == 1) {
					Util.copy(c[f[0]], pos[t][0]);
					Util.copy(c[f[1]], pos[t][1]);
					Util.copy(c[f[2]], pos[t][2]);
				} else {
					Util.copy(c[f[j+0]], pos[t][0]);
					Util.copy(c[f[j+1]], pos[t][1]);
					Util.copy(c[f[0]], pos[t][2]);
				}
				Util.copy(n,nrm[t][0]);
				Util.copy(n,nrm[t][1]);
				Util.copy(n,nrm[t][2]);
				clr[t][0] = color;
				clr[t][1] = color;
				clr[t][2] = color;
				t++;
			}
		}
		positions = BufferUtils.createFloatBuffer(pos.length*3*3);
		//positions = ByteBuffer.allocateDirect(pos.length*3*3*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		Util.put(pos, positions).flip();
		normals = BufferUtils.createFloatBuffer(nrm.length*3*3);
		//normals = ByteBuffer.allocateDirect(nrm.length*3*3*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		Util.put(nrm, normals).flip();
		colors = BufferUtils.createFloatBuffer(clr.length*3*4);
		//colors = ByteBuffer.allocateDirect(clr.length*3*4*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		Util.put(clr, colors).flip();

		// calculate normal for each face
		// create tris from each face
		// for each vertex of each tri, output:
		//  1 positional coordinate (3 floats)
		//  1 normal (3 floats)
		//  1 color (3 floats)
		
		// quads:
		/*
		// tris:
		short[] i = new short[] {
				0,1,2,
				2,3,0,
				4,5,1,
				1,0,4,
				7,6,5,
				5,4,7,
				3,2,6,
				6,7,3,
				4,0,3,
				3,7,4,
				1,5,6,
				6,2,1,

		};
		float[] d = new float[] {
				 r, r, r,
				 r,-r, r,
				-r,-r, r,
				-r, r, r,
				 r, r,-r,
				 r,-r,-r,
				-r,-r,-r,
				-r, r,-r,
		};
		*/
		
//		ByteBuffer vbb = ByteBuffer.allocateDirect(c.length * 4);
//		vbb.order(ByteOrder.nativeOrder());
//		vertexBuffer = vbb.asFloatBuffer();
//		vertexBuffer.put(c);
//		vertexBuffer.position(0);
//		
//		ByteBuffer ibb = ByteBuffer.allocateDirect(i.length * 2);
//		ibb.order(ByteOrder.nativeOrder());
//		indexBuffer = ibb.asShortBuffer();
//		indexBuffer.put(i);
//		indexBuffer.position(0);
	}

	@Override
	public void draw(RenderState state) {
		
		int cap = positions.capacity()/3;
		program.draw(state, positions, colors, normals, GL11.GL_TRIANGLES, cap);
	}
}

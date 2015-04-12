package renderer.object;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import renderer.Util;
import renderer.shader.Attribute;


public class Cube extends DrawableBase {

	//private FloatBuffer vertexBuffer;
	//private ShortBuffer indexBuffer;
	
//	private final FloatBuffer positions;
//	private final FloatBuffer colors;
//	private final FloatBuffer normals;
	
//	private final FloatBuffer attribs;
//	int vao;
//	int vbo;
//
//	private int triCount;
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
		
		float[] fc = new float[]{ 0.5f, 0.5f, 0.6f, 1.0f};
		float[][] faceColors = new float[][] {
//				{ 1.0f, 0.0f, 0.0f, 1.0f },
//				{ 0.0f, 1.0f, 0.0f, 1.0f },
//				{ 0.0f, 0.0f, 1.0f, 1.0f },
//				{ 1.0f, 1.0f, 0.0f, 1.0f },
//				{ 0.0f, 1.0f, 1.0f, 1.0f },
//				{ 1.0f, 0.0f, 1.0f, 1.0f }
				fc,
				fc,
				fc,
				fc,
				fc,
				fc		};
		
		Vector3f[] faceNormals = new Vector3f[faces.length];
		
		int triCount = 0;
		for (int i = 0; i < faces.length; i++) {
			short[] f = faces[i];
			faceNormals[i] = Util.normal(c[f[0]], c[f[1]], c[f[2]]);
			faceNormals[i].normalise();
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
		
		init(GL11.GL_TRIANGLES,
			Attribute.POSITION_3F, pos,
			Attribute.NORMAL_3F, nrm,
			Attribute.COLOR_4F, clr);
		
		// generate vertex data
		
		// bind vertex data to server side buffers
		
		// render vertex data
		/*
		
		
		attribs = BufferUtils.createFloatBuffer(pos.length*3*3 + nrm.length*3*3 + clr.length*3*4);
		
//		positions = BufferUtils.createFloatBuffer(pos.length*3*3);
//		//positions = ByteBuffer.allocateDirect(pos.length*3*3*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		Util.put(pos, positions).flip();
//		normals = BufferUtils.createFloatBuffer(nrm.length*3*3);
//		//normals = ByteBuffer.allocateDirect(nrm.length*3*3*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		Util.put(nrm, normals).flip();
//		colors = BufferUtils.createFloatBuffer(clr.length*3*4);
//		//colors = ByteBuffer.allocateDirect(clr.length*3*4*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		Util.put(clr, colors).flip();

		int posOffset = attribs.position();
		Util.put(pos, attribs);
		int nrmOffset = attribs.position();
		Util.put(nrm, attribs);
		int clrOffset = attribs.position();
		Util.put(clr, attribs);
		attribs.flip();

		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		// create buffer and upload all vertex attributes
		vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, attribs, GL15.GL_STREAM_DRAW);

		//int nindex = program.getIndex(Attribute.NORMAL_3F);
		//int cindex = program.getIndex(Attribute.COLOR_4F);
		GL20.glVertexAttribPointer(Attribute.POSITION_3F.ordinal(), 3, GL11.GL_FLOAT, false, 0, posOffset*4);
		GL20.glEnableVertexAttribArray(Attribute.POSITION_3F.ordinal());
		GL20.glVertexAttribPointer(Attribute.NORMAL_3F.ordinal(), 3, GL11.GL_FLOAT, false, 0, nrmOffset*4);
		GL20.glEnableVertexAttribArray(Attribute.NORMAL_3F.ordinal());
		GL20.glVertexAttribPointer(Attribute.COLOR_4F.ordinal(), 4, GL11.GL_FLOAT, false, 0, clrOffset*4);
		GL20.glEnableVertexAttribArray(Attribute.COLOR_4F.ordinal());
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		*/
		
		/*
		float[] f = new float[attribs.limit()];
		attribs.get(f);
		attribs.flip();
		
		System.out.println("pos");
		for (int i = 0; i < triCount*3; i++) {
			System.out.println(Arrays.toString(Arrays.copyOfRange(f, i*3, i*3 + 3)));
		}
		
		System.out.println("nrm");
		for (int i = 0; i < triCount*3; i++) {
			System.out.println(Arrays.toString(Arrays.copyOfRange(f, nrmOffset+ i*3, nrmOffset + i*3 + 3)));
		}

		System.out.println("clr");
		for (int i = 0; i < triCount*3; i++) {
			System.out.println(Arrays.toString(Arrays.copyOfRange(f, clrOffset+ i*4, clrOffset + i*4 + 4)));
		}
		*/
		
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
//	int posOffset;
//	int nrmOffset;
//	int clrOffset;

//	@Override
//	public Drawable prepare(Program program) {
//
//		return new DrawableGeometry(program) {
//			
//			int vao;
//			int vbo;
//			{
//				vao = GL30.glGenVertexArrays();
//				GL30.glBindVertexArray(vao);
//				
//				// create buffer and upload all vertex attributes
//				vbo = GL15.glGenBuffers();
//				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
//				GL15.glBufferData(GL15.GL_ARRAY_BUFFER, attribs, GL15.GL_STREAM_DRAW);
//	
//				int nindex = program.getIndex(Attribute.NORMAL_3F);
//				int cindex = program.getIndex(Attribute.COLOR_4F);
//				GL20.glVertexAttribPointer(program.getIndex(Attribute.POSITION_3F), 3, GL11.GL_FLOAT, false, 0, posOffset*4);
//				GL20.glEnableVertexAttribArray(program.getIndex(Attribute.POSITION_3F));
//				if (nindex >= 0) {
//					GL20.glVertexAttribPointer(nindex, 3, GL11.GL_FLOAT, false, 0, nrmOffset*4);
//					GL20.glEnableVertexAttribArray(nindex);
//				}
//				if (cindex >= 0) {
//					GL20.glVertexAttribPointer(cindex, 4, GL11.GL_FLOAT, false, 0, clrOffset*4);
//					GL20.glEnableVertexAttribArray(cindex);
//				}
//				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//				GL30.glBindVertexArray(0);
//			}
//			@Override
//			protected void drawGeometry() {
//				GL30.glBindVertexArray(vao);
//				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, triCount*3);
//			}
//			@Override
//			public void destroy() {
//				GL30.glDeleteVertexArrays(vao);
//				GL15.glDeleteBuffers(vbo);
//			}
//		};
//	}

	/*
	@Override
	public void destroy() {
		GL30.glDeleteVertexArrays(vao);
		GL15.glDeleteBuffers(vbo);
	}

	@Override
	public void draw() {
		GL30.glBindVertexArray(vao);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, triCount*3);
		//GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 1*3);
	}

	@Override
	public void drawInstanced(int count) {
		GL30.glBindVertexArray(vao);
		GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, triCount*3, count);
		//GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, 1*3, count);
	}
	*/

//	@Override
//	public void draw() {
//
//		//GL20.glUseProgram(program);
//		
//		GL30.glBindVertexArray(vao);
//		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, triCount*3);
//		
////		int cap = positions.capacity()/3;
////		program.draw(state, positions, colors, normals, GL11.GL_TRIANGLES, cap);
//	}
	
	//@Override
		
}

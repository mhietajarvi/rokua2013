package test;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Vector3f;


public class Sphere implements Drawable {

	private final FloatBuffer attribs;
	private final ShortBuffer indices;
	int vao;
	int vbo;
	int ibo;

	//private int triCount;
	
	/**
	 */
	public Sphere(float r, int subdivisions) {

		// create one 1/8th and mirror it around

		Vector3f[] s = new Vector3f[] {
				new Vector3f(0, 1, 0),
				new Vector3f(1, 0, 0),
				new Vector3f(0, 0, 1)
			};
		
		for (int i = 0; i < subdivisions; i++) {
			s = Util.subdivide(s);
			for (Vector3f v : s) {
				v.normalise();
			}
		}
		
		float[] nrm = new float[s.length*3];
		float[] clr = new float[s.length*4];
		int p = 0;
		int c = 0;
		for (Vector3f v : s) {
			nrm[p++] = v.x;
			nrm[p++] = v.y;
			nrm[p++] = v.z;
			clr[c++] = v.x;
			clr[c++] = v.y;
			clr[c++] = v.z;
			clr[c++] = 1.0f;
		}
		float[] pos = new float[nrm.length];
		for (int i = 0; i < nrm.length; i++) {
			pos[i] = nrm[i]*r;
		}
		short[] idx = triIndices(s);
		

		indices = BufferUtils.createShortBuffer(8*idx.length);
		attribs = BufferUtils.createFloatBuffer(8*pos.length + 8*nrm.length + 8*clr.length);
		
//		positions = ByteBuffer.allocateDirect(8*pos.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		normals = ByteBuffer.allocateDirect(8*nrm.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		colors = ByteBuffer.allocateDirect(8*clr.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();

		int[] offset = new int[] { 0, 1, 0, 2, 0, 1, 0, 2 };

		short[] idxtmp = new short[idx.length];
		
		for (int i = 0; i < offset.length; i++) {
			for (int j = 0; j < s.length; j++) {
				nrm[j*3+offset[i]] *= -1;
				pos[j*3+offset[i]] *= -1;
			}
			for (int j = 0; j < idx.length; j++) {
				idxtmp[j] = (short) (s.length*i + idx[(i & 1) == 0 ? idx.length - 1 - j : j]);
			}
			
			indices.put(idxtmp);
			
			attribs.position(i*pos.length);
			attribs.put(pos);
			attribs.position(8*pos.length + i*nrm.length);
			attribs.put(nrm);
			attribs.position(8*pos.length + 8*nrm.length + i*clr.length);
			attribs.put(clr);
		}

		indices.position(0);
		attribs.position(0);
		
		
		//attribs = BufferUtils.createFloatBuffer(pos.length*3*3 + nrm.length*3*3 + clr.length*3*4);
		
//		int posOffset = attribs.position();
//		Util.put(pos, attribs);
//		int nrmOffset = attribs.position();
//		Util.put(nrm, attribs);
//		int clrOffset = attribs.position();
//		Util.put(clr, attribs);
//		attribs.flip();

		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		// create buffer and upload all vertex attributes
		vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, attribs, GL15.GL_STREAM_DRAW);
		
		// create buffer and upload all vertex attributes
		ibo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STREAM_DRAW);
		

		//int nindex = program.getIndex(Attribute.NORMAL_3F);
		//int cindex = program.getIndex(Attribute.COLOR_4F);
		GL20.glVertexAttribPointer(Attribute.POSITION_3F.ordinal(), 3, GL11.GL_FLOAT, false, 0, 0);
		GL20.glEnableVertexAttribArray(Attribute.POSITION_3F.ordinal());
		GL20.glVertexAttribPointer(Attribute.NORMAL_3F.ordinal(), 3, GL11.GL_FLOAT, false, 0, (8*pos.length)*4);
		GL20.glEnableVertexAttribArray(Attribute.NORMAL_3F.ordinal());
		GL20.glVertexAttribPointer(Attribute.COLOR_4F.ordinal(), 3, GL11.GL_FLOAT, false, 0, (8*pos.length + 8*nrm.length)*4);
		GL20.glEnableVertexAttribArray(Attribute.COLOR_4F.ordinal());
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	@Override
	public void destroy() {
		GL30.glDeleteVertexArrays(vao);
		GL15.glDeleteBuffers(ibo);
		GL15.glDeleteBuffers(vbo);
	}

	@Override
	public void draw() {
		GL30.glBindVertexArray(vao);
		GL11.glDrawElements(GL11.GL_TRIANGLES, indices.capacity(), GL11.GL_UNSIGNED_SHORT, 0);
	}

	@Override
	public void drawInstanced(int count) {
		GL30.glBindVertexArray(vao);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, indices.capacity(), GL11.GL_UNSIGNED_SHORT, 0, count);
	}


	static short[] triIndices(Vector3f[] s) {
		
		int n = (int)(Math.sqrt(1+8*s.length)-1)/2;
		int triCount = (n-1)*(n-1);
		short[] r = new short[triCount*3];
		int t = 0;
		int p = 0;
		int m = 1;
		for (; p + m + m + 1 <= s.length; m++) {
			int last = p + m - 1;
			for (int v = p; v < last; v++) {
				r[t++] = (short)p;
				r[t++] = (short)(p+1);
				r[t++] = (short)(p+m+1);
				r[t++] = (short)(p+m+1);
				r[t++] = (short)(p+m);
				r[t++] = (short)(p);
				p++;
			}
			r[t++] = (short)(p+m+1);
			r[t++] = (short)(p+m);
			r[t++] = (short)(p);
			p++;
		}
		return r;
	}
	
}

package test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import org.lwjgl.util.vector.Vector3f;

public class OldSphere {

	private final FloatBuffer positions;
	private final FloatBuffer colors;
	private final FloatBuffer normals;
	private final ShortBuffer indices;
	
	/**
	 * A spherical shape
	 */
	public OldSphere(float r, int subdivisions) {
		
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
		

		indices = ByteBuffer.allocateDirect(8*idx.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
		positions = ByteBuffer.allocateDirect(8*pos.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		normals = ByteBuffer.allocateDirect(8*nrm.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		colors = ByteBuffer.allocateDirect(8*clr.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();

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
			positions.put(pos);
			normals.put(nrm);
			colors.put(clr);
		}

		indices.position(0);
		positions.position(0);
		normals.position(0);
		colors.position(0);
		
//		indices = ByteBuffer.allocateDirect(idx.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
//		indices.put(idx).position(0);
//		
//		positions = ByteBuffer.allocateDirect(pos.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		positions.put(pos).position(0);
//		
//		normals = ByteBuffer.allocateDirect(nrm.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		normals.put(nrm).position(0);
//		
//		colors = ByteBuffer.allocateDirect(clr.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		colors.put(clr).position(0);
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
	
	static void test() {
		
		Vector3f[] s = new Vector3f[] {
				new Vector3f(0.5f, 1, 0),
				new Vector3f(0, 0, 0),
				new Vector3f(1, 0, 0)
			};
		
		Vector3f[] s2 = Util.subdivide(s);
		Vector3f[] s3 = Util.subdivide(s2);

		short[] i1 = triIndices(s);
		short[] i2 = triIndices(s2);
		short[] i3 = triIndices(s3);
		
		System.out.println(Arrays.toString(s));
		System.out.println(Arrays.toString(s2));
		System.out.println(Arrays.toString(s3));

		System.out.println(Arrays.toString(i1));
		System.out.println(Arrays.toString(i2));
		System.out.println(Arrays.toString(i3));
		
		System.out.println();
	}
	public static void main(String[] args) {
		test();
	}
	

//	@Override
//	public void draw(RenderState state) {
//		
//		int cap = indices.capacity();
//		program.draw(state, positions, colors, normals, indices, GLES20.GL_TRIANGLES, cap);
//	}
	
}

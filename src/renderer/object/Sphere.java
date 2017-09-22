package renderer.object;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import renderer.Util;
import renderer.shader.Attribute;


public class Sphere extends DrawableBase {

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
		
		float[][] pos = new float[s.length][];
		float[][] nrm = new float[s.length][];
		float[][] clr = new float[s.length][];
		for (int i = 0; i < s.length; i++) {
			pos[i] = new float[]{ r*s[i].x, r*s[i].y, r*s[i].z };
			nrm[i] = new float[]{   s[i].x,   s[i].y,   s[i].z };
			clr[i] = new float[]{   s[i].x,   s[i].y,   s[i].z, 1.0f };
		}
		short[] idx = triIndices(s);
		
		
		float[][] pos8 = new float[pos.length * 8][];
		float[][] nrm8 = new float[nrm.length * 8][];
		float[][] clr8 = new float[clr.length * 8][];
		short[] idx8 = new short[idx.length * 8];
		

		int[] offset = new int[] { 0, 1, 0, 2, 0, 1, 0, 2 };
		
		for (int i = 0; i < offset.length; i++) {

			// mirror position and normal along one axis
			for (int j = 0; j < s.length; j++) {
				pos[j][offset[i]] *= -1;
				nrm[j][offset[i]] *= -1;
				pos8[i*pos.length + j] = Arrays.copyOf(pos[j], pos[j].length);
				nrm8[i*nrm.length + j] = Arrays.copyOf(nrm[j], nrm[j].length);
				clr8[i*clr.length + j] = Arrays.copyOf(clr[j], clr[j].length);
			}
			// copy indices, but flip order for odd mirroring to keep same handedness (for front/back culling)
			for (int j = 0; j < idx.length; j++) {
				idx8[i*idx.length + j] = (short) (s.length*i + idx[(i & 1) == 0 ? idx.length - 1 - j : j]);
			}
		}

		init(GL11.GL_TRIANGLES, idx8,
				Attribute.POSITION_3F, pos8,
				Attribute.NORMAL_3F, nrm8,
				Attribute.COLOR_4F, clr8
				);
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

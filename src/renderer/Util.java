package renderer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class Util {

	public static Vector3f[] toVector3f(float[] c) {
		Vector3f[] r = new Vector3f[c.length / 3];
		for (int i = 0; i < r.length; i++) {
			r[i] = new Vector3f(c[3 * i], c[3 * i + 1], c[3 * i + 2]);
		}
		return r;
	}

	public static String read(String file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file)), Charset.forName("UTF-8"));
	}

	public static String read(File file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), Charset.forName("UTF-8"));
	}

	public static File find(String dir, final String filePattern) {

		File[] files = new File(dir).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches(filePattern);
			}
		});
		return files.length > 0 ? files[0] : null;
	}

	// static Vector3f normalize(Vector3f v) {
	// return v.mul((float) (1/Math.sqrt(v.x*v.x + v.y*v.y + v.z*v.z)));
	// }
	//
	// static Vector3f normalizeLocal(Vector3f v) {
	// return v.mulLocal((float) (1/Math.sqrt(v.x*v.x + v.y*v.y + v.z*v.z)));
	// }
	//
	// static Vector3f[] normalizeLocal(Vector3f[] va) {
	// for (Vector3f v : va) {
	// v.mulLocal((float) (1/Math.sqrt(v.x*v.x + v.y*v.y + v.z*v.z)));
	// }
	// return va;
	// }

	public static Vector3f normal(Vector3f p1, Vector3f p2, Vector3f p3) {

		Vector3f tmp1 = Vector3f.sub(p2, p1, null);
		Vector3f tmp2 = Vector3f.sub(p3, p1, null);
		return Vector3f.cross(tmp2, tmp1, null);
		// Vector3f tmp1 = new Vector3f(p2);
		// Vector3f tmp2 = new Vector3f(p3);
		// tmp1.sub(p1);
		// tmp2.sub(p1);
		// tmp1.cross(tmp1, tmp2);
		// return tmp1;
	}

	public static void copy(Vector3f v, float[] c) {
		c[0] = v.x;
		c[1] = v.y;
		c[2] = v.z;
	}

	public static FloatBuffer put(float[][] data, FloatBuffer buf) {
		for (float[] d2 : data) {
			buf.put(d2);
		}
		return buf;
	}

	public static FloatBuffer put(float[][][] data, FloatBuffer buf) {
		for (float[][] d1 : data) {
			for (float[] d2 : d1) {
				buf.put(d2);
			}
		}
		return buf;
	}

	// subdivides triangles as follows:
	// *0
	//
	// *1 *2
	// to
	// *0
	// *1 *2
	// *3 *4 *5
	//
	public static Vector3f[] subdivide(Vector3f[] s) {

		List<Vector3f> r = new LinkedList<Vector3f>();
		int p = 0;
		int m = 1;
		for (; p + m <= s.length; p += m, m++) {
			// add top vertices to result
			int last = p + m - 1;
			for (int v = p; v < last; v++) {
				r.add(s[v]);
				r.add(mid(s[v], s[v + 1]));
			}
			r.add(s[last]);

			if (p + m + m + 1 > s.length) {
				break;
			}

			// subdivide tris from p to p + m - 1
			for (int v = p; v < p + m; v++) {
				Vector3f top = s[v];
				Vector3f left = s[v + m];
				Vector3f right = s[v + m + 1];
				r.add(mid(top, left));
				r.add(mid(top, right));
			}
		}
		return r.toArray(new Vector3f[r.size()]);
	}

	public static Vector3f mid(Vector3f a, Vector3f b) {
		return new Vector3f((a.x + b.x) * 0.5f, (a.y + b.y) * 0.5f, (a.z + b.z) * 0.5f);
	}

}

package test;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;


public class Sandbox {

	public void test1() {
		
		double t0 = 0;
		double t1 = 1;
		
		SmoothVelocity s = new SmoothVelocity(t0, t1);
		Func.PV ip = s.interpolate(0,-100, -1,-100);
		
		int n = 10;
		
		for (int i = -1; i <= (n+1); i++) {
			double t = t0 + ((t1-t0)*i)/n;
			Log.d("t=%.1f, p=%.1f, v=%.1f", t, ip.p(t),ip.v(t));
		}
	}
	
	public void test2() {
		
		// nanoseconds -> float
		// how much precision is lost for different base magnitudes

		long d_ns = 1000000000L/60;
		float df = 1.0f/60; //0.004f;
		
		
		
		
		int b1 = 0;
		int b2 = 36000;
		
		long ns1 = b1 * 1000000000L;
		long ns2 = b2 * 1000000000L;
		Log.d("epsilon : %.5f", df);
		
		for (int i = 0; i < 20; i++) {

			float f1 = (float)(ns1/1000000000.0);
			float f2 = (float)(ns2/1000000000.0);
			
			Log.d("%.5f  %.5f  %.2f %%", f1, f2, ((f2 - b2) - (f1 - b1))*100/(df));
			
			ns1 += d_ns;
			ns2 += d_ns;
		}
	}

	Matrix3f rotz(float angle) {
		float s = (float)Math.sin(angle);
		float c = (float)Math.cos(angle);
		Matrix3f m = new Matrix3f();
		m.m00 =  c; m.m10 =  s;
		m.m01 = -s; m.m11 =  c;
		return m;
	}
	Matrix3f roty(float angle) {
		float s = (float)Math.sin(angle);
		float c = (float)Math.cos(angle);
		Matrix3f m = new Matrix3f();
		m.m00 =  c; m.m20 =  s;
		m.m02 = -s; m.m22 =  c;
		return m;
	}
	Matrix3f rotx(float angle) {
		float s = (float)Math.sin(angle);
		float c = (float)Math.cos(angle);
		Matrix3f m = new Matrix3f();
		m.m11 =  c; m.m21 = -s;
		m.m12 =  s; m.m22 =  c;
		return m;
	}
	
	final float eps = 0.063273f;
	final Matrix3f rx = rotx(eps);
	final Matrix3f ry = roty(eps);
	final Matrix3f rz = rotz(eps);
	
	public void test4(Vector3f p0) {

		float ax = Math.abs(p0.x);
		float ay = Math.abs(p0.y);
		float az = Math.abs(p0.z);
		Vector3f p1 = new Vector3f();
		Vector3f p2 = new Vector3f();
		float mult = 1;
		if (ax >= ay && ax >= az) {
			mult = Math.signum(p0.x);
			Matrix3f.transform(ry, p0, p1);
			Matrix3f.transform(rz, p0, p2);
		} else 	if (ay >= ax && ay >= az) {
			mult = Math.signum(p0.y);
			Matrix3f.transform(rz, p0, p1);
			Matrix3f.transform(rx, p0, p2);
		} else {
			mult = Math.signum(p0.z);
			Matrix3f.transform(ry, p0, p1);
			Matrix3f.transform(rx, p0, p2);
		}
		System.out.println("p0 : "+p0);
		System.out.println("p1 : "+p1);
		System.out.println("p2 : "+p2);
		Vector3f d1 = Vector3f.sub(p1, p0, null);
		Vector3f d2 = Vector3f.sub(p2, p0, null);
		System.out.println("p1-p0 : "+d1);
		System.out.println("p2-p0 : "+d2);
		Vector3f n = Vector3f.cross(d2, d1, null);
		n.scale(mult);
		n.normalise();
		System.out.println("n     : "+n);
		System.out.println();
	}
	
	public void test3() {
	
		Vector3f xx = new Vector3f(1,0,0);
		Vector3f yy = new Vector3f(0,1,0); 
		Vector3f zz = new Vector3f(0,0,1);
		Vector3f xxn = new Vector3f(-1,0,0);
		Vector3f yyn = new Vector3f(0,-1,0); 
		Vector3f zzn = new Vector3f(0,0,-1);

		test4(xx);
		test4(yy);
		test4(zz);
		test4(xxn);
		test4(yyn);
		test4(zzn);
		
		//Matrix3f mx = new Matrix3f();
		//Log.d("%.1f", Math.a(4))
	}
	
	public static void main(String[] args) {
		
		Sandbox s = new Sandbox();
		
		//s.test1();
		//s.test2();
		s.test3();

	}
}

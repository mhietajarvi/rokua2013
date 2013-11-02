package test;

import org.lwjgl.util.vector.*;

public class VectorPV {

	Func.PV x,y,z;
	
	VectorPV(Interpolator ip, Vector3f p0, Vector3f v0, Vector3f p1, Vector3f v1) {
		x = ip.interpolate(p0.x, v0.x, p1.x, v1.x);
		y = ip.interpolate(p0.y, v0.y, p1.y, v1.y);
		z = ip.interpolate(p0.z, v0.z, p1.z, v1.z);
	}

	public void p(double t, Vector3f result) {
		result.x = x.p(t);
		result.y = y.p(t);
		result.z = z.p(t);
	}
	
	public void v(double t, Vector3f result) {
		result.x = x.v(t);
		result.y = y.v(t);
		result.z = z.v(t);
	}
	
}

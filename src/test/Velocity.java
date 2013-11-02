package test;

import org.lwjgl.util.vector.*;

public class Velocity implements Animation {
	
	Vector3f v;
	
	public Velocity(Vector3f v) {
		this.v = v;
	}

	@Override
	public void updateTransform(long time_ns, Matrix4f transform) {
		float t = (float)(time_ns/1000000000.0);
		transform.m30 += t*v.x;
		transform.m31 += t*v.y;
		transform.m32 += t*v.z;
	}

}

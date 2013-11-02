package test;

import org.lwjgl.util.vector.*;

public class Acceleration implements Animation {
	
	// 0.5*a*t*t
	
	//Vector3f a; // units/s
	Vector3f a; // units/s*s
	
	public Acceleration(Vector3f a) {
		this.a = a;
	}

	@Override
	public void updateTransform(long time_ns, Matrix4f transform) {
		//transform.setIdentity();
		//transform.translate(vec)
		float t = (float)(time_ns/1000000000.0);
		float m = 0.5f*t*t;
		
		transform.m30 += m*a.x;
		transform.m31 += m*a.y;
		transform.m32 += m*a.z;
	}

}

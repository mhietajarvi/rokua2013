package test;

import org.lwjgl.util.vector.*;

public class Rotation implements Animation {
	
	Vector3f axis;
	float speed;
	
	public Rotation(float x, float y, float z, float speed) {
		this.axis = new Vector3f(x, y, z);
		this.axis.normalise();
		this.speed = speed;
	}

	@Override
	public void updateTransform(long time_ns, Matrix4f transform) {
		//transform.setIdentity();
		transform.rotate((float)((time_ns/1000000000.0)*speed), axis);
	}

}

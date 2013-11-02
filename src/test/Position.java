package test;

import org.lwjgl.util.vector.Matrix4f;

public class Position implements Animation {
	
	float x,y,z;
	
	public Position(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void updateTransform(long time_ns, Matrix4f transform) {
		//transform.setIdentity();
		//transform.translate(vec)
		transform.m30 = x;
		transform.m31 = y;
		transform.m32 = z;
	}

}

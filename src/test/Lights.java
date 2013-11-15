package test;

import org.lwjgl.util.vector.Vector4f;

public class Lights {
	// point light in world space
	public final Vector4f point_light_1 = new Vector4f(); 
	void setWorldLight(float x, float y, float z) {

		// directly set a point light in view coordinates
		point_light_1.x = x;
		point_light_1.y = y;
		point_light_1.z = z;
	}

}

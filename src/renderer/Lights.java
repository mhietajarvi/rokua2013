package renderer;

import org.lwjgl.util.vector.Vector3f;

public class Lights {
	// point light in world space
	public final Vector3f point_light_1 = new Vector3f(); 

	public void setWorldLight(float x, float y, float z) {

		// directly set a point light in view coordinates
		point_light_1.x = x;
		point_light_1.y = y;
		point_light_1.z = z;
	}

}

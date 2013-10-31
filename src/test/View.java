package test;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

// things that should stay static for one frame
public class View {
	
	public final Matrix4f world_to_view = new Matrix4f();
	public final Matrix4f projection = new Matrix4f();
	
	// lights temporarily here...
	// point light in view space
	public final Vector4f point_light_1 = new Vector4f(); 

	View() {
		
	}
	
	private final double PI = 3.14159265358979323846;
	private float degreesToRadians(float degrees) {
		return degrees * (float)(PI / 180d);
	}
	private float coTangent(float angle) {
		return (float)(1f / Math.tan(angle));
	}
	
	void setViewLight(float x, float y, float z) {

		// directly set a point light in view coordinates
		point_light_1.x = x;
		point_light_1.y = y;
		point_light_1.z = z;
	}
	// convert point light given in world coordinates to view coordinates 
	
	void setView() {
		// e.g. look at something from somewhere 
	}

	void translateView(float dx, float dy, float dz) {
		
		Matrix4f translation = new Matrix4f();
		translation.translate(new Vector3f(dx, dy, dz));
		Matrix4f.mul(translation, world_to_view, world_to_view);
		orthogonalize();
	}
	
	void rotateView(float dx, float dy) {
		
		Vector3f vec = new Vector3f(-dy, dx, 0);
		float len = vec.length();
		vec.normalise();
		Matrix4f rotation = new Matrix4f();
		rotation.rotate(len, vec);
		Matrix4f.mul(rotation, world_to_view, world_to_view);
		orthogonalize();
	}
	
	void orthogonalize() {
		// TODO: orthogonalize world_to_view matrix to reset accumulated error
	}

	void setProjection(float fieldOfView, float nearPlane, float farPlane, int viewportWidth, int viewportHeight) {
		
		float aspectRatio = (float)viewportWidth / (float)viewportHeight;
		float yScale = this.coTangent(degreesToRadians(fieldOfView / 2f));
		float xScale = yScale / aspectRatio;
		float frustumLength = farPlane - nearPlane;
		 
		projection.m00 = xScale;
		projection.m11 = yScale;
		projection.m22 = -((farPlane + nearPlane) / frustumLength);
		projection.m23 = -1;
		projection.m32 = -((2 * nearPlane * farPlane) / frustumLength);
		projection.m33 = 0;
	}
}

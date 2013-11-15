package test;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

// things that should stay static for one frame
public class View {

	public final Matrix4f world_to_view = new Matrix4f();
	public final Matrix4f view_to_world = new Matrix4f();
	public final Matrix4f projection = new Matrix4f();
	
	View() {
		
	}
	
	private final double PI = 3.14159265358979323846;
	private float degreesToRadians(float degrees) {
		return degrees * (float)(PI / 180d);
	}
	private float coTangent(float angle) {
		return (float)(1f / Math.tan(angle));
	}
	
	void setView() {
		// e.g. look at something from somewhere 
	}
	
	void translateView(float dx, float dy, float dz) {
		
		Matrix4f translation = new Matrix4f();
		translation.translate(new Vector3f(dx, dy, dz));
		Matrix4f.mul(translation, world_to_view, world_to_view);
		recalc();
	}
	
	void rotateView(float dx, float dy) {
		
		Vector3f vec = new Vector3f(-dy, dx, 0);
		float len = vec.length();
		vec.normalise();
		Matrix4f rotation = new Matrix4f();
		rotation.rotate(len, vec);
		Matrix4f.mul(rotation, world_to_view, world_to_view);
		recalc();
	}
	
	private void recalc() {
		// TODO: orthogonalize world_to_view matrix to reset accumulated error
		// TODO: or better yet, use lookat vector and keep view x axis parallel to world x-z plane
		Matrix4f.invert(world_to_view, view_to_world);
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

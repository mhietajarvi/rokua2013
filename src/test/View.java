package test;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

// things that should stay static for one frame
public class View {

	public static final Vector3f UP = new Vector3f(0,1,0);

	public final Vector3f eye = new Vector3f(0,0,0);
	public final Vector3f x = new Vector3f(1,0,0);
	public final Vector3f y = new Vector3f(0,1,0);
	public final Vector3f z = new Vector3f(0,0,1);
	public final Matrix4f view_to_world = new Matrix4f();
	public final Matrix4f world_to_view = new Matrix4f();
	public final Matrix4f projection = new Matrix4f();
	public final Matrix4f world_to_projected = new Matrix4f();
	
	private final double PI = 3.14159265358979323846;
	private float degreesToRadians(float degrees) {
		return degrees * (float)(PI / 180d);
	}
	private float coTangent(float angle) {
		return (float)(1f / Math.tan(angle));
	}
	
	public View() {
		recalc();
	}
	
	@Override
	public String toString() {
		return "x : "+x+", y : "+y+", z : "+z;
	}
	
	public static void main(String[] args) {
		
		View v = new View();
		//v.look(new Vector3f(0,0,0),  new Vector3f(1,0,0));
		System.out.println(v);
		
		v.rotateView((float)(Math.PI/2), 0);
		System.out.println(v);
	}
	
	public void look(Vector3f from, Vector3f to) {
		
		eye.set(from);
		Vector3f.sub(from, to, z); // we look along negative z, so sub is reversed 
		z.normalise();
		y.set(z);
		y.scale(Vector3f.dot(UP, z));
		Vector3f.sub(UP, y, y);
		Vector3f.cross(y, z, x);
		recalc();
	}
	
	public void translateView(double dx, double dy, double dz) {

		Vector3f.add(mul(dx,x,t1), mul(dy,y,t2), t3);
		Vector3f.add(mul(dz,z,t1), t3, t3);
		Vector3f.add(eye, t3, eye);
		//Vector3f.add(mul(dz,z,t1), t3, t3);
		
//		eye.x += dx;
//		eye.y += dy;
//		eye.z += dz;
		
//		Matrix4f translation = new Matrix4f();
//		translation.translate(new Vector3f(dx, dy, dz));
//		Matrix4f.mul(translation, world_to_view, world_to_view);
		recalc();
	}
	
	Vector3f t1 = new Vector3f();
	Vector3f t2 = new Vector3f();
	Vector3f t3 = new Vector3f();
	//Vector3f t4 = new Vector3f();
	
	static Vector3f mul(double m, Vector3f src, Vector3f dest) {

		dest.set(src);
		dest.scale((float) m);
		return dest;
	}

	// project vector on rotation axis
	// subtract projection to get 
	// calc cross product
	// 
	
	public void rotateView(double dx, double dy) {
	
		float c = (float) Math.cos(dx);
		float s = (float) Math.sin(dx);

		// rotate around y by dx
		// this changes x,z
		mul(-s,x,t3);
		Vector3f.add(mul(c,x,t1), mul(s,z,t2), x);
		Vector3f.add(mul(c,z,t1),          t3, z);

		// rotate around x by dy
		// this changes y,z
		
		c = (float) Math.cos(dy);
		s = (float) Math.sin(dy);
		mul(-s,y,t3);
		Vector3f.add(mul(c,y,t1), mul(s,z,t2), y);
		Vector3f.add(mul(c,z,t1),          t3, z);
		
//		y = c*y + s*z
//		z = c*z - s*y
		
		// view_to_world.rotate(angle, axis);
		
//		Vector3f vec = new Vector3f(-dy, dx, 0);
//		float len = vec.length();
//		vec.normalise();
//		Matrix4f rotation = new Matrix4f();
//		rotation.rotate(len, vec);
//		Matrix4f.mul(rotation, world_to_view, world_to_view);
		recalc();
	}
	
	private void recalc() {
		
		view_to_world.m00 = x.x;
		view_to_world.m01 = x.y;
		view_to_world.m02 = x.z;
		view_to_world.m10 = y.x;
		view_to_world.m11 = y.y;
		view_to_world.m12 = y.z;
		view_to_world.m20 = z.x;
		view_to_world.m21 = z.y;
		view_to_world.m22 = z.z;
		view_to_world.m30 = eye.x;
		view_to_world.m31 = eye.y;
		view_to_world.m32 = eye.z;
		view_to_world.m03 = view_to_world.m13 = view_to_world.m23 = 0;
		view_to_world.m33 = 1;
		
		// TODO: orthogonalize world_to_view matrix to reset accumulated error
		// TODO: or better yet, use lookat vector and keep view x axis parallel to world x-z plane
		//Matrix4f.invert(world_to_view, view_to_world);
		Matrix4f.invert(view_to_world, world_to_view);
		Matrix4f.mul(projection, world_to_view, world_to_projected);
		
//		System.out.println("view_to_world:\n"+view_to_world);
//		System.out.println("world_to_view:\n"+world_to_view);
	}

	float nearPlane;
	float farPlane;
	
	public void setProjection(float fieldOfView, float nearPlane, float farPlane, int viewportWidth, int viewportHeight) {
		
		this.nearPlane = nearPlane;
		this.farPlane = farPlane;
		
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
		recalc();
	}
}

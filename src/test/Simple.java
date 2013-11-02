package test;

import org.lwjgl.util.vector.*;

public class Simple {

	private static final Vector3f ZERO = new Vector3f();
	
	public static class Position implements Func.M4 {

		private final Vector3f p;
		private final Vector3f v;
		private final Vector3f a;
		private final double t0;
		public Position(double t, Vector3f p0) {
			this(t, p0, ZERO, ZERO);
		}
		public Position(double t, Vector3f p0, Vector3f v) {
			this(t, p0, v, ZERO);
		}
		public Position(double t0, Vector3f p0, Vector3f v0, Vector3f a) {
			this.t0 = t0;
			this.p = p0;
			this.v = v0;
			this.a = a;
		}
		@Override
		public void m4(double t, Matrix4f result) {
			result.setIdentity();
			float dt = (float)(t - t0);
			result.m30 = p.x + v.x*dt + 0.5f*a.x*dt*dt;
			result.m31 = p.y + v.y*dt + 0.5f*a.y*dt*dt;
			result.m32 = p.z + v.z*dt + 0.5f*a.z*dt*dt;
		}
	}

	public static class Velocity implements Func.M4 {

		private final Vector3f p;
		public Velocity(Vector3f p) {
			this.p = p;
		}
		@Override
		public void m4(double t, Matrix4f result) {
			result.setIdentity();
			result.m30 = p.x;
			result.m31 = p.y;
			result.m32 = p.z;
		}
	}
}

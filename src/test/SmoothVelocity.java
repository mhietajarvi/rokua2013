package test;

import org.lwjgl.util.vector.*;

//animate single variable: 
//start state  p0,v0 at t0 (specified)
//acceleration a0 (calculated)
//middle state p1,v1 at t1  (calculated)
//acceleration a1 (calculated)
//end state    p2,v2 at t2 (specified)
//preserve continuity of v
//dt1 = t1 - t0
//dt2 = t2 - t1
//no limit to acceleration
//apply at most two different acceleration segments

//matrix only depends on times, so it can be reused for 
//any value

// -dt1         0           1    0     a0     v0
// -0.5*dt1*dt1 0           0    1  *  a1  =  p0 + v0*dt1
//  0           0.5*dt2*dt2 dt2  1     v1     p2
//  0           dt2         1    0     p1     v2

// DT * x = S
// x = DT^-1 * S
// 
// 
//p1 = p0 + v0*dt1 + 0.5*a0*dt1*dt1
//v1 = v0 + a0*dt1

//
//

//p2 = p1 + v1*dt2 + 0.5*a1*dt2*dt2
//v2 = v1 + a1*dt2


// if (t < t0):
//  p = p0
//  v = v0

// if (t < t1):
//  p = p0 + v0*(t-t0) + 0.5*a0*(t-t0)^2
//  v = v0 + a0*(t-t0)

// if (t < t2):


// else
//  p = p2
//  v = v2

//p2 = p1 + v1*dt2 + 0.5*a1*dt2*dt2
//v2 = v1 + a1*dt2


public class SmoothVelocity implements Interpolator {

	final double t0;
	final double t1;
	final double t2;
	final Matrix4f m = new Matrix4f();
	final Vector4f tmp = new Vector4f();
	float dt1;

	@Override
	public PV interpolate(final float p0, final float v0, final float p2, final float v2) {
		
		return new PV() {

			final float a0;
			final float a1;
			final float v1;
			final float p1;
			{
//				this.p0 = p0;
//				this.v0 = v0;
//				this.p2 = p2;
//				this.v2 = v2;
				tmp.set(v0, p0 + v0*dt1, p2, v2);
				Matrix4f.transform(m, tmp, tmp);
				a0 = tmp.x;
				a1 = tmp.y;
				v1 = tmp.z;
				p1 = tmp.w;
				// Log.d("a0:"+a0+", a1:"+a1+", v1:"+v1+", p1:"+p1);
			}
			
//			final float p0;
//			final float v0;
//			final float p2;
//			final float v2;
		
			public float p(double t) {
		
				if (t < t0) {
					float dt = (float)(t-t0);
					return p0 + v0*dt;
				}
				if  (t < t1) {
					float dt = (float)(t-t0);
					return p0 + v0*dt + 0.5f*a0*dt*dt;
				}
				if (t < t2) {
					float dt = (float)(t-t1);
					return p1 + v1*dt + 0.5f*a1*dt*dt;
				}
				float dt = (float)(t-t2);
				return p2 + v2*dt;
			}
		
			public float v(double t) {
		
				if (t < t0) {
					return v0;
				}
				if  (t < t1) {
					float dt = (float)(t-t0);
					return v0 + a0*dt;
				}
				if (t < t2) {
					float dt = (float)(t-t1);
					return v1 + a1*dt;
				}
				return v2;
			}
		};
	}
	
	/*
	// return position and velocity at given time
	public class Interpolator implements PV {

		Interpolator(float p0, float v0, float p2, float v2) {
			this.p0 = p0;
			this.v0 = v0;
			this.p2 = p2;
			this.v2 = v2;
			tmp.set(v0, p0 + v0*dt1, p2, v2);
			//Vector4f v = new Vector4f(v0, p0 + v0*dt1, p2, v2);
			Matrix4f.transform(m, tmp, tmp);
			a0 = tmp.x;
			a1 = tmp.y;
			v1 = tmp.z;
			p1 = tmp.w;
			// Log.d("a0:"+a0+", a1:"+a1+", v1:"+v1+", p1:"+p1);
		}
		
		final float p0;
		final float v0;
		final float p2;
		final float v2;
		final float a0;
		final float a1;
		final float v1;
		final float p1;
	
		public float p(double t) {
	
			if (t < t0) {
				float dt = (float)(t-t0);
				return p0 + v0*dt;
			}
			if  (t < t1) {
				float dt = (float)(t-t0);
				return p0 + v0*dt + 0.5f*a0*dt*dt;
			}
			if (t < t2) {
				float dt = (float)(t-t1);
				return p1 + v1*dt + 0.5f*a1*dt*dt;
			}
			float dt = (float)(t-t2);
			return p2 + v2*dt;
		}
	
		public float v(double t) {
	
			if (t < t0) {
				return v0;
			}
			if  (t < t1) {
				float dt = (float)(t-t0);
				return v0 + a0*dt;
			}
			if (t < t2) {
				float dt = (float)(t-t1);
				return v1 + a1*dt;
			}
			return v2;
		}
	}
	*/
	// this gives p,v 
	// generate new value interpolator
	
	
	SmoothVelocity(double t0, double t2) {

		this.t0 = t0;
		this.t2 = t2;
		t1 = (t0+t2)/2;
		dt1 = (float)(t1 - t0);
		float dt2 = (float)(t2 - t1);
		
		// -dt1         0           1    0     a0     v0
		// -0.5*dt1*dt1 0           0    1  *  a1  =  p0 + v0*dt1
		//  0           0.5*dt2*dt2 dt2  1     v1     p2
		//  0           dt2         1    0     p1     v2
		m.m00 = -dt1;          m.m10 = 0;            m.m20 = 1;   m.m30 = 0;
		m.m01 = -0.5f*dt1*dt1; m.m11 = 0;            m.m21 = 0;   m.m31 = 1;
		m.m02 = 0;             m.m12 = 0.5f*dt2*dt2; m.m22 = dt2; m.m32 = 1;
		m.m03 = 0;             m.m13 = dt2;          m.m23 = 1;   m.m33 = 0;
		
		Log.d("time matrix:\n"+m);
		m.invert();
		Log.d("time matrix inverse:\n"+m);
		
		//Matrix4f m = new Matrix4f();
		
	}

}

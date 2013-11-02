package test;

import java.util.*;

import org.lwjgl.util.vector.*;

import test.ObjectManager.Event;
import test.Program.Uniform;



public class Composer {

	private final test.ObjectManager.Object parent;
	private final Deque<Vector3f> available; //attachPoints;
	//private final boolean[] used;
	
    static Vector3f p0 = new Vector3f();
    static Vector3f v0 = new Vector3f();
    static Vector3f p1 = new Vector3f();
    static Vector3f v1 = new Vector3f();
	
	public Composer(test.ObjectManager.Object parent, List<Vector3f> attachPoints) {
		this.parent = parent;
		this.available = new ArrayDeque<>(attachPoints);
		//used = new boolean[attachPoints.size()];
	}
	
	// composer attaches objects under parent locations
	// and provides a transition effect
	
	// create composite object
	// initially composite does not have any subobjects, just knowledge where they would go
	// attach subobjects to the composite
	public boolean hasRoom() {
		return !available.isEmpty();
	}
	Matrix4f tmp = new Matrix4f();
	
	public void attach(final Interpolator ip, long time_ns, final test.ObjectManager.Object object) {

		// get current p/v of object in parent's space
		// object.worldPos
		// object.worldVelocity
		
		Matrix4f otf = object.getTransform(time_ns);
		Matrix4f ptf = parent.getTransform(time_ns);
		
		Matrix4f.invert(ptf, tmp);
		Matrix4f.mul(tmp, otf, tmp);
		
		Vector4f v = new Vector4f(0, 0, 0, 1);
		Matrix4f.transform(tmp, v, v);
		p0.x = v.x;
		p0.y = v.y;
		p0.z = v.z;

		// p0 is object current position in parent's space
		
//    	rnd(p0, 10);
//    	rnd(p1, 0);
    	VectorPV pos = new VectorPV(ip, p0, v0, available.remove(), v1);
		
		object.set(new PosRot(pos, null)); //.Position(available.remove()));
		parent.attach(object);
		//object.clearEvents();
		object.add(new Event() {
			@Override
			public boolean update(double t, long time_ns) {
				if (t >= ip.t2()) {
					Interpolator ip = new SmoothVelocity(t, t+0.7);
					object.set(Uniform.U_COLOR_MULT_F, ip.interpolate(1, 20, 1, 0));
					
					//object.add(event);
					
					
					return true;
				}
				return false;
			}
		});
		
		// this object needs to be attached to the world
		
		// when object is attached, get its current p/v relative to this object
		// (we need p/v for parent object as well...)
		
		// TODO Auto-generated method stub
		//return super.attach(objects);
	}
	
}

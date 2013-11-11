package test;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import test.ObjManager2.Obj;
import test.Program.Uniform;


// this is a controller,
// which means that it controls
// movement and other attributes
// of a set of objects

// this particular controller 
// keeps attached objects 
// at or near point cloud positions
// 


public class Binder implements Controller {

//	public static class State {
//		
//		long t;
//		Vector3f p = new Vector3f();
//		Vector3f v = new Vector3f();
//	}
	
	// how objects are moved towards target state
	public interface Mover {
		
		// calculate result location at given time
		// (this is more time-consuming than following simple equation,
		//  but on the other hand, this is dynamic and robust)
		void move(Vector3f p, Vector3f v, Vector3f target, float dt, Vector3f result);
	}
	
	private final Obj parent;
	private final Deque<Vector3f> available;
	private final Mover mover;
	private final IdentityHashMap<Obj, Vector3f> bound;
	
	public Binder(Obj parent, Collection<Vector3f> targets, Mover mover) {
		this.parent = parent;
		this.available = new ArrayDeque<>(targets);
		this.mover = mover;
		bound = new IdentityHashMap<>(targets.size());
	}
	
	

	public Obj getParent() {
		return parent;
	}

	// we need precision of at least 1/200
	// 
	// 2345.55

	// working area
	Vector3f p = new Vector3f();
	Vector3f v = new Vector3f();
	Vector3f target = new Vector3f();
	Vector3f result = new Vector3f();
	
//    private State current = new State();
//    private State target = new State();
//    private State result = new State();

	@Override
	public void step(double time, float dt) {
		
		for (Map.Entry<Obj, Vector3f> e : bound.entrySet()) {
			e.getKey().getWorldPosition(p);
			e.getKey().getWorldVelocity(v);

			target.set(e.getValue());
			target.translate(0, 0, (float)(2*Math.sin(target.x*0.1 + 3*time)));
			
			//Log.d("p = "+p);
			//Log.d("v = "+v);
			Matrix.transform_pos(parent.getWorldTransform(), target, target);
			
			// O: problem, in order implement time based sine offset, we need absolute time parameter...
			
			
			//Log.d("target = "+target);
			mover.move(p, v, target, dt, result);
			//Log.d("result = "+result);
			e.getKey().setTransform(result, true, dt);
		}
		
		// transform target to world
		// calculate world position directly
		
		// is there ever any reason to define parent object trans
		
		// for each object
		//   determine current state (positional world location/velocity at the moment)
		//   determine target state
		//   use mover to calculate object state at requested time
		//   update object 
	}
	
	// composer attaches objects under parent locations
	// and provides a transition effect
	
	public boolean isFull() {
		
		return available.isEmpty();
	}
	
	//Matrix4f tmp = new Matrix4f();
	
	public void bind(Obj obj) {

		obj.setParent(parent);
		
		//Vector3f target = available.remove();
		bound.put(obj, available.remove());
		/*
		// perhaps it is easiest to just apply gravity
		// that pulls towards the target point (with optionally limited velocity)
		
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
		*/
		
		// this object needs to be attached to the world
		
		// when object is attached, get its current p/v relative to this object
		// (we need p/v for parent object as well...)
		
		// TODO Auto-generated method stub
		//return super.attach(objects);
	}

	
}

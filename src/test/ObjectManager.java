package test;

import java.util.*;

import org.lwjgl.util.vector.*;

import test.Program.Uniform;

public class ObjectManager {

	public interface Event {
		// return true if event has completed
		boolean update(double t, long time_ns);
	}
	
	public class Object {
		// can specify various parameters as time-based functions
		// attach directly to shader uniforms?
		
		private Func.M4 fTransform;
		private Program program;       // can be null
		private Drawable drawable;     // can be null
		private Object parent;         // can be null
		private List<Object> objects;  // can be null
		// directly contol simple variables 
		private Map<Uniform, Func.P> ufP;

		
		// (hmmm...  objects list is not really needed as we keep objects
		//  in byProgram map for drawing and only traverse object transformation
		//  hierarchy through parent pointers)
		Matrix4f transform = new Matrix4f();
		long transform_time_ns = 0;

		private List<Event> events;    // can be null
		
		void updateEvents(double t, long time_ns) {
			if (events != null) {
				for (Event e : events) {
					if (e.update(t, time_ns)) {
						events.remove(e);
						updateEvents(t, time_ns);
						return;
					}
				}
			}
		}
		
		//TODO
		// some separation of concerns:
		
		// 1) controllers (script/physics) update for time t0
		//    - all local model transforms
		//    - other model rendering parameters
		
		// 2) render preparation step computes model-to-world transformations
		//    and prepares direct buffers for instanced rendering calls
		
		// 3) rendering step draws scene at time t0
		
		// 4) running parallel with 3, steps 1 & 2 are started for t1
		
		// render preparation and rendering steps should be completely
		// independent of complexities of step 1
		// (currently some complexity is leaked here, like time-based transform and event updating)
		
		// some complications: how should we provide generic information
		// about object positional/rotational velocity/acceleration needed by controllers?
		// ok, so the problem is that object controller doesn't necessarily know full world
		// transformation so it cannot provide world velocity..
		
		// since position is part of object's state, perhaps it is best to also store
		// time derivatives of position there as well...
		// should not be too difficult to calc dp/dt from previous frame
		// (hard to say if calculating it for everything might cause perf issues) 
		
		// keep previous world transform
		// world position and velocity are simply determined from diff to prev 
		
		Vector3f getPosition(long time_ns) {
			Matrix4f tf = getTransform(time_ns);
			return new Vector3f(tf.m30, tf.m31, tf.m32);
		}
		
		void clearEvents() {
			if (events != null) {
				events.clear();
			}
		}
		
		Object add(Event event) {
			if (this.events == null) {
				this.events = new ArrayList<>();
			}
			events.add(event);
			return this;
		}
		
		Object set(Uniform u, Func.P func) {
			if (ufP == null) {
				ufP = new IdentityHashMap<>();
			}
			ufP.put(u, func);
			return this;
		}
		
		Object set(Func.M4 fTransform) {
			this.fTransform = fTransform;
			transform_time_ns = 0;
			return this;
		}
		
		Matrix4f getTransform(long time_ns) {

			if (transform_time_ns != time_ns) {
				if (fTransform == null) {
					return parent.getTransform(time_ns);
				}
				fTransform.m4(time_ns/1000000000.0, transform);
				transform_time_ns = time_ns;
				if (parent != null) {
					// apply parent transformation permanently
					// (could we need object's local transformation for something?)
					Matrix4f.mul(parent.getTransform(time_ns), transform, transform);
				}
			}
			return transform;
		}
		
		// object may only have one parent, so 
		// it is detached from previous parent
		private void setParent(Object parent) {
			if (this.parent != null) {
				this.parent.objects.remove(this);
				//throw new IllegalStateException("Object already has parent!");
			}
			this.parent = parent;
			transform_time_ns = 0; // invalidates transform
		}
		public Object(Func.M4 fTransform) {
			this(null, null, fTransform);
		}
		public Object(Program program, Drawable drawable, Func.M4 fTransform) {
			this.fTransform = fTransform;
			this.program = program;
			this.drawable = drawable;
//			for (Object obj : objects) {
//				obj.setParent(this);
//			}
//			this.objects = objects.length > 0 ? Arrays.asList(objects) : null;
			register(this);
		}
		// attach object to registered object -> register object
		// detach object ->  unregister object
		public void detach() {
			setParent(null);
		}
		
		public Object attach(Object... objects) {
			for (Object obj : objects) {
				obj.setParent(this);
			}
			if (this.objects == null) {
				this.objects = new ArrayList<>(Arrays.asList(objects));
			} else {
				this.objects.addAll(Arrays.asList(objects));
			}
			return this;
		}
		// things that are missing (and may be object specific)
		// different textures (too complex issue to think about now)
	}
	
	Map<Program,Map<Drawable, List<Object>>> byProgram = new IdentityHashMap<>();
	List<Object> nonDrawables = new ArrayList<>();

	private void register(Object obj) {
		
		if (obj.program != null && obj.drawable != null) {
			Map<Drawable, List<Object>> byDrawable = byProgram.get(obj.program);
			if (byDrawable == null) {
				byDrawable = new IdentityHashMap<>();
				byProgram.put(obj.program, byDrawable);
			}
			List<Object> objects = byDrawable.get(obj.drawable);
			if (objects == null) {
				objects = new ArrayList<>();
				byDrawable.put(obj.drawable, objects);
			}
			objects.add(obj);
		} else {
			nonDrawables.add(obj);
		}
	}

	// simple draw initially, instanced later (needs program support!)
	
	// this step should prepare stuff to be rendered, not call opengl directly
	// (create buffers that can be rendered directly)
	
	public void drawObjectsAt(View view, long time_ns) {
		double t = time_ns/1000000000.0;
		for (Map.Entry<Program,Map<Drawable, List<Object>>> e1 : byProgram.entrySet()) {
			Program program = e1.getKey();
			program.useView(view);
			
			for (Map.Entry<Drawable, List<Object>> e2 : e1.getValue().entrySet()) {
				Drawable drawable = e2.getKey();
				
				int max = program.maxInstanced();
				int remaining = e2.getValue().size();
				
				Matrix4f tmp_m = new Matrix4f();
				// check if program supports instanced drawing
				if (max > 1) {
					Matrix4f[] model_to_world = new Matrix4f[max];

					int count = 0;
					
					for (Object object : e2.getValue()) {
						
						model_to_world[count++] = object.getTransform(time_ns); // tmp_m; //
						if (count == max || count == remaining) {
							program.useModelTransforms(model_to_world, count);
							drawable.drawInstanced(count);
							remaining -= count;
							count = 0;
						}
//						program.useModelTransform(object.getTransform(time_ns));
//						if (object.ufP != null) {
//							for (Map.Entry<Uniform, Func.P> e : object.ufP.entrySet()) {
//								program.bind(e.getKey(), e.getValue().p(t));
//							}
//						}
//						drawable.draw();
						object.updateEvents(t, time_ns);
					}
					
					
				} else {
					for (Object object : e2.getValue()) {
						program.useModelTransform(object.getTransform(time_ns));
						if (object.ufP != null) {
							for (Map.Entry<Uniform, Func.P> e : object.ufP.entrySet()) {
								program.bind(e.getKey(), e.getValue().p(t));
							}
						}
						drawable.draw();
						object.updateEvents(t, time_ns);
					}
				}
			}
			// program.getIndex(Uniform.)
		}
		for (Object obj : new ArrayList<>(nonDrawables)) {
			obj.updateEvents(t, time_ns);
		}
	}
	
	// draw objects by program/drawable to take advantage of instancing
	
	// maintain (or collect) list of all objects using same drawable and program
	// these can be drawn with an instanced draw call
	// program has to support instanced drawing (uses correct animation for the instance)
	
	
	// takes care of 
	
	// 

}

package test;

import java.nio.FloatBuffer;
import java.util.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.*;

import test.Program.Uniform;

public class ObjManager2 {

	/*
	public static void transform_w0(Matrix4f left, Vector3f right, Vector3f dest) {

		float x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z;
		float y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z;
		float z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z;
		dest.x = x;
		dest.y = y;
		dest.z = z;
	}

	public static void transform_w1(Matrix4f left, Vector3f right, Vector3f dest) {

		float x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z + left.m30;
		float y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z + left.m31;
		float z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z + left.m32;
		dest.x = x;
		dest.y = y;
		dest.z = z;
	}
	*/
	
	public interface Transformable {
		
	}

	
	
	public class Obj {
		
		private Program program;       // can be null
		private Drawable drawable;     // can be null
		
		// if parent != null, this object is positioned relative to the parent object
		private Obj parent;         // can be null

		// current model-to-parent or model-to-world transform
		private Matrix4f transform; // new Matrix4f();
		
		// true if transform is the final model-to-world transform
		private boolean model_to_world;

		// transformation used in previous time step
		// (can be used to determine object positional and rotational velocities)
		private Matrix4f prev_transform; // new Matrix4f();
		
		// needed for velocity calculations (where do we get this?)
		private long time;
		private long prev_time;
		
		// simple variables (probably needs to be redesigned when I know what I am doing...)
		private Map<Uniform, Float> values = new HashMap<>();
		
		// (hmmm...  objects list is not really needed as we keep objects
		//  in byProgram map for drawing and only traverse object transformation
		//  hierarchy through parent pointers)

//		public Matrix4f getTransform() {
//			return transform;
//		}
		
		public Obj setTransform(Matrix4f m, boolean model_to_world, long time) {
			
			if (this.time < time) {
				this.prev_time = this.time;
				this.prev_transform = transform;
			}
			this.time = time;
			transform = m;
			this.model_to_world = model_to_world;
			return this;
		}
		
		public Obj set(Uniform u, float value) {
			values.put(u, value);
			return this;
		}
		
		
		
		//TODO
		// some separation of concerns:
		
		// 1) controllers (script/physics) update for time t0
		//    - all local model transforms
		//    - other model rendering parameters
		//    - controllers may set either model-to-parent or model-to-world transform directly
		
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
		
		Vector3f getWorldPosition() {
			
			Matrix4f m = getWorldTransform();
			return new Vector3f(m.m30, m.m31, m.m32);
		}
		
		Vector3f getWorldVelocity() {
			
			Matrix4f m1 = getWorldTransform();
			Matrix4f m0 = prev_transform;
			return (Vector3f)new Vector3f(
					m1.m30 - m0.m30,
					m1.m31 - m0.m31,
					m1.m32 - m0.m32).scale( 1000000000.0f / (time - prev_time));
		}
		
		Matrix4f getWorldTransform() {
			
			if (transform == null) {
				transform = new Matrix4f();
			}
			if (!model_to_world && parent != null) {
				Matrix4f.mul(parent.getWorldTransform(), transform, transform);
				model_to_world = true;
			}
			return transform;
		}
		
		// object may only have one parent, so 
		// it is detached from previous parent
		public void setParent(Obj parent) {
			// before changing parent, make sure object has self-contained world-transform
			getWorldTransform();
			this.parent = parent; // world transform does not change (by default, of course caller can reset transform after this call)
		}
		
		public Obj() {
		}
		
		public Obj(Program program, Drawable drawable) {
			this.program = program;
			this.drawable = drawable;
//			for (Object obj : objects) {
//				obj.setParent(this);
//			}
//			this.objects = objects.length > 0 ? Arrays.asList(objects) : null;
			register(this);
		}
		
		// things that are missing (and may be object specific)
		// different textures (too complex issue to think about now)
	}
	
	Map<Program,Map<Drawable, List<Obj>>> byProgram = new IdentityHashMap<>();
	List<Obj> nonDrawables = new ArrayList<>();

	private void register(Obj obj) {
		
		if (obj.program != null && obj.drawable != null) {
			Map<Drawable, List<Obj>> byDrawable = byProgram.get(obj.program);
			if (byDrawable == null) {
				byDrawable = new IdentityHashMap<>();
				byProgram.put(obj.program, byDrawable);
			}
			List<Obj> objects = byDrawable.get(obj.drawable);
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

	public class Buffers {
		
		public class DrawEntry {
			final Program program;
			final Drawable drawable;
			final int instanceCount;
			public DrawEntry(Program program, Drawable drawable, int instanceCount) {
				this.program = program;
				this.drawable = drawable;
				this.instanceCount = instanceCount;
			}
		}
		private FloatBuffer transforms;
		private List<DrawEntry> entries = new ArrayList<>();
		public Buffers(int max) {
			transforms = BufferUtils.createFloatBuffer(max*4*4);
		}
		public void clear() {
			entries.clear();
			transforms.clear();
		}
		public void draw(View view) {
			for (DrawEntry e : entries) {
				e.program.useView(view);
				int instancesRemaining = e.instanceCount;
				while (instancesRemaining > 0) {
					int batch_size = Math.min(instancesRemaining, e.program.maxInstanced());
					transforms.limit(transforms.position() + batch_size*4*4);
					e.program.setUniform(Uniform.U_MODEL_TO_WORLD_M4, transforms);
					transforms.position(transforms.limit());
					e.drawable.drawInstanced(batch_size);
					instancesRemaining -= batch_size;
				}
			}
		}
		
		// add multiple objects using same drawable
		public void add(Program program, Drawable drawable, List<Obj> objects) {
			
			entries.add(new DrawEntry(program, drawable, objects.size()));
			for (Obj o : objects) {
				o.getWorldTransform().store(transforms);
			}
		}
		// Matrix4f world_to_projected
		// instance count
	}
	
	// 2) render preparation step computes model-to-world transformations
	//    and prepares direct buffers for instanced rendering calls
	public void prepareBuffers(Buffers b) {
		
		for (Map.Entry<Program,Map<Drawable, List<Obj>>> e1 : byProgram.entrySet()) {
			Program program = e1.getKey();
			for (Map.Entry<Drawable, List<Obj>> e2 : e1.getValue().entrySet()) {
				Drawable drawable = e2.getKey();
				b.add(program, drawable, e2.getValue());
			}
		}
	}
	
	// draw objects by program/drawable to take advantage of instancing
	
	// maintain (or collect) list of all objects using same drawable and program
	// these can be drawn with an instanced draw call
	// program has to support instanced drawing (uses correct animation for the instance)
	
	
	// takes care of 
	
	// 

}

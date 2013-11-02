package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;

public class ObjectManager {

	public class Object {
		private Animation[] animation;
		private Program program;       // can be null
		private Drawable drawable;     // can be null
		private Object parent;         // can be null
		private List<Object> objects;  // can be null
		// (hmmm...  objects list is not really needed as we keep objects
		//  in byProgram map for drawing and only traverse object transformation
		//  hierarchy through parent pointers)
		Matrix4f transform = new Matrix4f();
		long transform_time_ns = 0;
		
		void setAnimation(Animation... animation) {
			this.animation = animation;
			//transform.setIdentity();
			transform_time_ns = 0;
		}
		
		Matrix4f getTransform(long time_ns) {

			if (transform_time_ns != time_ns) {
				transform.setIdentity();
				for (Animation a : animation) {
					a.updateTransform(time_ns, transform);
				}
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
		public Object(Animation animation) {
			this(null, null, animation);
		}
		public Object(Program program, Drawable drawable, Animation... animation) {
			this.animation = animation;
			this.program = program;
			this.drawable = drawable;
//			for (Object obj : objects) {
//				obj.setParent(this);
//			}
//			this.objects = objects.length > 0 ? Arrays.asList(objects) : null;
			register(this);
		}
		public Object attach(Object... objects) {
			for (Object obj : objects) {
				obj.setParent(this);
			}
			if (this.objects == null) {
				this.objects = Arrays.asList(objects);
			} else {
				this.objects.addAll(Arrays.asList(objects));
			}
			return this;
		}
		// things that are missing (and may be object specific)
		// different textures (too complex issue to think about now)
	}
	
	Map<Program,Map<Drawable, List<Object>>> byProgram = new IdentityHashMap<>();

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
		}
		
		// group by program, then by drawable
		
	}

	// simple draw initially, instanced later (needs program support!)
	public void drawObjectsAt(View view, long time_ns) {
		for (Map.Entry<Program,Map<Drawable, List<Object>>> e1 : byProgram.entrySet()) {
			Program program = e1.getKey();
			program.useView(view);
			for (Map.Entry<Drawable, List<Object>> e2 : e1.getValue().entrySet()) {
				Drawable drawable = e2.getKey();
				for (Object object : e2.getValue()) {
					program.useModelTransform(object.getTransform(time_ns));
					drawable.draw();
				}
			}
			// program.getIndex(Uniform.)
		}
		
		
	}
	
	// draw objects by program/drawable to take advantage of instancing
	
	// maintain (or collect) list of all objects using same drawable and program
	// these can be drawn with an instanced draw call
	// program has to support instanced drawing (uses correct animation for the instance)
	
	
	// takes care of 
	
	// 

}

package test;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;

import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import test.ObjManager2.Buffers;
import test.ObjManager2.Obj;
import test.Program.Uniform;
import test.Textures.Texture2D;
import test.Textures.TextureCubeMap;



public class Test2d {
	
	static {
		System.setProperty("org.lwjgl.util.NoChecks", "true");
	}
	
	Test2d() throws Exception {
		
	}

	
	static Random rnd = new Random(2434);
	
	Vector3f v = new Vector3f();
	Vector3f zero = new Vector3f();
	
	public static Vector3f rnd(Vector3f v, float r) {
		v.x = (rnd.nextFloat() - 0.5f)*2*r;
		v.y = (rnd.nextFloat() - 0.5f)*2*r;
		v.z = (rnd.nextFloat() - 0.5f)*2*r;
		return v;
	}

	public static Vector3f rnd(float r) {
		return rnd(new Vector3f(),r);
	}
	
	public static void main(String[] args) throws Exception {
		new Test2d().run();
	}

	int N = 3;
	
	//Deque<Obj> reserve = new ArrayDeque<>(N);
	//Deque<Obj> fallingBlocks = new ArrayDeque<>(N);
	ArrayList<Obj> objects = new ArrayList<>();

	// final Func.M4 farAway = new Simple.Position(0, new Vector3f(10000, 0, 0));

	final Vector3f farAwayPos() {
		
		Vector3f p = rnd(50);
		//Vector3f p = rnd(5);
		p.y += 100; 
		return p;
	}
	
	final Func.M4 farAway() {
		
		return new Simple.Position(0, farAwayPos());
	}

	
	
	int nextFb = 0;
	double fbInterval = 0.01;

	double nextCharMinTime = 0;
	double charInterval = 1.3;

	
    Vector3f p0 = new Vector3f();
    Vector3f v0 = new Vector3f();
    Vector3f p1 = new Vector3f();
    Vector3f v1 = new Vector3f();

    ObjManager2 om = new ObjManager2();
    
	//Interpolator ip = new SmoothVelocity(0, 10);
	//Obj someObj = om.new Obj(null);

    List<Controller> controllers = new ArrayList<>();
	
	// this creates new parent object for the char
	// and binds subobjects to it
    

	private static Vector3f tmpVec3f = new Vector3f();
    
    public static void move(View camera, Obj selected) {

    	float scale = 0.005f;
    	
		int dx = Mouse.getDX();
		int dy = Mouse.getDY();
		if (dx != 0 || dy != 0) {
			if (Mouse.isButtonDown(0)) {
				camera.rotateView(dx*scale, dy*scale);
			} else if (Mouse.isButtonDown(1)) {
				camera.translateView(dx*scale, dy*scale, 0);
			}
		}
		float step = 0.3f;
		
		if (!Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				camera.translateView(-step, 0, 0);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				camera.translateView(step, 0, 0);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				camera.translateView(0,  0, -step);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				camera.translateView(0,  0, step);
			}
			if (selected != null) {
				selected.getWorldPosition(tmpVec3f);
				if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
					tmpVec3f.x -= 0.1;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
					tmpVec3f.x += 0.1;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
					tmpVec3f.z -= 0.1;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
					tmpVec3f.z += 0.1;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
					tmpVec3f.y -= 0.1;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_INSERT)) {
					tmpVec3f.y += 0.1;
				}
				selected.setTransform(tmpVec3f, true, 0);
			}
		}
    }
	
	public void run() throws Exception {
		
		Display.setDisplayMode(new DisplayMode(800, 400));
		Display.setVSyncEnabled(true);
		//Display.setFullscreen(true);
		Display.setTitle("Test 2d render");
		Display.create(new PixelFormat(), new ContextAttribs(4, 3).withProfileCore(true).withForwardCompatible(true));
		Display.setResizable(true);

		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
		
        //Program glass = new Program("assets/shaders/glass/vertex.glsl", "assets/shaders/glass/geometry.glsl", "assets/shaders/glass/fragment.glsl");
        Program glass = new Program("assets/shaders/glass2");
        Program noise = new Program("assets/shaders/noise");
        Program std = new Program("assets/shaders/std");
        //Program glass = new Program("assets/shaders/glass/vertex.glsl", null, "assets/shaders/glass/fragment.glsl");
        Program background = new Program("assets/shaders/background");
        
        Quad quad1 = new Quad(1);
        
        // TODO: specify background rect in projected space to 
        Rect bgRect = new Rect(2, 1, -0.5f);
        

		View lightView = new View();
		View camera = new View();
		camera.translateView(0, 0, 2);
		
        Lights lights = new Lights();
		//view.translateView(40, 30, -50);
		
        //glGetPr
        
		TextureCubeMap envCube = new TextureCubeMap("assets/images/env1");
        noise.bind(Uniform.U_ENV_CUBE, envCube);
		background.bind(Uniform.U_ENV_CUBE, envCube);
		

		Texture2D imgTexture = new Texture2D("assets/images/ash_uvgrid01.png");
		std.bind(Uniform.U_ENV_CUBE, envCube);
		//std.bind(Uniform.U_TEXTURE_1, imgTexture);
		// std.bind(Uniform.U_TEXTURE_2, imgTU);
		
		//view.setProjection(60, 0.1f, 100f, Display.getWidth(), Display.getHeight());
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		Log.e("asdf %s", "hello");

        glEnable(GL_BLEND);
		glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        //glEnable(GL_CULL_FACE);
        //glCullFace(GL_FRONT);
        glDisable(GL_CULL_FACE);


		
        Obj selected = null;
		

        long frame = 0;
    	final long initTime = System.nanoTime();
    	long frameStart = initTime;
    	long prevPrintTime = frameStart;
    	final List<Long> frameTimes = new ArrayList<>(500);
    	
    	Buffers buffers = new Buffers(N + 10);

		FrameBuffer frameBuffer = new FrameBuffer(4*512, 4*512);

		Obj q1 = om.new Obj(std, quad1);
		q1.setTransform(new Vector3f(2,0,0), true, 0);
		
    	// some temp testing stuff
//    	TODO: make proper sine scroller
//    	      could also apply some functions to the target point (like y = base + sin(C*t*x))
    	//List<Binder> binders = new ArrayList<>();
    	
		while (!Display.isCloseRequested()) {

			final long time_ns = frameStart - initTime;
			final double t = time_ns/1000000000.0;
			// start two processes in parallel:
			// 1) start rendering current state
			// 2) perform user input checking and
			//   calculate new state based on that and active animations/pseudo-physics
			
			move(camera, selected);
			
			
	// prepare geometry for drawing
			buffers.clear();
			// after object transformations and other attributes have been updated for this frame
			// we can prepare rendering buffers
			om.prepareBuffers(buffers);
			
	// move lights (TODO: these should be controlled just like geometry) 
			lights.setWorldLight((float)(10*Math.sin(frame*0.01f)), 10, (float)(10*Math.cos(frame*0.01f)));
			//lights.setWorldLight(-40, 0, 0);
			//lightMarker.setTransform(lights.point_light_1, true, 0);
			std.bind(Uniform.U_TEXTURE_1, imgTexture);
			

	// copy data to texture
	//

	// to use shadow map, shader needs:
	// the shadow map depth texture, naturally
	// transformation from world coordinates to depth texture (x,y,depth) for lookups
			
		// render shadow map
			// TODO: set lightview to match some light position and direction
			frameBuffer.selectAsRenderTarget();
			lightView.look(lights.point_light_1, new Vector3f(0,0,0));
			lightView.setProjection(60, 2f, 40f, frameBuffer.w, frameBuffer.h);
			//lightView.translateView(0, 0, -0.01f);

			glDepthRange(0, 1);
			glViewport(0, 0, frameBuffer.w, frameBuffer.h);

			glClearColor(255/255.0f, 105/255.0f, 180/255.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			glEnable(GL_DEPTH_TEST);
			
			glass.bind(Uniform.U_SHADOW_MAP_1, frameBuffer.depth);
			glass.bind(Uniform.U_WORLD_TO_SHADOW_M4, lightView.world_to_projected);
			
			//glDisable(GL_DEPTH_TEST);
			// TODO: we probably want to use cheapest possible shaders for shadow map generation...
			buffers.draw(lightView, null, t);
			
			// what to do with frameBuffer?
			// to test, render a quad that uses the resulting texture
			//lightView.projection
			//lightView.world_to_view
			//glass.bind(Uniform.U_SHADOW_MAP_1, frameBuffer.depth);
			//glass.bind(Uniform.U_WORLD_TO_SHADOW_M4, lightView.world_to_projected);
			
			camera.setProjection(50, 0.1f, 100f, Display.getWidth(), Display.getHeight());
			
			glViewport(0, 0, Display.getWidth(), Display.getHeight());
			FrameBuffer.setDefaultRenderTarget();

			//std.bind(Uniform.U_TEXTURE_1, frameBuffer.color);
			//std.bind(Uniform.U_SHADOW_MAP_1, frameBuffer.depth);
			std.bind(Uniform.U_TEXTURE_1, frameBuffer.depth);
			
			// manually updating view for every program... refactor when there are more programs
			//glass.useView(view);
			background.useView(camera);
			background.useTime(t);
			
			glClearColor(255/255.0f, 105/255.0f, 180/255.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			//glActiveTexture(GL_TEXTURE0 + view.envCubeSampler);
			//glBindTexture(GL_TEXTURE_CUBE_MAP, view.envCubeTexture);
			
		//	TODO: render to texture, then draw that texture to screen
		//	(also draw depth buffer, just to visualize it)
			
	        glDisable(GL_DEPTH_TEST);
			background.useModelTransform(camera.view_to_world);
			bgRect.draw();
			
			glEnable(GL_DEPTH_TEST);
			buffers.draw(camera, lights, t);
	        
	        
			//om.drawObjectsAt(view, time_ns);
			
			// if drawing multiple things with same program,
			// pass all transformations and drawables to program in one call
			
			// 400 is bit too much
			
//			glass.useModelTransform(new Matrix4f().translate(new Vector3f(0, 0, -2)));
//			cube.draw();
//			glass.useModelTransform(new Matrix4f().translate(new Vector3f(0, 1, -2)));
//			cube.draw();
//			glass.useModelTransform(new Matrix4f().translate(new Vector3f(0, -1, -2)));
//			cube.draw();
//			
//			for (int i = 0; i < 1000; i++) {
//				glass.useModelTransform(new Matrix4f().translate(new Vector3f((rnd.nextFloat() - 0.5f)*20, (rnd.nextFloat() - 0.5f)*20, (rnd.nextFloat() - 0.5f)*20)));
//				cube.draw();
//			}
//
//			//glass.useModelTransform(new Matrix4f().translate(new Vector3f(2, 0, -2)).rotate(frame*0.005f, new Vector3f(0, 1, 0)));
//			glass.useModelTransform(new Matrix4f().rotate(frame*0.005f, new Vector3f(0, 1, 0)).translate(new Vector3f(2, 0, -2)));
//			cube.draw();
			
            //List<TransformedRenderable> drawList = new LinkedList<TransformedRenderable>();
            //drawList.addAll(staticGeometry);
            //drawList.add(new TransformedRenderable(cube, new Animation(0, 0, -20 + 10*(float)Math.cos(t*0.13), 0, 0, 0)));
            //drawList.add(new TransformedRenderable(wall, new Animation(3, 2, angle / 10)));
            //drawList.add(new TransformedRenderable(ball2, new Animation(x+1, y+1, angle)));
            //drawList.add(new TransformedRenderable(sph, new Animation(bx, by, 0, angle / 10, angle/15, angle/20)));			
			//System.out.println("mouse : ("+Mouse.getX()+", "+Mouse.getY()+")");
			//glClear( GL_COLOR_BUFFER_BIT );
 
			//Display.sync(60);
			Display.update();
			frame++;
	    	long time = System.nanoTime();
	    	long dt_ns = time-frameStart;
	    	frameTimes.add(dt_ns);
	    	frameStart = time;

	    	float dt = (float)(dt_ns / 1000000000.0);
	    	double time_s = time / 1000000000.0;
	    	for (Controller c : controllers) {
	    		c.step(time_s, dt);
	    	}
	    	
	    	
	    	long d_ns = time - prevPrintTime;
	    	if (d_ns > 2000*1000000L) {
	    		
	    		double d_ms = d_ns/1000000.0;
	    		int frames = frameTimes.size(); //frame - prevPrintFrame;
	    		Collections.sort(frameTimes);
	    		
	    		if (frames > 0) {
	    			long med = ((frames & 1) == 1) ? frameTimes.get((frames - 1) / 2) : (frameTimes.get(frames/2) + frameTimes.get(frames/2 - 1))/2;
			    	Log.d(String.format("Last %.2f s frametime avg/med/min/max : %.1f/%.1f/%.1f/%.1f ms",
			    			d_ms/1000,d_ms/frames, med/1000000.0, frameTimes.get(0)/1000000.0, frameTimes.get(frames - 1)/1000000.0));
	    		} else {
	    			Log.d(String.format("Last %.2f s no frames rendered", d_ms*1000));	    			
	    		}
	    		
	    		prevPrintTime = time;
	    		//prevPrintFrame = frame;
	    		frameTimes.clear();
	    	}
			
		}
		Display.destroy();
	}
	
}

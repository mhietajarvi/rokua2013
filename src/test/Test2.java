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


public class Test2 {
	
	
	
	Test2() throws Exception {
		
	}

	static Random rnd = new Random(2434);
	
	public static void main(String[] args) throws Exception {
		new Test2().run();
	}

	

    ObjManager2 om = new ObjManager2();
    
	
	public void run() throws Exception {
		
		Display.setDisplayMode(new DisplayMode(800, 400));
		Display.setVSyncEnabled(true);
		//Display.setFullscreen(true);
		Display.setTitle("Rokua2013");
		Display.create(new PixelFormat(), new ContextAttribs(3, 2).withProfileCore(true).withForwardCompatible(true));
		Display.setResizable(true);
		
		
        Program std = new Program("assets/shaders/std");
        Program background = new Program("assets/shaders/background");
        
        Quad quad1 = new Quad(1);
        
        Rect bgRect = new Rect(2, 1, -0.5f);
        

		View lightView = new View();
		View camera = new View();
		
		int cubeTexture = Textures.loadCubeTexture("assets/images/env1", 0);
		background.bind(Uniform.U_ENV_CUBE, 0);
		
		int imgTexture = Textures.loadTexture("assets/images/ash_uvgrid01.png", 1);
		std.bind(Uniform.U_ENV_CUBE, 0);
		std.bind(Uniform.U_TEXTURE_1, 1);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		
        //glEnable(GL_BLEND);
		glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        //glEnable(GL_CULL_FACE);
        //glCullFace(GL_FRONT);
        glDisable(GL_CULL_FACE);

		

        long frame = 0;
    	final long initTime = System.nanoTime();
    	long frameStart = initTime;
    	long prevPrintTime = frameStart;
    	final List<Long> frameTimes = new ArrayList<>(500);
    	
    	Buffers buffers = new Buffers(10);

		//FrameBuffer frameBuffer = new FrameBuffer(300, 300);			

		Obj q1 = om.new Obj(std, quad1);
		q1.setTransform(new Vector3f(0,0,-1), true, 0);
		
    	
    	float scale = 0.005f;
		while (!Display.isCloseRequested()) {

			final long time_ns = frameStart - initTime;
			final double t = time_ns/1000000000.0;
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
					camera.translateView(step, 0, 0);
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
					camera.translateView(-step, 0, 0);
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
					camera.translateView(0,  0, step);
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
					camera.translateView(0,  0, -step);
				}
			}
			
	// prepare geometry for drawing
			buffers.clear();
			// after object transformations and other attributes have been updated for this frame
			// we can prepare rendering buffers
			om.prepareBuffers(buffers);
			
			camera.setProjection(60, 0.1f, 1000f, Display.getWidth(), Display.getHeight());
			glViewport(0, 0, Display.getWidth(), Display.getHeight());

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
			buffers.draw(camera, null, t);
	        
	        
			Display.update();
			frame++;
	    	long time = System.nanoTime();
	    	long dt_ns = time-frameStart;
	    	frameTimes.add(dt_ns);
	    	frameStart = time;

	    	float dt = (float)(dt_ns / 1000000000.0);
	    	double time_s = time / 1000000000.0;
	    	
	    	
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

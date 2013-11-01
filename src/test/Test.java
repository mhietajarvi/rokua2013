package test;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
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
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;


/*
 * ideas:
 * 
 *  text scroller, where each letter is composed of random arrangement of solids
 *  and solids behave a bit like asteroids
 *  
 *  (solids turn transparent dynamically, possible?)
 * 
 *  required for that:
 *  - better drawable/transformation management system
 *  - repurpose used solids for new letters on the fly
 *  - 
 * 
 * 
 *  transparent objects
 *  
 *  - how transparency work in the first place
 *  - rendering order must be managed manually
 * 
 *  (real glass ball)
 * 
 *  - advanced:
 *    - render back side of transparent object, store z and surface normal for each fragment
 *    - render front face
 *      - shoot ray to back side buffer, bounce from back side and sample back z/color (or env cube map) with refracted ray 
 *      - calculate reflected ray, sample env map with it
 *     
 *    (so env map reflection is standard stuff, novel thing is storing the back side geometry and calculating refracted rays) 
 *     - 
 *    (look up texel in cube map with direction)
 *     
 *  calculate , lookup to z/color buffers framebuffer
 * 
 */

public class Test {

	static FloatBuffer floatBuffer(float... data) {
		FloatBuffer buf = BufferUtils.createFloatBuffer(data.length);
		buf.put(data);
		buf.flip();
		return buf;
	}
	
	public static void exitOnGLError(String errorMessage) {
		int errorValue = glGetError();
		if (errorValue != GL_NO_ERROR) {
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);
			if (Display.isCreated()) Display.destroy();
			System.exit(-1);
		}
	}
	
	/*
	public static void loadCubeTexture(String directory) throws IOException {

		InputStream is = new FileInputStream(new File(directory, "top.jpg"));
		ImageIOImageData imageData = new ImageIOImageData();
    	ByteBuffer textureBuffer = imageData.loadImage(new BufferedInputStream(is), false, null); // new int[]{}
		
		int texture = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
		
        glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0,
                GL_RGBA8,
                imageData.getWidth(),
                imageData.getHeight(),
                0, 
                imageData.getDepth() == 32 ? GL_RGBA : GL_RGB,
                GL_UNSIGNED_BYTE,
                textureBuffer);
        
        // glTex
    	
		
		//TextureLoader.getTexture("JPG", in)
		
		//BufferedImage img = ImageIO.read(new File(directory, "top.jpg"));
		//Raster r = img.getData();
		// r.
		//glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, internalformat, width, height, border, format, type, pixels_buffer_offset);
		
	}*/
	
	public static void main(String[] args) throws Exception {

		Display.setDisplayMode(new DisplayMode(300, 200));
		Display.setVSyncEnabled(false);
		Display.setTitle("Rokua2013");
		Display.create(new PixelFormat(), new ContextAttribs(3, 2).withProfileCore(true).withForwardCompatible(true));
		Display.setResizable(true);

		glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
		
		System.out.println("OpenGL version: " + glGetString(GL_VERSION));

		// Map the internal OpenGL coordinate system to the entire screen
		exitOnGLError("setupOpenGL");
		
//		FloatBuffer vertexData = floatBuffer(
//			-1.0f, -1.0f, 0.0f,
//			 1.0f, -1.0f, 0.0f,
//			 0.0f,  1.0f, 0.0f);
//		
//		int vertexbuffer = glGenBuffers();
//		glBindBuffer(GL_ARRAY_BUFFER, vertexbuffer);
//		glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
		
		
		
		// glTexI
		
		//RokuaRenderer r = new RokuaRenderer();
		exitOnGLError("A");
        Program glass = new Program("assets/shaders/glass/vertex.glsl", "assets/shaders/glass/fragment.glsl");
        Program background = new Program("assets/shaders/background/vertex.glsl", "assets/shaders/background/fragment.glsl");
		exitOnGLError("B");
        
        // GLU.gluLookAt(eyex, eyey, eyez, centerx, cente§ry, centerz, upx, upy, upz);
        Cube cube = new Cube(0.5f);
        
        // TODO: specify background rect in projected space to 
        Rect bgRect = new Rect(1, 1, -0.5f);
        
		exitOnGLError("C");
        //Drawable dCube = cube.prepare(p);
		exitOnGLError("D");

		View view = new View();

		// ugly as hell
		view.loadCubeTexture("assets/images/env2");
		
		//view.setProjection(60, 0.1f, 100f, Display.getWidth(), Display.getHeight());
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		//dCube.d
		
        //r.initPrograms();
        
        
        
		exitOnGLError("E");
        
//		LOG.debug("debug");
//		LOG.info("info");
//		LOG.warn("warn");
//		LOG.error("error");
		//LogManager.
        Log.e("asdf %s", "hello");
        
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        //glDisable(GL_CULL_FACE);

        
//    	float red = 0.9f;
//    	float green = 0.2f;
//    	float blue = 0.2f;

        long frame = 0;
    	long startTime = System.nanoTime();
    	long prevPrintTime = startTime;
    	long prevPrintFrame = frame;
    	List<Long> frameTimes = new ArrayList<>(500);
    	
    	float scale = 0.005f;
		while (!Display.isCloseRequested()) {

			Random rnd = new Random(2434);
			
			// start two processes in parallel:
			// 1) start rendering current state
			// 2) perform user input checking and
			//   calculate new state based on that and active animations/pseudo-physics
			
			int dx = Mouse.getDX();
			int dy = Mouse.getDY();
			if (dx != 0 || dy != 0) {
				if (Mouse.isButtonDown(0)) {
					view.rotateView(dx*scale, dy*scale);
				} else if (Mouse.isButtonDown(1)) {
					view.translateView(dx*scale, dy*scale, 0);
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				view.translateView(0.1f, 0, 0);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				view.translateView(-0.1f, 0, 0);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				view.translateView(0,  0, 0.1f);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				view.translateView(0,  0, -0.1f);
			}
			
			view.setWorldLight(1, (float)(2*Math.sin(frame*0.01f)), -1);

			
			view.setProjection(60, 0.1f, 100f, Display.getWidth(), Display.getHeight());
			glViewport(0, 0, Display.getWidth(), Display.getHeight());

			// manually updating view for every program... refactor when there are more programs
			glass.useView(view);
			background.useView(view);
			
			//glClearColor(red, green, blue, 1.0f);
			//glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
            // rr.setLightPos(x2, y2, 3+x);
			
	    	glActiveTexture(GL_TEXTURE0 + view.envCubeSampler);
			glBindTexture(GL_TEXTURE_CUBE_MAP, view.envCubeTexture);

			//view.world_to_view
			// need view-to-world transformation
			//Matrix4f view_to_world = new Matrix4f(world_to_view);
//			Matrix4f world_to_projected = Matrix4f.mul(view.projection, view.world_to_view, null);
			
			// update background to cover view
			//view.projection
//			view_to_world.m30 = 0;
//			view_to_world.m31 = 0;
//			view_to_world.m32 = 0;
//			view_to_world.m33 = 1;
//			Matrix4f projected_to_world = Matrix4f.invert(world_to_projected,  null);
//			Vector4f v = Matrix4f.transform(world_to_projected, new Vector4f(1,1,1,1), null);
//			Log.d("world_to_projected:\n"+world_to_projected);
//			Log.d(" transformed:\n"+v);
//			Matrix4f.transform(projected_to_world, new Vector4f(1,1,0,-1), v);
//			Log.d("projected_to_world:\n"+projected_to_world);
//			Log.d(" transformed:\n"+v);
			
			
			background.useModelTransform(view.view_to_world);
			
			//Log.d("model_to_view:\n"+background.model_to_view);
			//Log.d("model_to_projected:\n"+background.model_to_projected);
			
			bgRect.draw();
			

			// if drawing multiple things with same program,
			// pass all transformations and drawables to program in one call
			
			// 400 is bit too much
			
			glass.useModelTransform(new Matrix4f().translate(new Vector3f(0, 0, -2)));
			cube.draw();
			glass.useModelTransform(new Matrix4f().translate(new Vector3f(0, 1, -2)));
			cube.draw();
			glass.useModelTransform(new Matrix4f().translate(new Vector3f(0, -1, -2)));
			cube.draw();
			
			for (int i = 0; i < 1000; i++) {
				glass.useModelTransform(new Matrix4f().translate(new Vector3f((rnd.nextFloat() - 0.5f)*20, (rnd.nextFloat() - 0.5f)*20, (rnd.nextFloat() - 0.5f)*20)));
				cube.draw();
			}

			//glass.useModelTransform(new Matrix4f().translate(new Vector3f(2, 0, -2)).rotate(frame*0.005f, new Vector3f(0, 1, 0)));
			glass.useModelTransform(new Matrix4f().rotate(frame*0.005f, new Vector3f(0, 1, 0)).translate(new Vector3f(2, 0, -2)));
			cube.draw();
			
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
	    	frameTimes.add(time-startTime);
	    	startTime = time;
	    	
	    	long d_ns = time - prevPrintTime;
	    	if (d_ns > 2000*1000000L) {
	    		
	    		double d_ms = d_ns/1000000.0;
	    		int frames = frameTimes.size(); //frame - prevPrintFrame;
	    		Collections.sort(frameTimes);
	    		
	    		if (frames > 0) {
	    			long med = ((frames & 1) == 1) ? frameTimes.get((frames - 1) / 2) : (frameTimes.get(frames/2) + frameTimes.get(frames/2 - 1))/2;
			    	Log.d(String.format("Last %.2f s frametime avg/med/min/max : %.1f/%.1f/%.1f/%.1f ms",
			    			d_ms*1000,d_ms/frames, med/1000000.0, frameTimes.get(0)/1000000.0, frameTimes.get(frames - 1)/1000000.0));
	    		} else {
	    			Log.d(String.format("Last %.2f s no frames rendered", d_ms*1000));	    			
	    		}
	    		
	    		prevPrintTime = time;
	    		prevPrintFrame = frame;
	    		frameTimes.clear();
	    	}
			
		}
		Display.destroy();
	}
}

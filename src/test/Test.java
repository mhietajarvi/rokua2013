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
import java.util.LinkedList;
import java.util.List;

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
	
	public static void main(String[] args) throws Exception {

		Display.setDisplayMode(new DisplayMode(300, 200));
		Display.setVSyncEnabled(true);
		Display.create(new PixelFormat(), new ContextAttribs(3, 2).withProfileCore(true).withForwardCompatible(true));
		Display.setResizable(true);

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
		
		
		//RokuaRenderer r = new RokuaRenderer();
		exitOnGLError("A");
        RenderProgram p = new RenderProgram("vertex_default.glsl", "fragment_default.glsl");
		exitOnGLError("B");
        
        // GLU.gluLookAt(eyex, eyey, eyez, centerx, cente§ry, centerz, upx, upy, upz);
        Cube cube = new Cube(0.4f);
		exitOnGLError("C");
        Drawable dCube = cube.prepare(p);
		exitOnGLError("D");

		View view = new View();
		view.setViewLight(0, 0, 0);
		
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
        

        int t = 0;
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        
    	float red = 0.9f;
    	float green = 0.2f;
    	float blue = 0.2f;
        
    	float scale = 0.005f;
		while (!Display.isCloseRequested()) {

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

			
			view.setProjection(60, 0.1f, 100f, Display.getWidth(), Display.getHeight());
			glViewport(0, 0, Display.getWidth(), Display.getHeight());
			
			glClearColor(red, green, blue, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
            // rr.setLightPos(x2, y2, 3+x);

			dCube.draw(view, new Matrix4f().translate(new Vector3f(0, 0, -2)));
			
            //List<TransformedRenderable> drawList = new LinkedList<TransformedRenderable>();
            //drawList.addAll(staticGeometry);
            //drawList.add(new TransformedRenderable(cube, new Transform(0, 0, -20 + 10*(float)Math.cos(t*0.13), 0, 0, 0)));
            
            
            //r.se
            //drawList.add(new TransformedRenderable(wall, new Transform(3, 2, angle / 10)));
            //drawList.add(new TransformedRenderable(ball2, new Transform(x+1, y+1, angle)));

            //drawList.add(new TransformedRenderable(sph, new Transform(bx, by, 0, angle / 10, angle/15, angle/20)));			
			
			//System.out.println("mouse : ("+Mouse.getX()+", "+Mouse.getY()+")");
			//glClear( GL_COLOR_BUFFER_BIT );
            
    		exitOnGLError("F");
			//r.draw(drawList);
    		exitOnGLError("G");

			Display.sync(60);
    		exitOnGLError("H");
			t++;
			
			// render 3d primitive
			// glVertexAttribPointer
			// glDrawElements(GL_TRIANGLE_STRIP, indices);
			
			//glGen
			Display.update();
    		exitOnGLError("I");
		}
		Display.destroy();
	}
}

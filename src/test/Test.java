package test;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glViewport;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

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

		System.out.println("OpenGL version: " + glGetString(GL_VERSION));

		// Map the internal OpenGL coordinate system to the entire screen
		glViewport(0, 0, 300, 200);
		exitOnGLError("setupOpenGL");
		
//		FloatBuffer vertexData = floatBuffer(
//			-1.0f, -1.0f, 0.0f,
//			 1.0f, -1.0f, 0.0f,
//			 0.0f,  1.0f, 0.0f);
//		
//		int vertexbuffer = glGenBuffers();
//		glBindBuffer(GL_ARRAY_BUFFER, vertexbuffer);
//		glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
		
		
		RokuaRenderer r = new RokuaRenderer();
		exitOnGLError("A");
        RenderProgram p = r.new Program("vertex_default.glsl", "fragment_default.glsl");
		exitOnGLError("B");
        
        // GLU.gluLookAt(eyex, eyey, eyez, centerx, cente§ry, centerz, upx, upy, upz);
        Cube cube = new Cube(0.4f);
		exitOnGLError("C");
        cube.setProgram(p);
		exitOnGLError("D");

		
		
        r.initPrograms();
        
        
        
		exitOnGLError("E");
        
//		LOG.debug("debug");
//		LOG.info("info");
//		LOG.warn("warn");
//		LOG.error("error");
		//LogManager.
        Log.e("asdf %s", "hello");
        

        int t = 0;
		
		while (!Display.isCloseRequested()) {
			
            // rr.setLightPos(x2, y2, 3+x);

            List<TransformedRenderable> drawList = new LinkedList<TransformedRenderable>();
            //drawList.addAll(staticGeometry);
            drawList.add(new TransformedRenderable(cube, new Transform(0, 0, -20 + 10*(float)Math.cos(t*0.13), 0, 0, 0)));
            
            
            //r.se
            //drawList.add(new TransformedRenderable(wall, new Transform(3, 2, angle / 10)));
            //drawList.add(new TransformedRenderable(ball2, new Transform(x+1, y+1, angle)));

            //drawList.add(new TransformedRenderable(sph, new Transform(bx, by, 0, angle / 10, angle/15, angle/20)));			
			
			//System.out.println("mouse : ("+Mouse.getX()+", "+Mouse.getY()+")");
			//glClear( GL_COLOR_BUFFER_BIT );
            
    		exitOnGLError("F");
            
			r.draw(drawList);
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

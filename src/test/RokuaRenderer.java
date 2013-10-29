package test;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.MemoryUtil;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

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
//import static org.lwjgl.opengl.GL40.*;
//import static org.lwjgl.opengl.GL41.*;
//import static org.lwjgl.opengl.GL42.*;
//import static org.lwjgl.opengl.GL43.*;

/*
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
*/

// TODO:
// - wall renderables with intuitive positioning (transform)
// - do we need better camera control?
// - 3d-ball with actual shading
// - anti-alias (low priority, might not even work)


class RokuaRenderer {

    static String read(String file) throws IOException {
    	return new String(Files.readAllBytes(Paths.get(file)), Charset.forName("UTF-8"));
    }
	
	public class PointLight {
		final Vector4f worldPosition; // point light location in world coordinates
		//final float[] viewPosition = new float[4]; // and transformed to view coordinates
		public PointLight(float x, float y, float z) {
			worldPosition = new Vector4f( x, y, z, 1.0f );
		}
	}
	
	// assumes that shader 

	static FloatBuffer floatBuffer(float... data) {
		FloatBuffer buf = BufferUtils.createFloatBuffer(data.length);
		buf.put(data);
		buf.flip();
		return buf;
	}
	
	public class Program implements RenderProgram {
		
	    private int program;
	    //private int vertexHandle;
	    //private int transformHandle;

	    private int hMVPMatrix;
	    private int hMVMatrix;
	    private int hLightPos;
	    private int hPosition;
	    private int hColor;
	    private int hNormal;
	    
	    FloatBuffer mvBuf = BufferUtils.createFloatBuffer(4*4);
	    FloatBuffer mvpBuf = BufferUtils.createFloatBuffer(4*4);
	    //FloatBuffer lightBuf = BufferUtils.createFloatBuffer(4);
	    
	    private final String vertexShaderCode;
	    private final String fragmentShaderCode;

//	    Program(String vertexShaderFile, String fragmentShaderFile) {
//	    	Paths.get(vertexShaderFile)
//	    	Files.readAllBytes(new Path(path)
//	    	// vertexShaderFile.re
//	    }
	    
	    Program(String vertexShaderFile, String fragmentShaderFile) throws IOException {
	    	
	    	this.vertexShaderCode = read(vertexShaderFile);
	    	this.fragmentShaderCode = read(fragmentShaderFile);
	    	programs.add(this); // let renderer (re)init the program as necessary
	    }

	    private int loadShader(int type, String shaderCode) {
	        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	        int shader = glCreateShader(type); 
	        // add the source code to the shader and compile it
	        glShaderSource(shader, shaderCode);
	        glCompileShader(shader);
	        
	        Log.d(glGetShaderInfoLog(shader, 10000));
	        return shader;
	    }
	    
	    // called by renderer if program needs to be (re)initialized
	    void init() {
	    	
	        int vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderCode);
	        int fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode);
	        //LOG.error("loaded vertexShader "+ vertexShader+"\n"+vertexShaderCode);
	        //LOG.error("loaded fragmentShader "+ fragmentShader+"\n"+fragmentShaderCode);
	        
	        program = glCreateProgram();             // create empty OpenGL RenderProgram
	        glAttachShader(program, vertexShader);   // add the vertex shader to program
	        glAttachShader(program, fragmentShader); // add the fragment shader to program
	        glLinkProgram(program);                  // creates OpenGL program executables
	        
	        // get handle to the vertex shader's members
	        //vertexHandle = GLES20.glGetAttribLocation(program, "position");	
	        //transformHandle = GLES20.glGetUniformLocation(program, "transform");
	        
	        // these return -1 if location is not available
	        
	        hMVPMatrix = glGetUniformLocation(program, "u_MVPMatrix");
	        hMVMatrix = glGetUniformLocation(program, "u_MVMatrix");
	        hLightPos = glGetUniformLocation(program, "u_LightPos");

	        hPosition = glGetAttribLocation(program, "a_Position");
	        hColor = glGetAttribLocation(program, "a_Color");
	        hNormal = glGetAttribLocation(program, "a_Normal");
	        Log.d(""+hMVPMatrix+" "+hMVMatrix+" "+hLightPos+" "+hPosition+" "+hColor+" "+hNormal);
	    }
	    
	    private void drawSetupP(RenderState state, FloatBuffer position) {
	    	
	    	//LOG.error("drawSetupP: glUseProgram({})", program);
	    	glUseProgram(program);
	    	
	    	Test.exitOnGLError("a");
	    	
	    	// pass in the light position and transformations
	        state.mMVMatrix.store(mvBuf);
	        mvBuf.flip();
	        state.mMVMatrix.store(mvpBuf);
	        mvpBuf.flip();
	        //state.vVLight.store(lightBuf);

	    	Test.exitOnGLError("b");
	        
            //GLES20.glUniform3fv(hLightPos, 1, state.vVLight, 0);
	        glUniform3f(hLightPos, state.vVLight.x, state.vVLight.y, state.vVLight.z);
	        glUniformMatrix4(hMVMatrix, false, mvBuf);
	        glUniformMatrix4(hMVPMatrix, false, mvpBuf);
//	        glUniformMatrix4fv(hMVMatrix, 1, false, state.mMVMatrix, 0);
//	        glUniformMatrix4fv(hMVPMatrix, 1, false, state.mMVPMatrix, 0);
	    	Test.exitOnGLError("c");

	   // ILMEISESTI JOSKUS ON VOINUT PASSATA SUORAAN POINTERIN, NYKYÄÄN VAIN OFFSETIN BINDATTUUN BUFFERIIN...
	    	System.out.println("address: " +MemoryUtil.getAddress(position));
	    	
	    	//LOG.error("drawSetupP: glVertexAttribPointer({} floats)", position.capacity());
	    	glVertexAttribPointer(hPosition, 3, false, 0, position);
	    	Test.exitOnGLError("d1");
	    	glEnableVertexAttribArray(hPosition);
	    	Test.exitOnGLError("d2");
	    }

	    private void drawSetupPCN(RenderState state, FloatBuffer position, FloatBuffer color, FloatBuffer normal) {

	    	drawSetupP(state, position);
	    	glVertexAttribPointer(hColor, 4, false, 0, color);
	    	glEnableVertexAttribArray(hColor);
	    	glVertexAttribPointer(hNormal, 3, false, 0, normal);
	    	glEnableVertexAttribArray(hNormal);
	    	Test.exitOnGLError("e");
	    }
	    
	    @Override
	    public void draw(RenderState state, FloatBuffer position, int mode, int count) {
	    	
	    	drawSetupP(state, position);
	    	glDrawArrays(mode, 0, count);
	    }
	    
	    @Override
	    public void draw(RenderState state, FloatBuffer position, ShortBuffer index, int mode) {
	    	
	    	drawSetupP(state, position);
	    	glDrawElements(mode, index);
	    }

	    @Override
	    public void draw(RenderState state, FloatBuffer position, FloatBuffer color, FloatBuffer normal, int mode, int count) {
	    	
	    	drawSetupPCN(state, position, color, normal);
	    	glDrawArrays(mode, 0, count);
	    }

		@Override
		public void draw(RenderState state, FloatBuffer position, FloatBuffer color, FloatBuffer normal, ShortBuffer index, int mode) {
	    	drawSetupPCN(state, position, color, normal);
	    	glDrawElements(mode, index);
		}
	}
	
	private float eye[] = { 0, 0 };

	public void setViewCenterPosition(float x, float y) {
		eye = new float[] { x, y };
	}
	
    private List<Program> programs = new LinkedList<Program>();
//    private List<TransformedRenderable> drawList = new LinkedList<TransformedRenderable>();

    
    private Object mutex = new Object();
    
//    public void setDrawList(List<TransformedRenderable> drawList) {
//    	synchronized (mutex) {
//    		this.drawList = new ArrayList<TransformedRenderable>(drawList);
//    	}
//    }
    
	private float red = 0.9f;
	private float green = 0.2f;
	private float blue = 0.2f;

//	private float[] mMVPMatrix = new float[16];
//	private float[] mVMatrix = new float[16];
//	private float[] mProjMatrix = new float[16];
    
//    private RectF box;

//	public void touch(float x, float y) {
//		
//	}
    
	public void setColor(float r, float g, float b) {
		red = r;
		green = g;
		blue = b;
	}

	private final double PI = 3.14159265358979323846;
	private float degreesToRadians(float degrees) {
		return degrees * (float)(PI / 180d);
	}
	private float coTangent(float angle) {
		return (float)(1f / Math.tan(angle));
	}	
	public void initPrograms() {

		Matrix4f projectionMatrix = new Matrix4f();
		float fieldOfView = 60f;
		float aspectRatio = 1; //(float)WIDTH / (float)HEIGHT;
		float near_plane = 0.1f;
		float far_plane = 100f;
		 
		float y_scale = this.coTangent(this.degreesToRadians(fieldOfView / 2f));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = far_plane - near_plane;
		 
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);
		projectionMatrix.m33 = 0;
		state.mPMatrix.load(projectionMatrix);
		Log.d("pmatrix: "+state.mPMatrix);
		Log.d("  "+Matrix4f.transform(state.mPMatrix, new Vector4f(1, 0, -10, 1), null));
		
		for (Program program : programs) {
			program.init();
		}
	}
    
//    public RectF getWorldBox(){
//        return box;
//    }

//	@Override
//	public void onSurfaceChanged(GL10 unused, int w, int h) {
//		GLES20.glViewport(0, 0, w, h);
//        float ratio = (float) w / h;
//        // init static view and projection matrices
//        box = new RectF();
//        box.left = -ratio*10;
//        box.right = ratio*10;
//        box.top = 10;
//        box.bottom = -10;
//        Matrix.frustumM(state.mPMatrix, 0,
//        		box.left, box.right, // left, right
//        		box.bottom, box.top,         // bottom, top
//        		10.00f, 200.00f      // near, far clip distance
//        		);
//    }
	
	public void setLightPos(float x, float y, float z) {
		pointLight = new PointLight(x, y, z);
	}
	
	private PointLight pointLight = new PointLight(0, 0, 0);
	
	private final RenderState state = new RenderState(); //mMMatrix, mVMatrix, mPMatrix, vVLight);
	
	// set rendered state for next frame
	
	public void draw(List<TransformedRenderable> drawList) {
		
		// define the color we want to be displayed as the "clipping wall"
		//glDisableClientState(cap);EnableClientState
		
//        Matrix.setLookAtM(state.mVMatrix, 0,
//        		eye[0], eye[1], 15,      // eye
//        		eye[0], eye[1], 0f,    // center
//        		0f, 1.0f, 0.0f // up
//        		);
		
		glClearColor(red, green, blue, 1.0f);
		// clear the color buffer to show the ClearColor we called above...
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		//glDisable();
		//GLES20.glEnable(GLES20.GL_CULL_FACE);
		//glDisable(GL_CULL_FACE);
		//GLES20.glEnable(GLES20.GL_BLEND);
		//glDisable(GL_DEPTH_TEST);
		//GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		//Test.exitOnGLError("setupOpenGL");

		//GLES20.glCullFace(GLES20.GL_FRONT);
		//GLES20.glCullFace(GLES20.GL_BACK);
        // Create a rotation for the triangle
        //long time = SystemClock.uptimeMillis() % 4000L;
        //float angle = 0.090f * ((int) time);

        // Calculate position of the light. Rotate and then push into the distance.
		//initLights();
		//PointLight light = pointLight;

        //Matrix.multiplyMV(state.vVLight, 0, state.mVMatrix, 0, pointLight.worldPosition, 0);

		Matrix4f.transform(state.mVMatrix, pointLight.worldPosition, state.vVLight);

		// LOG.error("ASDF");
        
        // render all queued primitives
        
        // for each primitive, set up state
        
    	synchronized (mutex) {
	        for (TransformedRenderable r : drawList) {
	        	
	        	Matrix4f.mul(state.mVMatrix, r.getTransform().getMatrix(), state.mMVMatrix);
	        	Matrix4f.mul(state.mPMatrix, state.mMVMatrix, state.mMVPMatrix);
	        	
//	        	Matrix4f..multiplyMM(state.mMVMatrix, 0, state.mVMatrix, 0, r.getTransform().getMatrix(), 0);
//	        	Matrix.multiplyMM(state.mMVPMatrix, 0, state.mPMatrix, 0, state.mMVMatrix, 0);
	        	//Matrix.multiplyMM(state.mMVPMatrix, 0, state.mMVMatrix, 0, state.mPMatrix, 0);
	
	        	r.getRenderable().draw(state);
	        }
    	}
	}
}

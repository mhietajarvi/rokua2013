package test;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
//import org.lwjgl.opengl.GL20;

/*
 * programs provide
 * 
 * prepare object/geometry by binding it to a program 
 * 
 * 
 * view/projection
 * 
 * program <-- view/projection
 * program <-- model transformation
 * program <-- model
 * 
 * I DONT HAVE ENOUGH KNOWLEDGE YET TO MAKE GOOD DESIGN DECISION, JUST HAVE TO PICK SOME ARRANGEMENT...
 * 
 * model transformation
 *  
 * model
 *  
 * 
 * 
 */
public class Program {

	static String read(String file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file)),
				Charset.forName("UTF-8"));
	}

	public static enum Uniform {
		// standard matrices that should be used by any sane vertex shader
		U_MODEL_TO_WORLD_M4,
		U_MODEL_TO_VIEW_M4,
		U_MODEL_TO_PROJECTED_M4,
		U_WORLD_TO_PROJECTED_M4,
		
		U_EYE_WORLD_POS_3F,
		
		U_COLOR_MULT_F,
		U_TIME_F,

		// I don't yet know how to elegantly handle different light types
		// supported by shaders...
		U_POINT_LIGHT_1_3F,
		U_POINT_LIGHT_2_3F,
		U_POINT_LIGHT_3_3F,

		// 
		U_ENV_CUBE
	}

//	public static enum Attribute {
//		POSITION_3F, NORMAL_3F, COLOR_4F
//	}

	private int uIndices[] = new int[Uniform.values().length];
	//private int aIndices[] = new int[Attribute.values().length];

	private int program;
	private int vertexShader;
	private int geometryShader;
	private int fragmentShader;
	
	public int maxInstanced() {
		return 250;
	}
	
	private FloatBuffer buf = BufferUtils.createFloatBuffer(maxInstanced()*4*4);
	

	public Program(String vertexShaderFile, String geometryShaderFile, String fragmentShaderFile) throws IOException {
		
		Arrays.fill(uIndices, -1);
		//Arrays.fill(aIndices, -1);
		
		Log.d("Loading vertex shader: %s", vertexShaderFile);
		vertexShader = loadShader(GL_VERTEX_SHADER, read(vertexShaderFile));
		if (geometryShaderFile != null) {
			Log.d("Loading geometry shader: %s", geometryShaderFile);
			geometryShader = loadShader(GL_GEOMETRY_SHADER, read(geometryShaderFile));
		}
		Log.d("Loading fragment shader: %s", fragmentShaderFile);
		fragmentShader = loadShader(GL_FRAGMENT_SHADER, read(fragmentShaderFile));
		program = glCreateProgram();
		glAttachShader(program, vertexShader);
		if (geometryShaderFile != null) {
			glAttachShader(program, geometryShader);
		}
		glAttachShader(program, fragmentShader);
		
		for (Attribute a : Attribute.values()) {
			glBindAttribLocation(program, a.ordinal(), a.name());
		}
		glLinkProgram(program);
		
		if (glGetProgrami(program, GL_LINK_STATUS) != GL_TRUE) {
			throw new IllegalArgumentException("Could not link program with shaders: "+vertexShaderFile+","+fragmentShaderFile+"\n"+glGetProgramInfoLog(program, 10000));
		}
		
		Log.d("Linked shaders %s and %s to program %s", vertexShader, fragmentShader, program);

		StringBuilder sb = new StringBuilder();
		for (Uniform u : Uniform.values()) {
			int index = glGetUniformLocation(program, u.name());
			setIndex(u, index);
			sb.append(u+":"+index+" ");
		}
		Log.d("Uniforms: %s", sb);

		sb.setLength(0);
		for (Attribute a : Attribute.values()) {
			//int index = a.ordinal();
			//glBindAttribLocation(program, index, a.name());
			int index = glGetAttribLocation(program, a.name());
			//setIndex(a, index);
			sb.append(a+":"+index+" ");
		}
		Log.d("Attributes: %s", sb);
	}

	protected void setIndex(Uniform u, int index) {
		uIndices[u.ordinal()] = index;
	}

//	protected void setIndex(Attribute a, int index) {
//		aIndices[a.ordinal()] = index;
//	}
//
//	public int getIndex(Attribute a) {
//		return aIndices[a.ordinal()];
//	}

	public int getIndex(Uniform u) {
		return uIndices[u.ordinal()];
	}

	private int loadShader(int type, String shaderCode) {
		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = glCreateShader(type);
		// add the source code to the shader and compile it
		glShaderSource(shader, shaderCode);
		glCompileShader(shader);
		
		if (glGetShaderi(shader, GL_COMPILE_STATUS) != GL_TRUE) {
			//Log.d("Shader compilation failed: \n"+glGetShaderInfoLog(shader, 10000));
			throw new IllegalArgumentException("Could not compile shader code:\n"+shaderCode+"\n"+glGetShaderInfoLog(shader, 10000));
		}

		// Log.d(glGetShaderInfoLog(shader, 10000));
		Log.d("Compiled shader %s, source size = %s bytes", shader, shaderCode.length());
		return shader;
	}
	
	View view;
	Matrix4f model_to_view = new Matrix4f();
	Matrix4f model_to_projected = new Matrix4f();
	Matrix4f world_to_projected = new Matrix4f();
	
	public void useGlobals(View view, double time) {
		
		this.view = view;
		Matrix4f.mul(view.projection, view.world_to_view, world_to_projected);
		
		glUseProgram(program);

		setUniform(Uniform.U_WORLD_TO_PROJECTED_M4, 1, world_to_projected);
		
        // map current lights to uniforms provided by shader program
        glUniform3f(getIndex(Uniform.U_POINT_LIGHT_1_3F), view.point_light_1.x, view.point_light_1.y, view.point_light_1.z);
        
        glUniform1i(getIndex(Uniform.U_ENV_CUBE), view.envCubeSampler);
		setUniform(Uniform.U_EYE_WORLD_POS_3F, view.view_to_world.m30, view.view_to_world.m31, view.view_to_world.m32);
		
		setUniform(Uniform.U_TIME_F, (float)time);
	}
	
	public void bind(Uniform u, float value) {
        glUniform1f(getIndex(u), value);
	}
	
	
	public void setUniform(Uniform u, FloatBuffer buf) {
		
		int index = getIndex(u);
		if (index != -1) {
	        glUniformMatrix4(index, false, buf);
		}
	}
	
	public void setUniform(Uniform u, int count, Matrix4f... matrices) {
		
		int index = getIndex(u);
		if (index != -1) {
	    	buf.clear();
	    	for (int i = 0; i < count; i++) {
	    		matrices[i].store(buf);
	    	}
	    	buf.flip();
	        glUniformMatrix4(index, false, buf);
		}
	}

	public void setUniform(Uniform u, float v0, float v1, float v2) {
		
		int index = getIndex(u);
		if (index != -1) {
	        glUniform3f(index, v0, v1, v2);
		}
	}
	
	public void setUniform(Uniform u, float f) {
		
		int index = getIndex(u);
		if (index != -1) {
	        glUniform1f(index, f);
		}
	}
	

	public void useModelTransforms(Matrix4f[] model_to_world, int count) {

		setUniform(Uniform.U_MODEL_TO_WORLD_M4, count, model_to_world);
		setUniform(Uniform.U_WORLD_TO_PROJECTED_M4, 1, world_to_projected);
	}
	
	public void useModelTransform(Matrix4f model_to_world) {
		
		glUseProgram(program);
		
		Matrix4f.mul(view.world_to_view, model_to_world, model_to_view);
		Matrix4f.mul(view.projection, model_to_view, model_to_projected);
		
		setUniform(Uniform.U_MODEL_TO_WORLD_M4, 1, model_to_world);
		setUniform(Uniform.U_MODEL_TO_VIEW_M4, 1, model_to_view);
		setUniform(Uniform.U_MODEL_TO_PROJECTED_M4, 1, model_to_projected);
	}
	
//	public void useWith(View view, Matrix4f model_to_world) {
//		
//		glUseProgram(program);
//
//		// TODO: we only need to update model-dependent uniforms for every new object
//		// 
//		
//		// Bind the texture
////		GL13.glActiveTexture(GL13.GL_TEXTURE0);
////		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIds[textureSelector]);
//		
//		// Bind to the VAO that has all the information about the vertices
////		GL30.glBindVertexArray(vaoId);
////		GL20.glEnableVertexAttribArray(0);
////		GL20.glEnableVertexAttribArray(1);
////		GL20.glEnableVertexAttribArray(2);
//		
//		// Bind to the index VBO that has all the information about the order of the vertices
//		//GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
//		
//		// Draw the vertices
//		//GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);
//    	
//    	//LOG.error("drawSetupP: glUseProgram({})", program);
//    	//glUseProgram(program);
//		
//		Matrix4f model_to_view = Matrix4f.mul(view.world_to_view, model_to_world, null);
//		Matrix4f model_to_projected = Matrix4f.mul(view.projection, model_to_view, null);
//		
//    	// pass in the light position and transformations
//    	
//    	buf.clear();
//    	model_to_world.store(buf);
//    	buf.flip();
//        glUniformMatrix4(getIndex(Uniform.U_MODEL_TO_WORLD_M4), false, buf);
//		
//    	buf.clear();
//        model_to_view.store(buf);
//    	buf.flip();
//        glUniformMatrix4(getIndex(Uniform.U_MODEL_TO_VIEW_M4), false, buf);
//		
//    	buf.clear();
//        model_to_projected.store(buf);
//    	buf.flip();
//        glUniformMatrix4(getIndex(Uniform.U_MODEL_TO_PROJECTED_M4), false, buf);
//
//        
//        // glUniform
//        
////        state.vVLight
////        
////        glUniform3f(hLightPos, state.vVLight.x, state.vVLight.y, state.vVLight.z);
////        
////        //state.vVLight.store(lightBuf);
////
////        
////        //GLES20.glUniform3fv(hLightPos, 1, state.vVLight, 0);
////        glUniform3f(hLightPos, state.vVLight.x, state.vVLight.y, state.vVLight.z);
//	}

	// LOG.error("loaded vertexShader "+ vertexShader+"\n"+vertexShaderCode);
	// LOG.error("loaded fragmentShader "+
	// fragmentShader+"\n"+fragmentShaderCode);

	/**
	 * Transforms and draws a set of vertices.
	 * 
	 * @param state
	 *            any state affecting rendering result
	 * @param vertexBuffer
	 *            vertices
	 * @param mode
	 *            GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP, GL_LINES,
	 *            GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, and GL_TRIANGLES
	 * @param count
	 */
/*
	abstract void draw(RenderState state, FloatBuffer position, int mode,
			int count);

	abstract void draw(RenderState state, FloatBuffer position,
			ShortBuffer index, int mode);

	abstract void draw(RenderState state, FloatBuffer position,
			FloatBuffer color, FloatBuffer normal, int mode, int count);

	abstract void draw(RenderState state, FloatBuffer position,
			FloatBuffer color, FloatBuffer normal, ShortBuffer index, int mode);
			*/
}

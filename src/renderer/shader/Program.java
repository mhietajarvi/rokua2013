package renderer.shader;

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

import org.lwjgl.opengl.GL43;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

import renderer.Lights;
import renderer.Log;
import renderer.Util;
import renderer.View;
import renderer.texture.Texture;

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

	

	// public static enum Attribute {
	// POSITION_3F, NORMAL_3F, COLOR_4F
	// }

	private int uIndices[] = new int[Uniform.values().length];
	// private int aIndices[] = new int[Attribute.values().length];

	private int program;
	private int vertexShader;
	private int geometryShader;
	private int fragmentShader;

	public int maxInstanced() {
		return 250;
	}

	private FloatBuffer buf = BufferUtils.createFloatBuffer(maxInstanced() * 4 * 4);

	private static int currentProgram = -1;

	private void useProgram() {
		if (currentProgram != program) {
			glUseProgram(program);
			currentProgram = program;
		}
	}

	public Program(String dir) throws IOException {

		this(Util.find(dir, "vertex.*"), Util.find(dir, "geometry.*"), Util.find(dir, "fragment.*"));
	}

	public static final int[] resTypes = new int[] { GL43.GL_UNIFORM, GL43.GL_UNIFORM_BLOCK, GL43.GL_PROGRAM_INPUT,
			GL43.GL_PROGRAM_OUTPUT, GL43.GL_BUFFER_VARIABLE, GL43.GL_SHADER_STORAGE_BLOCK, GL43.GL_VERTEX_SUBROUTINE,
			GL43.GL_TESS_CONTROL_SUBROUTINE, GL43.GL_TESS_EVALUATION_SUBROUTINE, GL43.GL_GEOMETRY_SUBROUTINE,
			GL43.GL_FRAGMENT_SUBROUTINE, GL43.GL_COMPUTE_SUBROUTINE, GL43.GL_VERTEX_SUBROUTINE_UNIFORM,
			GL43.GL_TESS_CONTROL_SUBROUTINE_UNIFORM, GL43.GL_TESS_EVALUATION_SUBROUTINE_UNIFORM,
			GL43.GL_GEOMETRY_SUBROUTINE_UNIFORM, GL43.GL_FRAGMENT_SUBROUTINE_UNIFORM,
			GL43.GL_COMPUTE_SUBROUTINE_UNIFORM, GL43.GL_TRANSFORM_FEEDBACK_VARYING };

	public String resTypeName(int resType) {
		switch (resType) {
		case GL43.GL_UNIFORM:
			return "GL_UNIFORM";
		case GL43.GL_UNIFORM_BLOCK:
			return "GL_UNIFORM_BLOCK";
		case GL43.GL_PROGRAM_INPUT:
			return "GL_PROGRAM_INPUT";
		case GL43.GL_PROGRAM_OUTPUT:
			return "GL_PROGRAM_OUTPUT";
		case GL43.GL_BUFFER_VARIABLE:
			return "GL_BUFFER_VARIABLE";
		case GL43.GL_SHADER_STORAGE_BLOCK:
			return "GL_SHADER_STORAGE_BLOCK";
		case GL43.GL_VERTEX_SUBROUTINE:
			return "GL_VERTEX_SUBROUTINE";
		case GL43.GL_TESS_CONTROL_SUBROUTINE:
			return "GL_TESS_CONTROL_SUBROUTINE";
		case GL43.GL_TESS_EVALUATION_SUBROUTINE:
			return "GL_TESS_EVALUATION_SUBROUTINE";
		case GL43.GL_GEOMETRY_SUBROUTINE:
			return "GL_GEOMETRY_SUBROUTINE";
		case GL43.GL_FRAGMENT_SUBROUTINE:
			return "GL_FRAGMENT_SUBROUTINE";
		case GL43.GL_COMPUTE_SUBROUTINE:
			return "GL_COMPUTE_SUBROUTINE";
		case GL43.GL_VERTEX_SUBROUTINE_UNIFORM:
			return "GL_VERTEX_SUBROUTINE_UNIFORM";
		case GL43.GL_TESS_CONTROL_SUBROUTINE_UNIFORM:
			return "GL_TESS_CONTROL_SUBROUTINE_UNIFORM";
		case GL43.GL_TESS_EVALUATION_SUBROUTINE_UNIFORM:
			return "GL_TESS_EVALUATION_SUBROUTINE_UNIFORM";
		case GL43.GL_GEOMETRY_SUBROUTINE_UNIFORM:
			return "GL_GEOMETRY_SUBROUTINE_UNIFORM";
		case GL43.GL_FRAGMENT_SUBROUTINE_UNIFORM:
			return "GL_FRAGMENT_SUBROUTINE_UNIFORM";
		case GL43.GL_COMPUTE_SUBROUTINE_UNIFORM:
			return "GL_COMPUTE_SUBROUTINE_UNIFORM";
		case GL43.GL_TRANSFORM_FEEDBACK_VARYING:
			return "GL_TRANSFORM_FEEDBACK_VARYING";
		}
		return "UNKNOWN(" + resType + ")";
	}

	// for arrays
	public static <T, U> U[] convertArray(T[] from, Function<T, U> func, IntFunction<U[]> generator) {
		return Arrays.stream(from).map(func).toArray(generator);
	}

	public Program(File vertexShaderFile, File geometryShaderFile, File fragmentShaderFile) throws IOException {

		Arrays.fill(uIndices, -1);
		// Arrays.fill(aIndices, -1);

		Log.d("Loading vertex shader: %s", vertexShaderFile);
		vertexShader = loadShader(GL_VERTEX_SHADER, Util.read(vertexShaderFile));
		if (geometryShaderFile != null) {
			Log.d("Loading geometry shader: %s", geometryShaderFile);
			geometryShader = loadShader(GL_GEOMETRY_SHADER, Util.read(geometryShaderFile));
		}
		Log.d("Loading fragment shader: %s", fragmentShaderFile);
		fragmentShader = loadShader(GL_FRAGMENT_SHADER, Util.read(fragmentShaderFile));
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
			throw new IllegalArgumentException("Could not link program with shaders: " + vertexShaderFile + ","
					+ fragmentShaderFile + "\n" + glGetProgramInfoLog(program, 10000));
		}

		Log.d("Linked shaders %s and %s to program %s", vertexShader, fragmentShader, program);

		// find all uniforms in the program
		// track that all uniforms are bound before execution?

		for (int resType : resTypes) {

			final int numActive = GL43.glGetProgramInterfacei(program, resType, GL43.GL_ACTIVE_RESOURCES);
			for (int i = 0; i < numActive; i++) {
				String name = GL43.glGetProgramResourceName(program, resType, i, 100);
				// IntBuffer props = BufferUtils.createIntBuffer(1);
				// props.put(GL43.GL_NAME_LENGTH);
				// GL43.glGetProgramResource(program, GL43.GL_UNIFORM, i, props,
				// null, params);
				Log.d(" " + resTypeName(resType) + "[" + i + "] : '" + name + "'");
			}
		}

		IntBuffer indices = BufferUtils.createIntBuffer(Uniform.values().length);

		glGetUniformIndices(program, Arrays.stream(Uniform.values()).map((Enum<?> e) -> e.name())
				.toArray(String[]::new), indices);

		// IntBuffer
		StringBuilder sb = new StringBuilder();
		for (Uniform u : Uniform.values()) {
			int index = glGetUniformLocation(program, u.name());
			setIndex(u, index);
			sb.append(u + ":" + index + " ");
		}
		Log.d("Uniforms: %s", sb);

		sb.setLength(0);
		for (Attribute a : Attribute.values()) {
			// int index = a.ordinal();
			// glBindAttribLocation(program, index, a.name());
			int index = glGetAttribLocation(program, a.name());
			// setIndex(a, index);
			sb.append(a + ":" + index + " ");
		}
		Log.d("Attributes: %s", sb);
	}

	protected void setIndex(Uniform u, int index) {
		uIndices[u.ordinal()] = index;
	}

	// protected void setIndex(Attribute a, int index) {
	// aIndices[a.ordinal()] = index;
	// }
	//
	// public int getIndex(Attribute a) {
	// return aIndices[a.ordinal()];
	// }

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
			// Log.d("Shader compilation failed: \n"+glGetShaderInfoLog(shader, 10000));
			throw new IllegalArgumentException("Could not compile shader code:\n" + shaderCode + "\n"
					+ glGetShaderInfoLog(shader, 10000));
		}

		// Log.d(glGetShaderInfoLog(shader, 10000));
		Log.d("Compiled shader %s, source size = %s bytes", shader, shaderCode.length());
		return shader;
	}

	View view;

	// Matrix4f world_to_projected = new Matrix4f();

	public void useLights(Lights lights) {

		useProgram();
		glUniform3f(getIndex(Uniform.U_POINT_LIGHT_1_3F), lights.point_light_1.x, lights.point_light_1.y,
				lights.point_light_1.z);
	}

	public void useTime(double time) {

		useProgram();
		setUniform(Uniform.U_TIME_F, (float) time);
	}

	public void useView(View view) {

		this.view = view;
		// Matrix4f.mul(view.projection, view.world_to_view, world_to_projected);

		useProgram();

		bind(Uniform.U_WORLD_TO_PROJECTED_M4, view.world_to_projected);

		// TODO: lights need to be handled properly (e.g. )
		// map current lights to uniforms provided by shader program

		// glUniform1i(getIndex(Uniform.U_ENV_CUBE), view.envCubeSampler);
		bind(Uniform.U_EYE_WORLD_POS_3F, view.view_to_world.m30, view.view_to_world.m31, view.view_to_world.m32);

	}

	public void bind(Uniform u, Texture texture) {
		bind(u, texture.getSampler());
	}

	public void bind(Uniform u, int value) {

		int index = getIndex(u);
		if (index != -1) {
			useProgram();
			glUniform1i(index, value);
		}
	}

	public void bind(Uniform u, float value) {
		int index = getIndex(u);
		if (index != -1) {
			useProgram();
			glUniform1f(getIndex(u), value);
		}
	}

	public void bind(Uniform u, FloatBuffer buf) {

		int index = getIndex(u);
		if (index != -1) {
			useProgram();
			glUniformMatrix4fv(index, false, buf);
		}
	}

	public void bind(Uniform u, Matrix4f... matrices) {

		int index = getIndex(u);
		if (index != -1) {
			buf.clear();
			for (Matrix4f matrix : matrices) {
				// for (int i = 0; i < count; i++) {
				matrix.store(buf);
				// matrices[i].store(buf);
			}
			buf.flip();
			useProgram();
			glUniformMatrix4fv(index, false, buf);
		}
	}

	public void bind(Uniform u, float v0, float v1, float v2) {

		int index = getIndex(u);
		if (index != -1) {
			useProgram();
			glUniform3f(index, v0, v1, v2);
		}
	}

	public void setUniform(Uniform u, float f) {

		int index = getIndex(u);
		if (index != -1) {
			useProgram();
			glUniform1f(index, f);
		}
	}

	// temp, only for method below
	Matrix4f model_to_view = new Matrix4f();
	Matrix4f model_to_projected = new Matrix4f();

	// hmm... now all shaders support these uniforms?
	public void useModelTransform(Matrix4f model_to_world) {

		useProgram();

		Matrix4f.mul(view.world_to_view, model_to_world, model_to_view);
		Matrix4f.mul(view.projection, model_to_view, model_to_projected);

		bind(Uniform.U_MODEL_TO_WORLD_M4, model_to_world);
		bind(Uniform.U_MODEL_TO_VIEW_M4, model_to_view);
		bind(Uniform.U_MODEL_TO_PROJECTED_M4, model_to_projected);
	}

	// public void useModelTransforms(Matrix4f[] model_to_world, int count) {
	//
	// useProgram();
	//
	// setUniform(Uniform.U_MODEL_TO_WORLD_M4, count, model_to_world);
	// setUniform(Uniform.U_WORLD_TO_PROJECTED_M4, 1, world_to_projected);
	// }

	// public void useWith(View view, Matrix4f model_to_world) {
	//
	// glUseProgram(program);
	//
	// // TODO: we only need to update model-dependent uniforms for every new object
	// //
	//
	// // Bind the texture
	// // GL13.glActiveTexture(GL13.GL_TEXTURE0);
	// // GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIds[textureSelector]);
	//
	// // Bind to the VAO that has all the information about the vertices
	// // GL30.glBindVertexArray(vaoId);
	// // GL20.glEnableVertexAttribArray(0);
	// // GL20.glEnableVertexAttribArray(1);
	// // GL20.glEnableVertexAttribArray(2);
	//
	// // Bind to the index VBO that has all the information about the order of the vertices
	// //GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
	//
	// // Draw the vertices
	// //GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);
	//
	// //LOG.error("drawSetupP: glUseProgram({})", program);
	// //glUseProgram(program);
	//
	// Matrix4f model_to_view = Matrix4f.mul(view.world_to_view, model_to_world, null);
	// Matrix4f model_to_projected = Matrix4f.mul(view.projection, model_to_view, null);
	//
	// // pass in the light position and transformations
	//
	// buf.clear();
	// model_to_world.store(buf);
	// buf.flip();
	// glUniformMatrix4(getIndex(Uniform.U_MODEL_TO_WORLD_M4), false, buf);
	//
	// buf.clear();
	// model_to_view.store(buf);
	// buf.flip();
	// glUniformMatrix4(getIndex(Uniform.U_MODEL_TO_VIEW_M4), false, buf);
	//
	// buf.clear();
	// model_to_projected.store(buf);
	// buf.flip();
	// glUniformMatrix4(getIndex(Uniform.U_MODEL_TO_PROJECTED_M4), false, buf);
	//
	//
	// // glUniform
	//
	// // state.vVLight
	// //
	// // glUniform3f(hLightPos, state.vVLight.x, state.vVLight.y, state.vVLight.z);
	// //
	// // //state.vVLight.store(lightBuf);
	// //
	// //
	// // //GLES20.glUniform3fv(hLightPos, 1, state.vVLight, 0);
	// // glUniform3f(hLightPos, state.vVLight.x, state.vVLight.y, state.vVLight.z);
	// }

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
	 *            GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP, GL_LINES, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, and GL_TRIANGLES
	 * @param count
	 */
	/*
	 * abstract void draw(RenderState state, FloatBuffer position, int mode, int count);
	 * 
	 * abstract void draw(RenderState state, FloatBuffer position, ShortBuffer index, int mode);
	 * 
	 * abstract void draw(RenderState state, FloatBuffer position, FloatBuffer color, FloatBuffer normal, int mode, int
	 * count);
	 * 
	 * abstract void draw(RenderState state, FloatBuffer position, FloatBuffer color, FloatBuffer normal, ShortBuffer
	 * index, int mode);
	 */
}

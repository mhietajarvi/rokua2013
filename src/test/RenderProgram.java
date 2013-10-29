package test;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


public interface RenderProgram {

	/**
	 * Transforms and draws a set of vertices. 
	 * 
	 * @param state any state affecting rendering result
	 * @param vertexBuffer vertices
	 * @param mode GL_POINTS,
                    GL_LINE_STRIP,
                    GL_LINE_LOOP,
                    GL_LINES,
                    GL_TRIANGLE_STRIP,
                    GL_TRIANGLE_FAN, and
                    GL_TRIANGLES
	 * @param count
	 */
    void draw(RenderState state, FloatBuffer position, int mode, int count);
    void draw(RenderState state, FloatBuffer position, ShortBuffer index, int mode);
    void draw(RenderState state, FloatBuffer position, FloatBuffer color, FloatBuffer normal, int mode, int count);
    void draw(RenderState state, FloatBuffer position, FloatBuffer color, FloatBuffer normal, ShortBuffer index, int mode);
}

package test;


/**
 * Container for static information that defines
 * what object will look like (geometry, shader programs).
 * Can render itself using a transformation.
 */
public interface Geometry {

	Drawable prepare(RenderProgram program);
	//void unbind();
	//void draw();
}


// object is bound to attributes provided by a program
// 
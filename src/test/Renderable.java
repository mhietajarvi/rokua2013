package test;


/**
 * Container for static information that defines
 * what object will look like (geometry, shader programs).
 * Can render itself using a transformation.
 */
public interface Renderable {
	
	void draw(RenderState state);
}

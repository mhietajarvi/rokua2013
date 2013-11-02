package test;

import org.lwjgl.util.vector.Matrix4f;

/**
 * provides various attributes as function of time
 * 
 * animation could be scripted or simple physics based (acceleration by gravity mainly) 
 * (collisions and their effects would need to be calculated separately)
 * 
 * 
 * provide chainable animation implementations
 * 
 * each animation modifies transform provided by previous
 * some just overwrite it, so some combinations don't make sense
 * 
 * e.g. dynamic rotation + static position
 * 
 * 
 */
public interface Animation {
	// NOTE! if animation state depends on other things in addition to time,
	//       they must be added as parameters here

	// generate (overwrite) transformation matrix at given point of time
	void updateTransform(long time_ns, Matrix4f transform);
}

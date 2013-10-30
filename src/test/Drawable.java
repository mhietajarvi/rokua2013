package test;

import org.lwjgl.util.vector.Matrix4f;


/**
 */
public interface Drawable extends Destroyable {

	void draw(View state, Matrix4f modelTransform);
}

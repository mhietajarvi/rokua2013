package test;

import org.lwjgl.util.vector.Matrix4f;


/**
 */
public interface Drawable extends Destroyable {

	void draw(View view, Matrix4f model_to_world);
}
package renderer.object;

import renderer.Destroyable;

/**
 */
public interface Drawable extends Destroyable {

	void draw(); // View view, Matrix4f model_to_world);

	void drawInstanced(int count);
}

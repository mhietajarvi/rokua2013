package test;

/**
 * Associates a transformation with a static renderable.
 */
public class TransformedRenderable {

	private final Drawable renderable;
	private final Transform transform;

	public TransformedRenderable(Drawable renderable, Transform transform) {
		this.renderable = renderable;
		this.transform = transform;
	}
	public Drawable getRenderable() {
		return renderable;
	}
	public Transform getTransform() {
		return transform;
	}
}
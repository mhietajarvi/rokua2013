package test;

/**
 * Associates a transformation with a static renderable.
 */
public class TransformedRenderable {

	private final Renderable renderable;
	private final Transform transform;

	public TransformedRenderable(Renderable renderable, Transform transform) {
		this.renderable = renderable;
		this.transform = transform;
	}
	public Renderable getRenderable() {
		return renderable;
	}
	public Transform getTransform() {
		return transform;
	}
}
package test;

/**
 * Associates a transformation with a static renderable.
 */
public class TransformedRenderable {

	private final Drawable renderable;
	private final Animation animation;

	public TransformedRenderable(Drawable renderable, Animation animation) {
		this.renderable = renderable;
		this.animation = animation;
	}
	public Drawable getRenderable() {
		return renderable;
	}
	public Animation getTransform() {
		return animation;
	}
}
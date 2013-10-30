package test;

/**
 * 
 *
 */
public class CompositeRenderable implements Drawable {
	
	private final Drawable[] parts;
	
	public CompositeRenderable(Drawable... parts) {
		this.parts = parts;
	}

	@Override
	public void draw(RenderState state) {
		for (Drawable part : parts) {
			part.draw(state);
		}
	}

	@Override
	public void bind() {
		for (Drawable part : parts) {
			part.bind();
		}
	}

	@Override
	public void unbind() {
		for (Drawable part : parts) {
			part.unbind();
		}
	}
}

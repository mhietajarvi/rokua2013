package test;

/**
 * 
 *
 */
public class CompositeRenderable implements Renderable {
	
	private final Renderable[] parts;
	
	public CompositeRenderable(Renderable... parts) {
		this.parts = parts;
	}

	@Override
	public void draw(RenderState state) {
		for (Renderable part : parts) {
			part.draw(state);
		}
	}
}

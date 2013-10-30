package test;

/**
 * Renderable rendered with single program.
 *
 */
public abstract class SimpleRenderable implements Drawable {

	RenderProgram program;
	
	public RenderProgram getProgram() {
		return program;
	}

	// return self for setter chaining
	public SimpleRenderable setProgram(RenderProgram program) {
		this.program = program;
		return this;
	}
}

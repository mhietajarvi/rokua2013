package test;

/**
 * Renderable rendered with single program.
 *
 */
public abstract class SimpleRenderable implements Drawable {

	Program program;
	
	public Program getProgram() {
		return program;
	}

	// return self for setter chaining
	public SimpleRenderable setProgram(Program program) {
		this.program = program;
		return this;
	}
}

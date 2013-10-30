package test;

import org.lwjgl.util.vector.Matrix4f;

public abstract class DrawableGeometry implements Drawable {

	final RenderProgram program;
	
	public DrawableGeometry(RenderProgram program) {
		this.program = program;
	}

	@Override
	public void draw(View state, Matrix4f modelTransform) {
		
		program.prepare(state, modelTransform);
		drawGeometry();
	}
	
	abstract protected void drawGeometry(); 
}

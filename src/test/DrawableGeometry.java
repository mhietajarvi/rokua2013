package test;

import org.lwjgl.util.vector.Matrix4f;

public abstract class DrawableGeometry implements Drawable {

	final RenderProgram program;
	
	public DrawableGeometry(RenderProgram program) {
		this.program = program;
	}

	@Override
	public void draw(View view, Matrix4f model_to_world) {
		
		program.useWith(view, model_to_world);
		drawGeometry();
	}
	
	abstract protected void drawGeometry(); 
}

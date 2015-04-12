package renderer;

public class TestMain {

	public static void main(String[] args) throws Exception {

		Scene scene = new TestScene1();

		new SceneRunner(scene).run();

	}

	/*
	 * TODO: - basic rendering works, now research new 4.5 stuff and refactor shape drawing, object management, textures
	 * etc
	 */

	// ARBDirectStateAccess.glVertexArrayVertexBuffer(vaobj, bindingindex,
	// buffer, offset, stride);

}

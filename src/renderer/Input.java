package renderer;

// simplified version of glfw inputs, uses glfw constants  
public interface Input {

	interface Listener {

		void mousePos(double x, double y, double dx, double dy);

		void mouseButtonDown(int button);

		void mouseButtonUp(int button);

		void keyDown(int key);

		void keyUp(int key);
	}

	void setListener(Listener listener);

	boolean keyDown(int key);

	boolean keyUp(int key);

	boolean mouseButtonDown(int button);

	boolean mouseButtonUp(int button);
}

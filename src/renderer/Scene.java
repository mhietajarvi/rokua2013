package renderer;

// Allows various test scenes to be plugged to the common 
// window/frame/input handling code
public interface Scene {

	void init(Input input) throws Exception;

	void renderFrame(long frame, double t, int w, int h);

	void dispose();
}

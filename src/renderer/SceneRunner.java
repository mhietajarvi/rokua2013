package renderer;

//import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
//import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import com.carrotsearch.hppcrt.lists.LongArrayList;

public class SceneRunner {

	static {
		System.setProperty("org.lwjgl.util.NoChecks", "true");
	}

	private Scene scene;

	public SceneRunner(Scene scene) throws Exception {
		this.scene = scene;

	}

	// We need to strongly reference callback instances.
	private GLFWErrorCallback errorCallback;
	private GLFWWindowSizeCallback windowSizeCallback;

	// private GLFWKeyCallback keyCallback;
	// private GLFWCursorPosCallback cursorPosCallback;
	// private GLFWMouseButtonCallback mouseButtonCallback;

	// convert various inputs to changes in render object states

	// e.g. mouse movement may be tied to camera rotation or

	static class GlfwInput implements Input {

		public boolean keyDown(int key) {
			return glfwGetKey(window, key) == GLFW_PRESS;
		}

		public boolean keyUp(int key) {
			return glfwGetKey(window, key) == GLFW_RELEASE;
		}

		public boolean mouseButtonDown(int button) {
			return glfwGetMouseButton(window, button) == GLFW_PRESS;
		}

		public boolean mouseButtonUp(int button) {
			return glfwGetMouseButton(window, button) == GLFW_RELEASE;
		}

		private final long window;
		private Listener listener = null;

		public void setListener(Listener listener) {
			this.listener = listener;
		}

		double prev_xpos;
		double prev_ypos;

		private GLFWKeyCallback keyCallback;
		private GLFWCursorPosCallback cursorPosCallback;
		private GLFWMouseButtonCallback mouseButtonCallback;

		public GlfwInput(long window) {

			this.window = window;

			glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
				@Override
				public void invoke(long window, int key, int scancode, int action, int mods) {

					System.out.println("KeyCallback");

					if (listener != null) {
						if (action == GLFW_PRESS) {
							listener.keyDown(key);
						} else if (action == GLFW_RELEASE) {
							listener.keyUp(key);
						}
					}
					// if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
					// // We will detect this in our rendering loop
					// glfwSetWindowShouldClose(window, GL_TRUE);
					// }
				}
			});

			glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback() {
				@Override
				public void invoke(long window, double xpos, double ypos) {
					double dx = xpos - prev_xpos;
					double dy = ypos - prev_ypos;
					prev_xpos = xpos;
					prev_ypos = ypos;
					if (listener != null) {
						listener.mousePos(xpos, ypos, dx, dy);
					}
				}
			});

			glfwSetMouseButtonCallback(window, mouseButtonCallback = new GLFWMouseButtonCallback() {
				@Override
				public void invoke(long window, int button, int action, int mods) {
					if (listener != null) {
						if (action == GLFW_PRESS) {
							listener.mouseButtonDown(button);
						} else if (action == GLFW_RELEASE) {
							listener.mouseButtonUp(button);
						}
					}
				}
			});

			DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer ypos = BufferUtils.createDoubleBuffer(1);
			glfwGetCursorPos(window, xpos, ypos);
			prev_xpos = xpos.get();
			prev_ypos = ypos.get();

			// glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		}
	}

	// The window handle
	private long window;

	private Input input;

	public void run() throws Exception {

		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		try {
			init();
			loop();

			// Release window and window callbacks
			glfwDestroyWindow(window);
			// keyCallback.release();
		} finally {
			// Terminate GLFW and release the GLFWerrorfun
			glfwTerminate();
			// errorCallback.release();
		}
	}

	int w = 300;
	int h = 300;

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.

		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure our window
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(w, h, "Hello World!", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetWindowSizeCallback(window, windowSizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				w = width;
				h = height;
			}
		});

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		// glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
		// if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
		// glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
		// });

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.

		input = new GlfwInput(window);
		
		TODO: make something happen... basic functionality should be ok

		// input.setListener(
		// new Input.Listener() {
		// @Override
		// public void mousePos(Input input, double x, double y, double dx,
		// double dy) {
		// }
		// @Override
		// public void mouseButtonUp(int button) {
		// }
		// @Override
		// public void mouseButtonDown(int button) {
		// }
		// @Override
		// public void keyUp(int key) {
		// }
		// @Override
		// public void keyDown(int key) {
		// }
		// });

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(window, (vidmode.width() - w) / 2, (vidmode.height() - h) / 2);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);

		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private void loop() throws Exception {

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the ContextCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities(); // Context.createFromCurrent();

		System.out.println("OpenGL version: " + glGetString(GL_VERSION));

		scene.init(input);

		// Set the clear color
		// glClearColor(0.1f, 0.1f, 1.0f, 0.0f);

		long frame = 0;
		final long initTime = System.nanoTime();
		long frameStart = initTime;
		long prevPrintTime = frameStart;
		final LongArrayList frameTimes = new LongArrayList(500);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(window)) {

			final long time_ns = frameStart - initTime;
			final double t = time_ns / 1000000000.0;

			scene.renderFrame(frame, t, w, h);

			frame++;
			long time = System.nanoTime();
			long dt_ns = time - frameStart;
			frameTimes.add(dt_ns);
			frameStart = time;

			// float dt = (float) (dt_ns / 1000000000.0);
			// double time_s = time / 1000000000.0;
			// for (Controller c : controllers) {
			// c.step(time_s, dt);
			// }

			long d_ns = time - prevPrintTime;
			if (d_ns > 2000 * 1000000L) {

				double d_ms = d_ns / 1000000.0;
				int frames = frameTimes.size(); // frame - prevPrintFrame;
				frameTimes.sort(); // Collections.sort(frameTimes);

				if (frames > 0) {
					long med = ((frames & 1) == 1) ? frameTimes.get((frames - 1) / 2)
							: (frameTimes.get(frames / 2) + frameTimes.get(frames / 2 - 1)) / 2;
					Log.d(String.format("Last %.2f s frametime avg/med/min/max : %.1f/%.1f/%.1f/%.1f ms", d_ms / 1000,
							d_ms / frames, med / 1000000.0, frameTimes.get(0) / 1000000.0,
							frameTimes.get(frames - 1) / 1000000.0));
				} else {
					Log.d(String.format("Last %.2f s no frames rendered", d_ms * 1000));
				}

				prevPrintTime = time;
				// prevPrintFrame = frame;
				frameTimes.clear();
			}

			// clear the framebuffer
			// glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			// swap the color buffers
			glfwSwapBuffers(window);

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
		scene.dispose();
	}

	// static Random rnd = new Random(2434);
	//
	// Vector3f v = new Vector3f();
	// Vector3f zero = new Vector3f();
	//
	// public static Vector3f rnd(Vector3f v, float r) {
	// v.x = (rnd.nextFloat() - 0.5f)*2*r;
	// v.y = (rnd.nextFloat() - 0.5f)*2*r;
	// v.z = (rnd.nextFloat() - 0.5f)*2*r;
	// return v;
	// }
	//
	// public static Vector3f rnd(float r) {
	// return rnd(new Vector3f(),r);
	// }

	/*
	 * public static void move(View camera, Obj selected, int dx, int dy) {
	 * 
	 * float scale = 0.005f;
	 * 
	 * // int dx = Mouse.getDX(); // int dy = Mouse.getDY(); if (dx != 0 || dy != 0) { if (Mouse.isButtonDown(0)) {
	 * camera.rotateView(dx * scale, dy * scale); } else if (Mouse.isButtonDown(1)) { camera.translateView(dx * scale,
	 * dy * scale, 0); } } float step = 0.3f;
	 * 
	 * if (!Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
	 * 
	 * if (Keyboard.isKeyDown(Keyboard.KEY_A)) { camera.translateView(-step, 0, 0); } if
	 * (Keyboard.isKeyDown(Keyboard.KEY_D)) { camera.translateView(step, 0, 0); } if
	 * (Keyboard.isKeyDown(Keyboard.KEY_W)) { camera.translateView(0, 0, -step); } if
	 * (Keyboard.isKeyDown(Keyboard.KEY_S)) { camera.translateView(0, 0, step); } if (selected != null) {
	 * selected.getWorldPosition(tmpVec3f); if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) { tmpVec3f.x -= 0.1; } if
	 * (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) { tmpVec3f.x += 0.1; } if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
	 * tmpVec3f.z -= 0.1; } if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) { tmpVec3f.z += 0.1; } if
	 * (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) { tmpVec3f.y -= 0.1; } if (Keyboard.isKeyDown(Keyboard.KEY_INSERT)) {
	 * tmpVec3f.y += 0.1; } selected.setTransform(tmpVec3f, true, 0); } } }
	 */
	/*
	 * int N = 3;
	 * 
	 * // Deque<Obj> reserve = new ArrayDeque<>(N); // Deque<Obj> fallingBlocks = new ArrayDeque<>(N); ArrayList<Obj>
	 * objects = new ArrayList<>();
	 * 
	 * // final Func.M4 farAway = new Simple.Position(0, new Vector3f(10000, 0, // 0));
	 * 
	 * // final Vector3f farAwayPos() { // // Vector3f p = rnd(50); // //Vector3f p = rnd(5); // p.y += 100; // return
	 * p; // } // // final Func.M4 farAway() { // // return new Simple.Position(0, farAwayPos()); // }
	 * 
	 * int nextFb = 0; double fbInterval = 0.01;
	 * 
	 * double nextCharMinTime = 0; double charInterval = 1.3;
	 * 
	 * Vector3f p0 = new Vector3f(); Vector3f v0 = new Vector3f(); Vector3f p1 = new Vector3f(); Vector3f v1 = new
	 * Vector3f();
	 * 
	 * ObjManager2 om = new ObjManager2();
	 * 
	 * // Interpolator ip = new SmoothVelocity(0, 10); // Obj someObj = om.new Obj(null);
	 * 
	 * List<Controller> controllers = new ArrayList<>();
	 * 
	 * // this creates new parent object for the char // and binds subobjects to it
	 * 
	 * private static Vector3f tmpVec3f = new Vector3f();
	 * 
	 * public void run2() throws Exception {
	 * 
	 * // Display.setDisplayMode(new DisplayMode(800, 400)); // Display.setVSyncEnabled(true); //
	 * //Display.setFullscreen(true); // Display.setTitle("Test 2d render"); // Display.create(new PixelFormat(), new
	 * ContextAttribs(4, // 3).withProfileCore(true).withForwardCompatible(true)); // Display.setResizable(true);
	 * 
	 * System.out.println("OpenGL version: " + glGetString(GL_VERSION));
	 * 
	 * // Program glass = new Program("assets/shaders/glass/vertex.glsl", // "assets/shaders/glass/geometry.glsl", //
	 * "assets/shaders/glass/fragment.glsl"); Program glass = new Program("assets/shaders/glass2"); Program noise = new
	 * Program("assets/shaders/noise"); Program std = new Program("assets/shaders/std"); // Program glass = new
	 * Program("assets/shaders/glass/vertex.glsl", null, // "assets/shaders/glass/fragment.glsl"); Program background =
	 * new Program("assets/shaders/background");
	 * 
	 * Quad quad1 = new Quad(1);
	 * 
	 * // TODO: specify background rect in projected space to Rect bgRect = new Rect(2, 1, -0.5f);
	 * 
	 * View lightView = new View(); View camera = new View(); camera.translateView(0, 0, 2);
	 * 
	 * Lights lights = new Lights(); // view.translateView(40, 30, -50);
	 * 
	 * // glGetPr
	 * 
	 * TextureCubeMap envCube = new TextureCubeMap("assets/images/env1"); noise.bind(Uniform.U_ENV_CUBE, envCube);
	 * background.bind(Uniform.U_ENV_CUBE, envCube);
	 * 
	 * Texture2D imgTexture = new Texture2D("assets/images/ash_uvgrid01.png"); std.bind(Uniform.U_ENV_CUBE, envCube); //
	 * std.bind(Uniform.U_TEXTURE_1, imgTexture); // std.bind(Uniform.U_TEXTURE_2, imgTU);
	 * 
	 * // view.setProjection(60, 0.1f, 100f, Display.getWidth(), // Display.getHeight());
	 * 
	 * glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	 * 
	 * Log.e("asdf %s", "hello");
	 * 
	 * glEnable(GL_BLEND); glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS); // glEnable(GL_CULL_FACE); // glCullFace(GL_FRONT);
	 * glDisable(GL_CULL_FACE);
	 * 
	 * Obj selected = null;
	 * 
	 * 
	 * Buffers buffers = new Buffers(N + 10);
	 * 
	 * FrameBuffer frameBuffer = new FrameBuffer(4 * 512, 4 * 512);
	 * 
	 * Obj q1 = om.new Obj(std, quad1); q1.setTransform(new Vector3f(2, 0, 0), true, 0);
	 * 
	 * // some temp testing stuff // TODO: make proper sine scroller // could also apply some functions to the target
	 * point (like y = base + // sin(C*t*x)) // List<Binder> binders = new ArrayList<>();
	 * 
	 * while (true) { // !Display.isCloseRequested()) {
	 * 
	 * final long time_ns = frameStart - initTime; final double t = time_ns / 1000000000.0; // start two processes in
	 * parallel: // 1) start rendering current state // 2) perform user input checking and // calculate new state based
	 * on that and active // animations/pseudo-physics
	 * 
	 * // move(camera, selected);
	 * 
	 * // prepare geometry for drawing buffers.clear(); // after object transformations and other attributes have been
	 * // updated for this frame // we can prepare rendering buffers om.prepareBuffers(buffers);
	 * 
	 * // move lights (TODO: these should be controlled just like geometry) lights.setWorldLight((float) (10 *
	 * Math.sin(frame * 0.01f)), 10, (float) (10 * Math.cos(frame * 0.01f))); // lights.setWorldLight(-40, 0, 0); //
	 * lightMarker.setTransform(lights.point_light_1, true, 0); std.bind(Uniform.U_TEXTURE_1, imgTexture);
	 * 
	 * // copy data to texture //
	 * 
	 * // to use shadow map, shader needs: // the shadow map depth texture, naturally // transformation from world
	 * coordinates to depth texture // (x,y,depth) for lookups
	 * 
	 * // render shadow map // TODO: set lightview to match some light position and direction
	 * frameBuffer.selectAsRenderTarget(); lightView.look(lights.point_light_1, new Vector3f(0, 0, 0));
	 * lightView.setProjection(60, 2f, 40f, frameBuffer.w, frameBuffer.h); // lightView.translateView(0, 0, -0.01f);
	 * 
	 * glDepthRange(0, 1); glViewport(0, 0, frameBuffer.w, frameBuffer.h);
	 * 
	 * glClearColor(255 / 255.0f, 105 / 255.0f, 180 / 255.0f, 1.0f); glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	 * 
	 * glEnable(GL_DEPTH_TEST);
	 * 
	 * glass.bind(Uniform.U_SHADOW_MAP_1, frameBuffer.depth); glass.bind(Uniform.U_WORLD_TO_SHADOW_M4,
	 * lightView.world_to_projected);
	 * 
	 * // glDisable(GL_DEPTH_TEST); // TODO: we probably want to use cheapest possible shaders for // shadow map
	 * generation... buffers.draw(lightView, null, t);
	 * 
	 * // what to do with frameBuffer? // to test, render a quad that uses the resulting texture // lightView.projection
	 * // lightView.world_to_view // glass.bind(Uniform.U_SHADOW_MAP_1, frameBuffer.depth); //
	 * glass.bind(Uniform.U_WORLD_TO_SHADOW_M4, // lightView.world_to_projected);
	 * 
	 * // camera.setProjection(50, 0.1f, 100f, Display.getWidth(), // Display.getHeight()); // // glViewport(0, 0,
	 * Display.getWidth(), Display.getHeight()); FrameBuffer.setDefaultRenderTarget();
	 * 
	 * // std.bind(Uniform.U_TEXTURE_1, frameBuffer.color); // std.bind(Uniform.U_SHADOW_MAP_1, frameBuffer.depth);
	 * std.bind(Uniform.U_TEXTURE_1, frameBuffer.depth);
	 * 
	 * // manually updating view for every program... refactor when there // are more programs // glass.useView(view);
	 * background.useView(camera); background.useTime(t);
	 * 
	 * glClearColor(255 / 255.0f, 105 / 255.0f, 180 / 255.0f, 1.0f); glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	 * 
	 * // glActiveTexture(GL_TEXTURE0 + view.envCubeSampler); // glBindTexture(GL_TEXTURE_CUBE_MAP,
	 * view.envCubeTexture);
	 * 
	 * // TODO: render to texture, then draw that texture to screen // (also draw depth buffer, just to visualize it)
	 * 
	 * glDisable(GL_DEPTH_TEST); background.useModelTransform(camera.view_to_world); bgRect.draw();
	 * 
	 * glEnable(GL_DEPTH_TEST); buffers.draw(camera, lights, t);
	 * 
	 * // om.drawObjectsAt(view, time_ns);
	 * 
	 * // if drawing multiple things with same program, // pass all transformations and drawables to program in one call
	 * 
	 * // 400 is bit too much
	 * 
	 * // glass.useModelTransform(new Matrix4f().translate(new Vector3f(0, // 0, -2))); // cube.draw(); //
	 * glass.useModelTransform(new Matrix4f().translate(new Vector3f(0, // 1, -2))); // cube.draw(); //
	 * glass.useModelTransform(new Matrix4f().translate(new Vector3f(0, // -1, -2))); // cube.draw(); // // for (int i =
	 * 0; i < 1000; i++) { // glass.useModelTransform(new Matrix4f().translate(new // Vector3f((rnd.nextFloat() -
	 * 0.5f)*20, (rnd.nextFloat() - // 0.5f)*20, (rnd.nextFloat() - 0.5f)*20))); // cube.draw(); // } // //
	 * //glass.useModelTransform(new Matrix4f().translate(new // Vector3f(2, 0, -2)).rotate(frame*0.005f, new
	 * Vector3f(0, 1, 0))); // glass.useModelTransform(new Matrix4f().rotate(frame*0.005f, new // Vector3f(0, 1,
	 * 0)).translate(new Vector3f(2, 0, -2))); // cube.draw();
	 * 
	 * // List<TransformedRenderable> drawList = new // LinkedList<TransformedRenderable>(); //
	 * drawList.addAll(staticGeometry); // drawList.add(new TransformedRenderable(cube, new Animation(0, 0, // -20 +
	 * 10*(float)Math.cos(t*0.13), 0, 0, 0))); // drawList.add(new TransformedRenderable(wall, new Animation(3, 2, //
	 * angle / 10))); // drawList.add(new TransformedRenderable(ball2, new Animation(x+1, // y+1, angle))); //
	 * drawList.add(new TransformedRenderable(sph, new Animation(bx, by, // 0, angle / 10, angle/15, angle/20))); //
	 * System.out.println("mouse : ("+Mouse.getX()+", "+Mouse.getY()+")"); // glClear( GL_COLOR_BUFFER_BIT );
	 * 
	 * // Display.sync(60); // Display.update(); frame++; long time = System.nanoTime(); long dt_ns = time - frameStart;
	 * frameTimes.add(dt_ns); frameStart = time;
	 * 
	 * float dt = (float) (dt_ns / 1000000000.0); double time_s = time / 1000000000.0; for (Controller c : controllers)
	 * { c.step(time_s, dt); }
	 * 
	 * long d_ns = time - prevPrintTime; if (d_ns > 2000 * 1000000L) {
	 * 
	 * double d_ms = d_ns / 1000000.0; int frames = frameTimes.size(); // frame - prevPrintFrame;
	 * Collections.sort(frameTimes);
	 * 
	 * if (frames > 0) { long med = ((frames & 1) == 1) ? frameTimes.get((frames - 1) / 2) : (frameTimes.get(frames / 2)
	 * + frameTimes.get(frames / 2 - 1)) / 2; Log.d(String.format(
	 * "Last %.2f s frametime avg/med/min/max : %.1f/%.1f/%.1f/%.1f ms", d_ms / 1000, d_ms / frames, med / 1000000.0,
	 * frameTimes.get(0) / 1000000.0, frameTimes.get(frames - 1) / 1000000.0)); } else { Log.d(String.format(
	 * "Last %.2f s no frames rendered", d_ms * 1000)); }
	 * 
	 * prevPrintTime = time; // prevPrintFrame = frame; frameTimes.clear(); }
	 * 
	 * } // Display.destroy(); }
	 */
}

package renderer;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthRange;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

import org.lwjgl.util.vector.Vector3f;

import renderer.object.ObjManager;
import renderer.object.Quad;
import renderer.object.Rect;
import renderer.object.ObjManager.Buffers;
import renderer.object.ObjManager.Obj;
import renderer.shader.Program;
import renderer.shader.Uniform;
import renderer.texture.Texture2D;
import renderer.texture.TextureCubeMap;

public class TestScene1 implements Scene, Input.Listener {

	// int nextFb = 0;
	// double fbInterval = 0.01;
	// double nextCharMinTime = 0;
	// double charInterval = 1.3;
	// Vector3f p0 = new Vector3f();
	// Vector3f v0 = new Vector3f();
	// Vector3f p1 = new Vector3f();
	// Vector3f v1 = new Vector3f();

	int N = 3;
	ObjManager om = new ObjManager();

	// Interpolator ip = new SmoothVelocity(0, 10);
	// Obj someObj = om.new Obj(null);

	// List<Controller> controllers = new ArrayList<>();

	// this creates new parent object for the char
	// and binds subobjects to it

	// private static Vector3f tmpVec3f = new Vector3f();

	TextureCubeMap envCube;
	Texture2D imgTexture;

	// Program glass = new Program("assets/shaders/glass/vertex.glsl",
	// "assets/shaders/glass/geometry.glsl",
	// "assets/shaders/glass/fragment.glsl");
	Program glass;
	Program noise;
	Program std;
	// Program glass = new Program("assets/shaders/glass/vertex.glsl", null,
	// "assets/shaders/glass/fragment.glsl");
	Program background;

	Quad quad1;

	// TODO: specify background rect in projected space to
	Rect bgRect;

	Rect rect;

	View lightView;
	View camera;
	Lights lights;

	Obj selected;

	Buffers buffers;
	FrameBuffer frameBuffer;
	Obj q1;

	// view.translateView(40, 30, -50);
	Input input;

	@Override
	public void init(Input input) throws Exception {

		this.input = input;
		input.setListener(this);

		imgTexture = new Texture2D("assets/images/ash_uvgrid01.png");
		// TODO: simple shape with program
		// - use DSA from OpenGL 4.5
		std = new Program("assets/shaders/std");
		std.bind(Uniform.U_TEXTURE_1, imgTexture);

		envCube = new TextureCubeMap("assets/images/env1");
		background = new Program("assets/shaders/background");
		background.bind(Uniform.U_ENV_CUBE, envCube);
		camera = new View();
		camera.translateView(0, 0, 2);
		bgRect = new Rect(2, 1, -0.5f);

		quad1 = new Quad(1);

		Obj q1 = om.new Obj(std, quad1);
		q1.setTransform(new Vector3f(2, 0, 0), true, 0);

		buffers = new Buffers(N + 10);

		// rect = new Rect(2, 1, -0.5f);
	}

	@Override
	public void renderFrame(long frame, double t, int w, int h) {

		// glClearColor(0.1f, 0.1f, 1.0f, 0.0f);
		// glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, w, h);
		camera.setProjection(50, 0.1f, 100f, w, h);

		// render texture to

		background.useView(camera);
		background.useTime(t);
		glClearColor(255 / 255.0f, 105 / 255.0f, 180 / 255.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glDisable(GL_DEPTH_TEST);
		background.useModelTransform(camera.view_to_world);

		bgRect.draw();

		buffers.clear();
		// after object transformations and other attributes have been updated
		// for this frame
		// we can prepare rendering buffers
		om.prepareBuffers(buffers);
		buffers.draw(camera, null, t);

		// std.useView(camera);
		// rect.draw();
	}

	// @Override
	public void init2(Input input) throws Exception {

		envCube = new TextureCubeMap("assets/images/env1");
		imgTexture = new Texture2D("assets/images/ash_uvgrid01.png");

		// Program glass = new Program("assets/shaders/glass/vertex.glsl",
		// "assets/shaders/glass/geometry.glsl",
		// "assets/shaders/glass/fragment.glsl");
		glass = new Program("assets/shaders/glass2");
		noise = new Program("assets/shaders/noise");
		std = new Program("assets/shaders/std");
		// Program glass = new Program("assets/shaders/glass/vertex.glsl", null,
		// "assets/shaders/glass/fragment.glsl");
		background = new Program("assets/shaders/background");

		quad1 = new Quad(1);

		// TODO: specify background rect in projected space to
		bgRect = new Rect(2, 1, -0.5f);

		lightView = new View();
		camera = new View();
		lights = new Lights();
		camera.translateView(0, 0, 2);

		selected = null;

		buffers = new Buffers(N + 10);
		frameBuffer = new FrameBuffer(4 * 512, 4 * 512);
		q1 = om.new Obj(std, quad1);
		q1.setTransform(new Vector3f(2, 0, 0), true, 0);

		// TODO Auto-generated method stub

		noise.bind(Uniform.U_ENV_CUBE, envCube);
		background.bind(Uniform.U_ENV_CUBE, envCube);
		std.bind(Uniform.U_ENV_CUBE, envCube);
		// std.bind(Uniform.U_TEXTURE_1, imgTexture);
		// std.bind(Uniform.U_TEXTURE_2, imgTU);
		// view.setProjection(60, 0.1f, 100f, Display.getWidth(),
		// Display.getHeight());

		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		// Log.e("asdf %s", "hello");

		glEnable(GL_BLEND);
		glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
		// glEnable(GL_CULL_FACE);
		// glCullFace(GL_FRONT);
		glDisable(GL_CULL_FACE);

		// long frame = 0;
		// final long initTime = System.nanoTime();
		// long frameStart = initTime;
		// long prevPrintTime = frameStart;
		// final List<Long> frameTimes = new ArrayList<>(500);
		glClearColor(0.1f, 0.1f, 1.0f, 0.0f);
	}

	public void renderFrame2(long frame, double t) {

		// TODO Auto-generated method stub
		// move(camera, selected);

		// prepare geometry for drawing
		buffers.clear();
		// after object transformations and other attributes have been
		// updated for this frame
		// we can prepare rendering buffers
		om.prepareBuffers(buffers);

		// move lights (TODO: these should be controlled just like geometry)
		lights.setWorldLight((float) (10 * Math.sin(frame * 0.01f)), 10, (float) (10 * Math.cos(frame * 0.01f)));
		// lights.setWorldLight(-40, 0, 0);
		// lightMarker.setTransform(lights.point_light_1, true, 0);
		std.bind(Uniform.U_TEXTURE_1, imgTexture);

		// copy data to texture
		//

		// to use shadow map, shader needs:
		// the shadow map depth texture, naturally
		// transformation from world coordinates to depth texture
		// (x,y,depth) for lookups

		// render shadow map
		// TODO: set lightview to match some light position and direction
		frameBuffer.selectAsRenderTarget();
		lightView.look(lights.point_light_1, new Vector3f(0, 0, 0));
		lightView.setProjection(60, 2f, 40f, frameBuffer.w, frameBuffer.h);
		// lightView.translateView(0, 0, -0.01f);

		glDepthRange(0, 1);
		glViewport(0, 0, frameBuffer.w, frameBuffer.h);

		glClearColor(255 / 255.0f, 105 / 255.0f, 180 / 255.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glEnable(GL_DEPTH_TEST);

		glass.bind(Uniform.U_SHADOW_MAP_1, frameBuffer.depth);
		glass.bind(Uniform.U_WORLD_TO_SHADOW_M4, lightView.world_to_projected);

		// glDisable(GL_DEPTH_TEST);
		// TODO: we probably want to use cheapest possible shaders for
		// shadow map generation...
		buffers.draw(lightView, null, t);

		// what to do with frameBuffer?
		// to test, render a quad that uses the resulting texture
		// lightView.projection
		// lightView.world_to_view
		// glass.bind(Uniform.U_SHADOW_MAP_1, frameBuffer.depth);
		// glass.bind(Uniform.U_WORLD_TO_SHADOW_M4,
		// lightView.world_to_projected);

		// camera.setProjection(50, 0.1f, 100f, Display.getWidth(),
		// Display.getHeight());
		//
		// glViewport(0, 0, Display.getWidth(), Display.getHeight());
		FrameBuffer.setDefaultRenderTarget();

		// std.bind(Uniform.U_TEXTURE_1, frameBuffer.color);
		// std.bind(Uniform.U_SHADOW_MAP_1, frameBuffer.depth);
		std.bind(Uniform.U_TEXTURE_1, frameBuffer.depth);

		// manually updating view for every program... refactor when there
		// are more programs
		// glass.useView(view);
		background.useView(camera);
		background.useTime(t);

		glClearColor(255 / 255.0f, 105 / 255.0f, 180 / 255.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// glActiveTexture(GL_TEXTURE0 + view.envCubeSampler);
		// glBindTexture(GL_TEXTURE_CUBE_MAP, view.envCubeTexture);

		// TODO: render to texture, then draw that texture to screen
		// (also draw depth buffer, just to visualize it)

		glDisable(GL_DEPTH_TEST);
		background.useModelTransform(camera.view_to_world);
		bgRect.draw();

		glEnable(GL_DEPTH_TEST);
		buffers.draw(camera, lights, t);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePos(double x, double y, double dx, double dy) {

		float scale = 0.005f;
		if (dx != 0 || dy != 0) {
			if (input.mouseButtonDown(0)) {
				camera.rotateView(dx * scale, dy * scale);
			} else if (input.mouseButtonDown(0)) {
				camera.translateView(dx * scale, dy * scale, 0);
			}
		}

		// float step = 0.3f;
		// TODO Auto-generated method stub
		/*
		 * public static void move(View camera, Obj selected, int dx, int dy) {
		 * 
		 * float scale = 0.005f;
		 * 
		 * // int dx = Mouse.getDX(); // int dy = Mouse.getDY(); if (dx != 0 || dy != 0) { if (Mouse.isButtonDown(0)) {
		 * camera.rotateView(dx * scale, dy * scale); } else if (Mouse.isButtonDown(1)) { camera.translateView(dx *
		 * scale, dy * scale, 0); } } float step = 0.3f;
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
		 * (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) { tmpVec3f.y -= 0.1; } if (Keyboard.isKeyDown(Keyboard.KEY_INSERT))
		 * { tmpVec3f.y += 0.1; } selected.setTransform(tmpVec3f, true, 0); } } }
		 */

	}

	@Override
	public void mouseButtonDown(int button) {

	}

	@Override
	public void mouseButtonUp(int button) {

	}

	@Override
	public void keyDown(int key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyUp(int key) {
		// TODO Auto-generated method stub

	}

}

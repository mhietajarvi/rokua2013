package test;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class RenderState {
	//public final float[] mMMatrix = new float[16]; // model-to-world
	public final Matrix4f mMVPMatrix = new Matrix4f(); // BufferUtils.createFloatBuffer(16); // model-to-projected
	public final Matrix4f mMVMatrix = new Matrix4f(); // BufferUtils.createFloatBuffer(16); // model-to-view
	public final Matrix4f mVMatrix = new Matrix4f(); // BufferUtils.createFloatBuffer(16); // world-to-view
	public final Matrix4f mPMatrix = new Matrix4f(); // BufferUtils.createFloatBuffer(16); // projection
	public final Vector4f vVLight = new Vector4f(); // .createFloatBuffer(4); // point light in view space

//	public RenderState(float[] mMMatrix, float[] mVMatrix, float[] mPMatrix, float[] vVLight) {
//		this.mMMatrix = mMMatrix;
//		this.mVMatrix = mVMatrix;
//		this.mPMatrix = mPMatrix;
//		this.vVLight = vVLight;
//	}
}

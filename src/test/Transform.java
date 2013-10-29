package test;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

// import android.opengl.Matrix;

/**
 * Converts basic transforms to a matrix representation needed for rendering.
 */
public class Transform {

	private final Matrix4f matrix = new Matrix4f();
	
	private static final Vector3f AXIS_X = new Vector3f(1.0f, 0, 0); 
	private static final Vector3f AXIS_Y = new Vector3f(0, 1.0f, 0); 
	private static final Vector3f AXIS_Z = new Vector3f(0, 0, -1.0f); 

	public Transform(float x, float y, float z, float rx, float ry, float rz) {

		matrix.setIdentity();
		matrix.translate(new Vector3f(x, y, z));
		matrix.rotate(rz, AXIS_Z);
		matrix.rotate(ry, AXIS_Y);
		matrix.rotate(rx, AXIS_X);
		
		/*
		Matrix.setIdentityM(matrix, 0);
		Matrix.translateM(matrix, 0, x, y, z);
		Matrix.rotateM(matrix, 0, rz, 0, 0, -1.0f);
		Matrix.rotateM(matrix, 0, ry, 0, 1.0f, 0);
		Matrix.rotateM(matrix, 0, rx, 1.0f, 0, 0);
		*/
		
		//Matrix.rotateM(rm, rmOffset, m, mOffset, a, rx, ry, rz); //rotateEulerM(matrix, 0, rx, ry, rz);
	}
	
	// 2d transform (translate x,y, rotate around z axis)
	public Transform(float x, float y, float angle) {
		
		matrix.setIdentity();
		matrix.translate(new Vector3f(x, y, 0));
		matrix.rotate(angle, AXIS_Z);
		/*
		Matrix.setIdentityM(matrix, 0);
		Matrix.translateM(matrix, 0, x, y, 0);
		Matrix.rotateM(matrix, 0, angle, 0, 0, -1.0f);
		*/
	}
	// TODO: add more constructors as needed

	// get resulting transformation matrix
	public Matrix4f getMatrix() {
		return matrix;
	}
}

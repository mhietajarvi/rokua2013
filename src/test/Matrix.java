package test;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Matrix {

	public static void transform_dir(Matrix4f left, Vector3f right, Vector3f dest) {

		float x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z;
		float y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z;
		float z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z;
		dest.x = x;
		dest.y = y;
		dest.z = z;
	}

	public static void transform_pos(Matrix4f left, Vector3f right, Vector3f dest) {

		float x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z + left.m30;
		float y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z + left.m31;
		float z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z + left.m32;
		dest.x = x;
		dest.y = y;
		dest.z = z;
	}
	
}

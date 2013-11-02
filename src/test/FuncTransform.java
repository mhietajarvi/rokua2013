package test;

import org.lwjgl.util.vector.*;

public class FuncTransform implements FuncMatrix {

	VectorPV pos;
	FuncMatrix rot;
	//final Vector3f tmp = new Vector3f();
	// combine position and rotation animations to matrix
	// position 
	// rotation, position providers can be independent or 
	
	
	@Override
	public void valueAt(double t, Matrix4f result) {
		result.setIdentity();
		//position.p(t, tmp);
		result.m30 = pos.x.p(t);
		result.m31 = pos.y.p(t);
		result.m32 = pos.z.p(t);
		rot.valueAt(t, result);
	}
	public FuncTransform(VectorPV pos, FuncMatrix rot) {
		this.pos = pos;
		this.rot = rot;
	}
}

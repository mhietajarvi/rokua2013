package test;

import org.lwjgl.util.vector.*;

public class PosRot implements Func.M4 {

	VectorPV pos;
	Func.M4 rot;
	//final Vector3f tmp = new Vector3f();
	// combine position and rotation animations to matrix
	// position 
	// rotation, position providers can be independent or 
	
	
	@Override
	public void m4(double t, Matrix4f result) {
		result.setIdentity();
		//position.p(t, tmp);
		result.m30 = pos.x.p(t);
		result.m31 = pos.y.p(t);
		result.m32 = pos.z.p(t);
		if (rot != null) {
			rot.m4(t, result);
		}
	}
	public PosRot(VectorPV pos, Func.M4 rot) {
		this.pos = pos;
		this.rot = rot;
	}
}

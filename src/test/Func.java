package test;

import org.lwjgl.util.vector.*;

public interface Func {

	public interface P {
		float p(double t);
	}

	public interface PV extends P {
		float v(double t);
	}

	public interface M4 {
		void m4(double t, Matrix4f result);
	}

	public interface V3 {
		void v3(double t, Vector3f result);
	}
	
	//void valueAt(double t, Matrix4f result);	
	//float valueAt(double t);
}

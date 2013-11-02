package test;

public interface Interpolator {

	Func.PV interpolate(float p0, float v0, float p2, float v2);
	double t0();
	double t2();
}

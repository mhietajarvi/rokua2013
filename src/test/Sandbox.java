package test;


public class Sandbox {

	public static void main(String[] args) {

		double t0 = 0;
		double t1 = 1;
		
		SmoothVelocity s = new SmoothVelocity(t0, t1);
		Func.PV ip = s.interpolate(0,-100, -1,-100);
		
		int n = 10;
		
		for (int i = -1; i <= (n+1); i++) {
			double t = t0 + ((t1-t0)*i)/n;
			Log.d("t=%.1f, p=%.1f, v=%.1f", t, ip.p(t),ip.v(t));
		}
	}
}

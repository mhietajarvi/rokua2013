package test;


public class Sandbox {

	public void test1() {
		
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
	
	public void test2() {
		
		// nanoseconds -> float
		// how much precision is lost for different base magnitudes

		long d_ns = 1000000000L/60;
		float df = 1.0f/60; //0.004f;
		
		
		
		
		int b1 = 0;
		int b2 = 36000;
		
		long ns1 = b1 * 1000000000L;
		long ns2 = b2 * 1000000000L;
		Log.d("epsilon : %.5f", df);
		
		for (int i = 0; i < 20; i++) {

			float f1 = (float)(ns1/1000000000.0);
			float f2 = (float)(ns2/1000000000.0);
			
			Log.d("%.5f  %.5f  %.2f %%", f1, f2, ((f2 - b2) - (f1 - b1))*100/(df));
			
			ns1 += d_ns;
			ns2 += d_ns;
		}
	}

	
	public void test3() {
		
		//Log.d("%.1f", Math.a(4))
	}
	
	public static void main(String[] args) {
		
		Sandbox s = new Sandbox();
		
		//s.test1();
		//s.test2();
		s.test3();

	}
}

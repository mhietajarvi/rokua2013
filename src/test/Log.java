package test;

import java.lang.Object;

public class Log {
	
	public static boolean d = true;
	public static boolean i = true;
	public static boolean w = true;
	public static boolean e = true;

	public static void p(String level, String msg, Object[] args) {
		System.out.println(""+level+" "+String.format(msg, args));
	}
	
	public static void d(String msg, Object... args) {
		if (d) {
			p("D", msg, args);
		}
	}
	public static void i(String msg, Object... args) {
		if (i) {
			p("I", msg, args);
		}
	}
	public static void w(String msg, Object... args) {
		if (w) {
			p("W", msg, args);
		}
	}
	public static void e(String msg, Object... args) {
		if (e) {
			p("E", msg, args);
		}
	}
}

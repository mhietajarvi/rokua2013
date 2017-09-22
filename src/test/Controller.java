package test;

// control is usually time-based,
// each controller provides an interface
// which can be used to let controller
// advance to specific moment of time
//
// do we need to specify time step size?
// probably controller should know its current time?
//
//
public interface Controller {

	void step(double time, float dt);
}

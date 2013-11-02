package test;

import org.lwjgl.util.vector.Matrix4f;

/**
 * provides various attributes as function of time
 * 
 * animation could be scripted or simple physics based (acceleration by gravity mainly) 
 * (collisions and their effects would need to be calculated separately)
 * 
 * 
 * provide chainable animation implementations
 * 
 * each animation modifies transform provided by previous
 * some just overwrite it, so some combinations don't make sense
 * 
 * e.g. dynamic rotation + static position
 * 
 * 
 */

// animate what:
//  transformation components:
//    rotation
//    position  (e.g. vector * sin(t) for pulsating objects)
//    scale
//  any simple numeric attribute
//  
//
// when animation changes, new animation can get
// derivative of current animation (can this be generalized?) 
// 

// animate single variable: 
//  start state  p0,v0 at t0 (specified)
//  acceleration a0 (calculated)
//  middle state p1,v1 at t1  (calculated)
//  acceleration a1 (calculated)
//  end state    p2,v2 at t2 (specified)
//  preserve continuity of v
// dt1 = t1 - t0
// dt2 = t2 - t1
// no limit to acceleration
// apply at most two different acceleration segments

// p1 = p0 + v0*dt1 + 0.5*a0*dt1*dt1
// v1 = v0 + a0*dt1
// p2 = p1 + v1*dt2 + 0.5*a1*dt2*dt2
// v2 = v1 + a1*dt2

// lets try additional constraint:
// t1 = (t0+t2)/2  which makes dt1 and dt2 constants

// so we have 4 unknowns: a0,p1,v1,a1  and 4 linear equations

//p1 = p0 + v0*dt1 + 0.5*a0*dt1*dt1
//v1 = v0 + a0*dt1
//p2 = p1 + v1*dt2 + 0.5*a1*dt2*dt2
//v2 = v1 + a1*dt2

//v2 - a1*dt2 = v1 

// a1 = (v2 - v0 - a0*dt1)/dt2 


// matrix only depends on times, so it can be reused for 
// any value

// -dt1         0           1    0   a0    v0
// -0.5*dt1*dt1 0           0    1   a1    p0 + v0*dt1
//  0           0.5*dt2*dt2 dt2  1   v1    p2
//  0           dt2         1    0   p1    v2

// v1  - a0*dt1 = v0
// p1  - 0.5*a0*dt1*dt1 = p0 + v0*dt1
// p1 + v1*dt2 + 0.5*a1*dt2*dt2 = p2 
// v1 + a1*dt2 = v2 

// A 0 1 0   a0    v0
// B 0 0 1   a1    p0 + v0*dt1
// 0 C D 1   v1    p2
// 0 D 1 0   p1    v2

//A 0 1 0   a0    v0
//B 0 0 1   a1    p0 + v0*dt1
//0 C D 1   v1    p2
//0 -D*D -D 0   p1    v2*-D


//A 0 1 0   a0    v0
//B 0 0 1   a1    p0 + v0*dt1
//0 C-D*D 0 1   v1    p2 -v2*D
//0 -D*D -D 0   p1    -v2*D

public interface Animation {
	// NOTE! if animation state depends on other things in addition to time,
	//       they must be added as parameters here

	// generate (overwrite) transformation matrix at given point of time
	void updateTransform(long time_ns, Matrix4f transform);
}

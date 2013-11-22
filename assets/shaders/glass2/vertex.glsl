#version 150 core

// Input vertex data, different for all executions of this shader.
// layout(location = 0) in vec3 vertexPosition_modelspace;
//void main(){
//    gl_Position.xyz = vertexPosition_modelspace;
//    gl_Position.w = 1.0;
//}


// some own color + reflected + refracted
// need 


uniform mat4 U_MODEL_TO_WORLD_M4[250];
uniform mat4 U_WORLD_TO_PROJECTED_M4;
uniform mat4 U_WORLD_TO_SHADOW_M4;

uniform float U_TIME_F;

in vec3 POSITION_3F;         // Per-vertex position information we will pass in.
in vec3 NORMAL_3F;           // Per-vertex normal information we will pass in.
in vec4 COLOR_4F;            // Per-vertex color information we will pass in.

//out vec3 v_world_pos;
//out vec4 v_color;

out Fragment {
	vec3 world_pos;
	vec3 world_nrm;
	vec4 shadow_pos;
	flat vec4 color;
};


//out vec3 v_normal;
//out vec3 v_world_normal;
//out mat4 w_to_p;
//out mat4 m_to_w;
//out vec3 mpos;
//out float v_color_mult;

mat3 rotx(float a) {
	float s = sin(a);
	float c = cos(a);
	return mat3( 1, 0, 0,
				 0, c, s,
				 0,-s, c);
}
mat3 roty(float a) {
	float s = sin(a);
	float c = cos(a);
	return mat3( c, 0,-s,
				 0, 1, 0,
				 s, 0, c);
}
mat3 rotz(float a) {
	float s = sin(a);
	float c = cos(a);
	return mat3( c,-s, 0,
				 s, c, 0,
				 0, 0, 1);
}

/*
c  s   x    c*x + s*y
-s c   y    c*y - s*x
             z

c  s   x    c*x - s*y
-s c   y    c*y + s*x
             z

 2*s*y
-2*s*x
   0
*/

float eps = 0.01;
mat3 rp_x_e = rotx(eps);
mat3 rp_y_e = roty(eps);
mat3 rp_z_e = rotz(eps);
mat3 rn_x_e = rotx(-eps);
mat3 rn_y_e = roty(-eps);
mat3 rn_z_e = rotz(-eps);

vec3 angles(vec3 v) {

	return vec3(atan(v.x, sqrt(v.z*v.z + v.y*v.y)),
				atan(v.y, sqrt(v.z*v.z + v.x*v.x)),
				atan(v.z, sqrt(v.x*v.x + v.y*v.y)));

	//return vec2(atan(v.z, sqrt(v.x*v.x + v.y*v.y)),atan(v.y, v.x));
}


float disp(vec3 v) {
	vec3 a = angles(v);
	return 1.21 + 0.4*sin(0.8*U_TIME_F + 4*a.y) + 0.4*cos(2 + 0.7*U_TIME_F + 4*a.x) - 0.4*sin(1 + 0.9*U_TIME_F + 4*a.z);
	
	//return 1;
}

// The entry point for our vertex shader.
void main() {

	//vec3 a = angles(POSITION_3F);
	//float disp = 1 + 0.2*cos(U_TIME_F + 7*a.x + 3*a.y + 5*a.z); // + 0.0*sin(U_TIME_F + l.y);
	//float disp = 1 + 0.2*cos(U_TIME_F + 2*POSITION_3F.x) + 0.2*sin(U_TIME_F + 3*POSITION_3F.y) + 0.2*sin(U_TIME_F + 4*POSITION_3F.z); // + 0.0*sin(U_TIME_F + l.y);
	
	vec3 ap = abs(POSITION_3F);
	
	vec3 p0 = POSITION_3F * (disp(POSITION_3F));
	
	vec3 p1;
	vec3 p2;
	vec3 s1;
	vec3 s2;
	float mult = 1;
	
	if (ap.x >= ap.y && ap.x >= ap.z) {
		mult = sign(POSITION_3F.x);
		p1 = rp_y_e * POSITION_3F;
		p2 = rp_z_e * POSITION_3F;
		s1 = rn_y_e * POSITION_3F;
		s2 = rn_z_e * POSITION_3F;
	} else 	if (ap.y >= ap.x && ap.y >= ap.z) {
		mult = sign(POSITION_3F.y);
		p1 = rp_z_e * POSITION_3F;
		p2 = rp_x_e * POSITION_3F;
		s1 = rn_z_e * POSITION_3F;
		s2 = rn_x_e * POSITION_3F;
	} else {
		mult = sign(POSITION_3F.z);
		p1 = rp_y_e * POSITION_3F;
		p2 = rp_x_e * POSITION_3F;
		s1 = rn_y_e * POSITION_3F;
		s2 = rn_x_e * POSITION_3F;
	}
	
	p1 = p1 * disp(p1);
	p2 = p2 * disp(p2);
	s1 = s1 * disp(s1);
	s2 = s2 * disp(s2);
	vec3 model_nrm = normalize(mult*cross(p2 - s2, p1 - s1));
	
	
	vec4 wp = U_MODEL_TO_WORLD_M4[gl_InstanceID] * vec4(POSITION_3F * (disp(POSITION_3F)), 1);
	world_pos = vec3(wp);

	shadow_pos = (U_WORLD_TO_SHADOW_M4 * wp); // * vec4(0.5, 0.5, 0.5, 1) + vec4(0.5, 0.5, 0.5, 0);

	world_nrm = mat3x3(U_MODEL_TO_WORLD_M4[gl_InstanceID]) * model_nrm;
	color = COLOR_4F;
	gl_Position = U_WORLD_TO_PROJECTED_M4 * wp;
	
//	v_color_mult = U_COLOR_MULT_F[gl_InstanceID];
	// Transform the normal's orientation into eye space. (this works if there is no non-uniform scaling)
	//v_normal = mat3x3(U_MODEL_TO_VIEW_M4) * NORMAL_3F;
//	v_world_normal = mat3x3(U_MODEL_TO_WORLD_M4[gl_InstanceID]) * NORMAL_3F;
	//v_normal = NORMAL_3F;
	
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	//gl_Position = pos;
}

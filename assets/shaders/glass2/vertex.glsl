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
//uniform float U_COLOR_MULT_F[100];

uniform float U_TIME_F;

in vec3 POSITION_3F;         // Per-vertex position information we will pass in.
in vec3 NORMAL_3F;           // Per-vertex normal information we will pass in.
in vec4 COLOR_4F;            // Per-vertex color information we will pass in.

//out vec3 v_world_pos;
//out vec4 v_color;

out Fragment {
	vec3 world_pos;
	vec3 world_nrm;
	flat vec4 color;
};


//out vec3 v_normal;
//out vec3 v_world_normal;
//out mat4 w_to_p;
//out mat4 m_to_w;
//out vec3 mpos;
//out float v_color_mult;

vec3 angles(vec3 v) {

	return vec3(atan(v.x, sqrt(v.z*v.z + v.y*v.y)),
				atan(v.y, sqrt(v.z*v.z + v.x*v.x)),
				atan(v.z, sqrt(v.x*v.x + v.y*v.y)));

	//return vec2(atan(v.z, sqrt(v.x*v.x + v.y*v.y)),atan(v.y, v.x));
}

//float es = sin(0.01);
//float ec = cos(0.01);



float disp(vec3 v) {
	return 1 + 0.2*cos(U_TIME_F + 2*v.x) + 0.2*sin(U_TIME_F + 3*v.y) + 0.2*sin(U_TIME_F + 4*v.z);
}

// The entry point for our vertex shader.
void main() {

	//vec3 a = angles(POSITION_3F);
	//float disp = 1 + 0.2*cos(U_TIME_F + 7*a.x + 3*a.y + 5*a.z); // + 0.0*sin(U_TIME_F + l.y);
	//float disp = 1 + 0.2*cos(U_TIME_F + 2*POSITION_3F.x) + 0.2*sin(U_TIME_F + 3*POSITION_3F.y) + 0.2*sin(U_TIME_F + 4*POSITION_3F.z); // + 0.0*sin(U_TIME_F + l.y);
	
	vec4 wp = U_MODEL_TO_WORLD_M4[gl_InstanceID] * vec4(POSITION_3F * (disp(POSITION_3F)), 1);
	world_pos = vec3(wp);
	world_nrm = mat3x3(U_MODEL_TO_WORLD_M4[gl_InstanceID]) * NORMAL_3F; // + vec3(0.2*cos(U_TIME_F + 3*POSITION_3F.y),0,0)) ;
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

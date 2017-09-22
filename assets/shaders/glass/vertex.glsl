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

out vec3 v_world_pos;
out vec4 v_color;

//out vec3 v_normal;
//out vec3 v_world_normal;
//out mat4 w_to_p;
//out mat4 m_to_w;
//out vec3 mpos;
//out float v_color_mult;

vec2 latlon(vec3 v) {

	return vec2(atan(v.z/sqrt(v.x*v.x + v.y*v.y)),atan(v.y/v.x));
}

// The entry point for our vertex shader.
void main() {

	vec2 l = latlon(POSITION_3F);

	vec4 world_pos = U_MODEL_TO_WORLD_M4[gl_InstanceID] * vec4(POSITION_3F * (1 + 0.1*cos(U_TIME_F + l.x) + 0.1*sin(U_TIME_F + l.y)), 1);
	
	v_world_pos = vec3(world_pos); //U_MODEL_TO_WORLD_M4[gl_InstanceID] * pos);
	v_color = COLOR_4F;
	gl_Position = U_WORLD_TO_PROJECTED_M4 * world_pos;
	
//	v_color_mult = U_COLOR_MULT_F[gl_InstanceID];
	// Transform the normal's orientation into eye space. (this works if there is no non-uniform scaling)
	//v_normal = mat3x3(U_MODEL_TO_VIEW_M4) * NORMAL_3F;
//	v_world_normal = mat3x3(U_MODEL_TO_WORLD_M4[gl_InstanceID]) * NORMAL_3F;
	//v_normal = NORMAL_3F;
	
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	//gl_Position = pos;
}

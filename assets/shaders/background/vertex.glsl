#version 330 core

uniform mat4 U_MODEL_TO_WORLD_M4;
uniform mat4 U_MODEL_TO_PROJECTED_M4;

in vec3 POSITION_3F;
out vec3 v_world_position;

// The entry point for our vertex shader.
void main() {


	vec4 pos = vec4(POSITION_3F, 1);
	v_world_position = vec3(U_MODEL_TO_WORLD_M4 * pos);
	//v_world_position = mat3x3(U_MODEL_TO_WORLD_M4) * POSITION_3F;
	gl_Position = U_MODEL_TO_PROJECTED_M4 * vec4(POSITION_3F, 1);
}

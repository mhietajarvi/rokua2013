#version 330 core

precision mediump float;  // Set the default precision to medium. We don't need as high of a precision in the fragment shader.
uniform vec3 U_EYE_WORLD_POS_3F;  // The env projection origin in world space.
uniform samplerCube U_ENV_CUBE;
uniform float U_COLOR_MULT_F;     // The position of eye in world space.

in vec3 v_world_position;   // world position of the fragment

out vec4 out_color;

// The entry point for our fragment shader.
void main() {

	vec3 lookAtEnv = v_world_position - U_EYE_WORLD_POS_3F;
	vec4 envcolor = texture(U_ENV_CUBE, lookAtEnv);
	out_color = envcolor; // * U_COLOR_MULT_F;
}

#version 150 core

uniform mat4 U_MODEL_TO_WORLD_M4[2];
uniform mat4 U_WORLD_TO_PROJECTED_M4;

uniform float U_TIME_F;

in vec3 POSITION_3F;
in vec3 NORMAL_3F;
in vec4 COLOR_4F;
in vec3 TEXTURE_3F;

out Fragment {
	vec3 world_pos;
	vec3 world_nrm;
	vec3 tex_coord;
	vec4 color;
};

// standard shader with:
// - N point lights
// - N color textures
// - bump map
// - 
// - shadow map lookup?
// - global illumination?
//   (hardly standard?)
// - ssao?

// occlusion culling, both for:
// - selecting what is seen by camera
// - selecting what objects may cast shadows that are seen by camera (for each light source)

// Light Space Perspective Shadow Maps?

// deep shadows maps as the standard shadow mechanism?
// (only use something else if performance requires)

// interesting mechanism:
//  Dynamic Shadow Volume Generation with Geometry Shaders
//  (also, soft shadows with shadow volumes?)


void main() {
	vec4 wp = U_MODEL_TO_WORLD_M4[gl_InstanceID] * vec4(POSITION_3F, 1);
	world_pos = vec3(wp);
	world_nrm = mat3x3(U_MODEL_TO_WORLD_M4[gl_InstanceID]) * NORMAL_3F;
	color = COLOR_4F;
	tex_coord = TEXTURE_3F; 
	gl_Position = U_WORLD_TO_PROJECTED_M4 * wp;
}

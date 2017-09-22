#version 150 core

precision mediump float;
uniform vec3 U_POINT_LIGHT_1_3F;  // The position of light in world space.
uniform vec3 U_EYE_WORLD_POS_3F;  // The position of eye in world space.
uniform samplerCube U_ENV_CUBE;

//in float f_color_mult;

in Fragment {
	vec3 world_pos;
	flat vec3 world_nrm;
	flat vec4 color;
};

//in vec3 f_world_pos;
//in vec3 f_world_nrm;
//in vec4 f_color;

out vec4 out_color;

// The entry point for our fragment shader.
void main() {

	// point lights
	// directional lights

	// from eye to fragment position
	vec3 from_eye = world_pos - U_EYE_WORLD_POS_3F;

	// these should be calculated for each point light
	float distance = length(U_POINT_LIGHT_1_3F - world_pos);
	vec3 to_light_n = normalize(U_POINT_LIGHT_1_3F - world_pos);
	
	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
	// pointing in the same direction then it will get max illumination.
	float diffuse = max(dot(world_nrm, to_light_n), 0.0);

	
	vec3 reflected = reflect(from_eye, world_nrm);
	vec3 refracted = refract(from_eye, world_nrm, 0.8);
	
	vec4 env_reflect = texture(U_ENV_CUBE, reflected);
	vec4 env_refract = texture(U_ENV_CUBE, refracted);
	
	// Add attenuation.
	//diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));
	
	// Multiply the color by the diffuse illumination level to get final output color.
	//out_color = envcolor; // mix(f_color * diffuse, envcolor, 0.5); //mix(vec3(f_color) * diffuse, out_color, f_color[3]); //
	//out_color = f_color * diffuse;
	//out_color = mix(f_color * diffuse, envcolor, 0.5);
	//out_color = env_reflect * 0.3 + env_refract * 0.3 + f_color * diffuse * 0.4;
//	out_color = env_refract; // + f_color * diffuse * 0.4;
	//out_color = env_reflect;
	//out_color = env_reflect * 0.2 + env_refract * 0.3 + f_color * diffuse * 0.5;
	//out_color = env_reflect * 0.2 + f_color * diffuse * 0.8;

//	out_color = env_reflect * 0.2 + f_color * diffuse * 0.8; // + vec4(0,0,0,0.5);
//	out_color = env_reflect * 0.5 + env_refract * 0.2 + f_color * diffuse * 0.3; // + vec4(0,0,0,0.5);
	
	out_color = env_reflect * 0.25 + env_refract * 0.70;
	
//	out_color = out_color * f_color_mult;
	
	//out_color.w = 0.5;
	//out_color = vec4(distance, distance, distance, 1);
	//out_color = vec4(diffuse, diffuse, diffuse, 1);
}



#version 150 core

precision mediump float;
uniform vec3 U_POINT_LIGHT_1_3F;  // The position of light in world space.
uniform vec3 U_EYE_WORLD_POS_3F;  // The position of eye in world space.
uniform samplerCube U_ENV_CUBE;
//uniform sampler2DShadow U_SHADOW_MAP_1;
uniform sampler2D U_SHADOW_MAP_1;

in Fragment {
	vec3 world_pos;
	vec3 world_nrm;
	vec4 shadow_pos;
	flat vec4 color;
};

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
	
	vec3 wnrm = normalize(world_nrm);
	
	//out_color = vec4(shadow_pos.x < 2.5 ? 1.0 : 0.0, shadow_pos.z < 2.5 ? 1.0 : 0.0 ,0,0);
	
	vec4 h = shadow_pos/shadow_pos.w;

	float t = 1;
	vec4 out_of_shadow_frustrum = vec4(h.x >= t || h.x <= -t ? 1.0 : 0.0, h.y >= t || h.y <= -t ? 1.0 : 0.0 ,h.z >= t || h.z <= -t ? 1.0 : 0.0,0);
	
	h.x = h.x * 0.5 + 0.5;
	h.y = h.y * 0.5 + 0.5;
	h.z = h.z * 0.5 + 0.5; // - 0.01;
	//h.z = 0.98; //h.z * 0.5 + 0.5 - 0.01;

	// Projection._43 / (zw - Projection._33);
	
	// -((2 * nearPlane * farPlane)) / ( z*frustumLength + ((farPlane + nearPlane)))	
	
    //float z_b = texture(U_SHADOW_MAP_1, vec2(h.x, h.y)).r; //texture2D(depthBuffTex, vTexCoord).x;
    //float z_n = 2.0 * z_b - 1.0;
    //float z_e = 2.0 * zNear * zFar / (zFar + zNear - z_n * (zFar - zNear));	
	// result = 1, if fragment is closer to light than shadodw_pos.w;
	
	float seenByLight = h.z - 0.01 <= texture(U_SHADOW_MAP_1, vec2(h.x, h.y)).r ? 1.0 : 0.4;
	//float seenByLight = texture(U_SHADOW_MAP_1, vec2(h.x, h.y)).r > 0.999 ? 1.0 : 0;
	//float seenByLight = h.z > 0.9993 ? 1.0 : 0;
	
	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
	// pointing in the same direction then it will get max illumination.
	float diffuse = max(dot(wnrm, to_light_n), 0.0);

	
	vec3 reflected = reflect(from_eye, wnrm);
	vec3 refracted = refract(from_eye, wnrm, 0.8);
	
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


	if (length(out_of_shadow_frustrum) > 0) {
		out_color = out_of_shadow_frustrum;
	} else {
		out_color = (env_reflect * 0.25 + env_refract * 0.70) * seenByLight;
	}
	
//	out_color = out_color * f_color_mult;
	
	//out_color.w = 0.5;
	//out_color = vec4(distance, distance, distance, 1);
	//out_color = vec4(diffuse, diffuse, diffuse, 1);
}



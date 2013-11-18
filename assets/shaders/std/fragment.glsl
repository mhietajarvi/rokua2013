#version 150 core

precision mediump float;

uniform vec3 U_POINT_LIGHT_1_3F;
uniform vec3 U_EYE_WORLD_POS_3F;
uniform samplerCube U_ENV_CUBE;
uniform sampler2D U_TEXTURE_1;
uniform sampler2D U_TEXTURE_2;

in Fragment {
	vec3 world_pos;
	vec3 world_nrm;
	vec3 tex_coord;
	vec4 color;
};

out vec3 out_color;

// The entry point for our fragment shader.
void main() {

	// from eye to fragment position
	//vec3 from_eye = world_pos - U_EYE_WORLD_POS_3F;

	// these should be calculated for each point light
	//float distance = length(U_POINT_LIGHT_1_3F - world_pos);
	//vec3 to_light_n = normalize(U_POINT_LIGHT_1_3F - world_pos);
	
	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
	// pointing in the same direction then it will get max illumination.
	//float diffuse = max(dot(world_nrm, to_light_n), 0.0);
	
//	vec3 c1 = texture(U_ENV_CUBE, tex_coord).rgb;
	vec3 t1 = texture(U_TEXTURE_1, tex_coord.xy ).rgb;
//	if (t1.x > 0) {
		out_color = t1;
//	} else {
//		out_color = c1;
//	}
	
	
	//out_color = texture( U_TEXTURE_1, tex_coord.xy ).rgb;
	

	//out_color = texture( U_TEXTURE, vec2(world_pos.y, world_pos.y)).rgb;
	//out_color = vec3(world_pos.y, world_pos.y, world_pos.y);

	//out_color = vec3(0,1,0); //texture( U_TEXTURE, uvec2(0.4, 0.3) ).rgb;
	//out_color = texture( U_TEXTURE, U_EYE_WORLD_POS_3F.xy/10).rgb;
	//out_color = vec4(texture(U_TEXTURE, tex_coord.xy)); //texture(U_TEXTURE, uvec2(2,3));

	//out_color = vec4(1.0,0.0,0.0,1.0); //uvec4(134, 134, 134, 255); // texture(U_TEXTURE, uvec2(2,3)); // vec4(tex_coord.x, tex_coord.x, tex_coord.x, 1); // + texture(U_TEXTURE, tex_coord.xy) + color;
	//out_color = uvec4(255,0,0,255);
}



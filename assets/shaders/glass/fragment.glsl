#version 150 core

precision mediump float;
uniform vec3 U_POINT_LIGHT_1_3F;  // The position of light in world space.
uniform vec3 U_EYE_WORLD_POS_3F;     // The position of eye in world space.
uniform samplerCube U_ENV_CUBE;

//in float v_color_mult;


in vec3 v_world_position;      // Interpolated position for this fragment.
in vec4 v_color;         // This is the color from the vertex shader interpolated across the triangle per fragment.
//in vec3 v_normal;        // Interpolated normal for this fragment.
in vec3 v_world_normal;  // Interpolated normal (in world space for env lookup) for this fragment.

// normal in world space

out vec4 out_color;

// The entry point for our fragment shader.
void main() {

	// point lights
	// directional lights

	// Will be used for attenuation.
	float distance = length(U_POINT_LIGHT_1_3F - v_world_position);
	
	// Get a lighting direction vector from the light to the vertex.
	vec3 lightVector = normalize(U_POINT_LIGHT_1_3F - v_world_position);

	// TODO: could we use reflection vector to calc specular lighting
	//       

	
	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
	// pointing in the same direction then it will get max illumination.
	float diffuse = max(dot(v_world_normal, lightVector), 0.0); // lightVector vec3(0, 0, 1)

	// env lookup should be in worlds coordinates, but test with view for now
	
	// v_position is in world
	
	vec3 fromView = v_world_position - U_EYE_WORLD_POS_3F;
	vec3 reflected = fromView - 2*dot(fromView, v_world_normal)*v_world_normal;
	
	vec4 env_reflect = texture(U_ENV_CUBE, reflected); //v_world_normal);
	vec4 env_refract = texture(U_ENV_CUBE, fromView); //v_world_normal);
	
	// Add attenuation.
	//diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));
	
	// Multiply the color by the diffuse illumination level to get final output color.
	//out_color = envcolor; // mix(v_color * diffuse, envcolor, 0.5); //mix(vec3(v_color) * diffuse, out_color, v_color[3]); //
	//out_color = v_color * diffuse;
	//out_color = mix(v_color * diffuse, envcolor, 0.5);
	//out_color = env_reflect * 0.3 + env_refract * 0.3 + v_color * diffuse * 0.4;
	//out_color = env_refract;
	//out_color = env_reflect;
	//out_color = env_reflect * 0.2 + env_refract * 0.3 + v_color * diffuse * 0.5;
	//out_color = env_reflect * 0.2 + v_color * diffuse * 0.8;
	out_color = env_reflect * 0.2 + v_color * diffuse * 0.8; // + vec4(0,0,0,0.5);
	
//	out_color = out_color * v_color_mult;
	
	//out_color.w = 0.5;
	//out_color = vec4(distance, distance, distance, 1);
	//out_color = vec4(diffuse, diffuse, diffuse, 1);
}



#version 330 core

// Ouput data
//out vec3 color;
//void main()
//{
	// Output color = red 
//	color = vec3(1,0,0);
//}


precision mediump float;  // Set the default precision to medium. We don't need as high of a precision in the fragment shader.
uniform vec3 U_POINT_LIGHT_1_3F;  // The position of the light in eye space.

in vec3 v_position;  // Interpolated position for this fragment.
in vec4 v_color;     // This is the color from the vertex shader interpolated across the triangle per fragment.
in vec3 v_normal;    // Interpolated normal for this fragment.

out vec4 out_color;

// The entry point for our fragment shader.
void main() {

	// point lights
	// directional lights

	// Will be used for attenuation.
	float distance = length(U_POINT_LIGHT_1_3F - v_position);
	
	// Get a lighting direction vector from the light to the vertex.
	vec3 lightVector = normalize(U_POINT_LIGHT_1_3F - v_position);
	
	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
	// pointing in the same direction then it will get max illumination.
	float diffuse = max(dot(v_normal, lightVector), 0.1);
	
	// Add attenuation.
	// diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));
	
	// Multiply the color by the diffuse illumination level to get final output color.
	out_color = v_color * diffuse;
}

#version 330 core

// Ouput data
//out vec3 color;
//void main()
//{
	// Output color = red 
//	color = vec3(1,0,0);
//}


precision mediump float;  // Set the default precision to medium. We don't need as high of a precision in the fragment shader.
uniform vec3 u_LightPos;  // The position of the light in eye space.

in vec3 v_Position;  // Interpolated position for this fragment.
in vec4 v_Color;     // This is the color from the vertex shader interpolated across the triangle per fragment.
in vec3 v_Normal;    // Interpolated normal for this fragment.

out vec4 out_Color;

// The entry point for our fragment shader.
void main() {

	// Will be used for attenuation.
	float distance = length(u_LightPos - v_Position);
	
	// Get a lighting direction vector from the light to the vertex.
	vec3 lightVector = normalize(u_LightPos - v_Position);
	
	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
	// pointing in the same direction then it will get max illumination.
	float diffuse = max(dot(v_Normal, lightVector), 0.1);
	
	// Add attenuation.
	// diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));
	
	// Multiply the color by the diffuse illumination level to get final output color.
	out_Color = v_Color * diffuse;
}

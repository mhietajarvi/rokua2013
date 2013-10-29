#version 330 core

// Input vertex data, different for all executions of this shader.
// layout(location = 0) in vec3 vertexPosition_modelspace;
//void main(){
//    gl_Position.xyz = vertexPosition_modelspace;
//    gl_Position.w = 1.0;
//}


uniform mat4 u_MVPMatrix;    // A constant representing the combined model/view/projection matrix.
uniform mat4 u_MVMatrix;     // A constant representing the combined model/view matrix.

in vec3 a_Position;   // Per-vertex position information we will pass in.
in vec4 a_Color;      // Per-vertex color information we will pass in.
in vec3 a_Normal;     // Per-vertex normal information we will pass in.

out vec3 v_Position;         // This will be passed into the fragment shader.
out vec4 v_Color;            // This will be passed into the fragment shader.
out vec3 v_Normal;           // This will be passed into the fragment shader.

// The entry point for our vertex shader.
void main() {

    vec4 pos = vec4(a_Position, 1);
        
	//v_Position = a_Position;
    // Transform the vertex into eye space.
	v_Position = vec3(u_MVMatrix * pos);
	
    // Pass through the color.
	v_Color = a_Color;
	
	// Transform the normal's orientation into eye space.
	//v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));
	v_Normal = a_Normal;
	
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
//	gl_Position = u_MVPMatrix * pos;
	gl_Position = pos;
}

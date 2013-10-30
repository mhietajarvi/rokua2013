#version 330 core

// Input vertex data, different for all executions of this shader.
// layout(location = 0) in vec3 vertexPosition_modelspace;
//void main(){
//    gl_Position.xyz = vertexPosition_modelspace;
//    gl_Position.w = 1.0;
//}


uniform mat4 U_MODEL_TO_PROJECTED_M4;    // A constant representing the combined model/view/projection matrix.
uniform mat4 U_MODEL_TO_VIEW_M4;     // A constant representing the combined model/view matrix.

in vec3 POSITION_3F;         // Per-vertex position information we will pass in.
in vec4 COLOR_4F;            // Per-vertex color information we will pass in.
in vec3 NORMAL_3F;           // Per-vertex normal information we will pass in.

out vec3 v_position;         // This will be passed into the fragment shader.
out vec4 v_color;            // This will be passed into the fragment shader.
out vec3 v_normal;           // This will be passed into the fragment shader.

// The entry point for our vertex shader.
void main() {

    vec4 pos = vec4(POSITION_3F, 1);
        
	//v_Position = a_Position;
    // Transform the vertex into eye space.
	v_position = vec3(U_MODEL_TO_VIEW_M4 * pos);
	
    // Pass through the color.
	v_color = COLOR_4F;
	
	// Transform the normal's orientation into eye space. (this works if there is no non-uniform scaling)
	v_normal = vec3(U_MODEL_TO_VIEW_M4 * vec4(NORMAL_3F, 0.0));
	//v_normal = NORMAL_3F;
	
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	gl_Position = U_MODEL_TO_PROJECTED_M4 * pos;
	//gl_Position = pos;
}

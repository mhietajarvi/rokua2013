#version 330 core

// Input vertex data, different for all executions of this shader.
// layout(location = 0) in vec3 vertexPosition_modelspace;
//void main(){
//    gl_Position.xyz = vertexPosition_modelspace;
//    gl_Position.w = 1.0;
//}


// some own color + reflected + refracted
// need 

uniform mat4 U_MODEL_TO_WORLD_M4;
//uniform mat4 U_MODEL_TO_VIEW_M4;
uniform mat4 U_MODEL_TO_PROJECTED_M4;

in vec3 POSITION_3F;         // Per-vertex position information we will pass in.
in vec3 NORMAL_3F;           // Per-vertex normal information we will pass in.
in vec4 COLOR_4F;            // Per-vertex color information we will pass in.

out vec3 v_world_position;   // This will be passed into the fragment shader.
//out vec3 v_normal;           // This will be passed into the fragment shader.
out vec4 v_color;            // This will be passed into the fragment shader.
out vec3 v_world_normal;

// The entry point for our vertex shader.
void main() {

    vec4 pos = vec4(POSITION_3F, 1);
        
	//v_Position = a_Position;
    // Transform the vertex into eye space.
	//v_position = vec3(U_MODEL_TO_VIEW_M4 * pos);
	v_world_position = vec3(U_MODEL_TO_WORLD_M4 * pos);
	
    // Pass through the color.
	v_color = COLOR_4F; //vec4(v_position, 1); //COLOR_4F;
	
	// Transform the normal's orientation into eye space. (this works if there is no non-uniform scaling)
	//v_normal = mat3x3(U_MODEL_TO_VIEW_M4) * NORMAL_3F;
	v_world_normal = mat3x3(U_MODEL_TO_WORLD_M4) * NORMAL_3F;
	//v_normal = NORMAL_3F;
	
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	gl_Position = U_MODEL_TO_PROJECTED_M4 * pos;
	//gl_Position = pos;
}

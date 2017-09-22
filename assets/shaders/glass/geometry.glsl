#version 150 core

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out; 

in vec3 v_world_pos[3];
in vec4 v_color[3];

out Fragment {
	vec3 world_pos;
	flat vec3 world_nrm;
	flat vec4 color;
};

//out vec3 f_world_pos;
//out vec3 f_world_nrm;
//out vec4 f_color;
// in mat4 w_to_p[3];
// in mat4 m_to_w[3];
//out float v_color_mult;

// The entry point for our vertex shader.
void main() {
	
	// modify model vertex
	// transform to world
	// world pos
	// v_world_position_v[i]
	//vec3 n = normalize(cross(v_world_pos[2] - v_world_pos[0], v_world_pos[1] - v_world_pos[0]));
	
	world_nrm = normalize(cross(v_world_pos[2] - v_world_pos[0], v_world_pos[1] - v_world_pos[0]));
	color = v_color[0]; 

	for (int i = 0; i < 3; i++) {
	
    	//vec4 pos = vec4(POSITION_3F * (1 + 0.1*cos(U_TIME_F + l.x) + 0.1*sin(U_TIME_F + l.y)), 1);
    	//vec4 pos = vec4(v_position[i], 1);
		//v_world_normal = n;
		//v_world_position = vec3(0,1,i);
		//v_world_normal = vec3(0,1,0);
		//v_color = color[i]; //vec4(0,0,0,1);
		
		world_pos = v_world_pos[i];
		gl_Position = gl_in[i].gl_Position;
		EmitVertex();
	}
	EndPrimitive();
}

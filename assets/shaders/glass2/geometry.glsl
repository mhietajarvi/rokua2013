#version 150 core

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out; 

in vec3 v_world_pos[3];
in vec4 v_color[3];

out Fragment {
	vec3 world_pos;
	vec3 world_nrm;
	flat vec4 color;
};

// The entry point for our vertex shader.
void main() {
	
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

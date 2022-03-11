#version 120

attribute vec2 vertices;

uniform mat4 projection;

void main() {
	gl_Position = projection * vec4(vertices, 0, 1);
}

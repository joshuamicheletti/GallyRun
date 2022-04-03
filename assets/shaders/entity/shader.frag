#version 120

uniform sampler2D sampler;

uniform float opacity;

varying vec2 tex_coords;

void main () {
	vec4 texColor = texture2D(sampler, tex_coords);

	if (texColor.a < 0.1)
		discard;

	gl_FragColor = vec4(texture2D(sampler, tex_coords).xyz, opacity);
}

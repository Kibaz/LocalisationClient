varying vec3 vert_colour;

uniform float opacity;

void main(void)
{
	gl_FragColor = vec4(vert_colour,opacity);
}
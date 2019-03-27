attribute vec3 position;

varying vec3 vert_colour;

uniform mat4 transformationMatrix;
uniform vec3 colour;

void main(void)
{
    gl_Position = transformationMatrix * vec4(position,1.0);
    vert_colour = colour;
}
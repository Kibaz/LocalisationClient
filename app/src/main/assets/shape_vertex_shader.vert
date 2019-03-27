attribute vec3 position;

varying vec3 vert_colour;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform vec3 colour;

void main(void)
{
    gl_Position = viewMatrix * transformationMatrix * vec4(position,1.0);
    vert_colour = colour;
}
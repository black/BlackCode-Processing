/**
 * Filter shader
 * by Andres Colubri. December 2008.
 * 
 * This GLSL fragment shader implements a mean convolution filter.
 *
 */
 
#define KERNEL_SIZE 9

// Gaussian kernel
// 1 2 1
// 2 4 2
// 1 2 1
float kernel[KERNEL_SIZE];

uniform sampler2D src_tex_unit0;
uniform vec2 src_tex_offset0;

vec2 offset[KERNEL_SIZE];

void main(void)
{
    int i = 0;
    vec4 sum = vec4(0.0);

    offset[0] = vec2(-src_tex_offset0.s, -src_tex_offset0.t);
    offset[1] = vec2(0.0, -src_tex_offset0.t);
    offset[2] = vec2(src_tex_offset0.s, -src_tex_offset0.t);

    offset[3] = vec2(-src_tex_offset0.s, 0.0);
    offset[4] = vec2(0.0, 0.0);
    offset[5] = vec2(src_tex_offset0.s, 0.0);

    offset[6] = vec2(-src_tex_offset0.s, src_tex_offset0.t);
    offset[7] = vec2(0.0, src_tex_offset0.t);
    offset[8] = vec2(src_tex_offset0.s, src_tex_offset0.t);

    for(i = 0; i < KERNEL_SIZE; i++)
    {
        sum += texture2D(src_tex_unit0, gl_TexCoord[0].st + offset[i]);
    }

    gl_FragColor = vec4(sum.rgb / 9.0, 1.0);
}

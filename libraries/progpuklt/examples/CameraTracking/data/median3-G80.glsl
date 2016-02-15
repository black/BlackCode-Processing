/*
3x3 Median optimized for GeForce 8800

Morgan McGuire and Kyle Whitson
Williams College

Register allocation tips by Victor Huang Xiaohuang
University of Illinois at Urbana-Champaign

http://graphics.cs.williams.edu/papers/MedianShaderX
*/

uniform sampler2D src_tex_unit0;
uniform vec2 src_tex_offset0;

// Change these 2 defines to change precision,
//#define vec vec3
//#define toVec(x) x.rgb

#define vec vec4
#define toVec(x) x.rgba

#define s2(a, b)				temp = a; a = min(a, b); b = max(temp, b);
#define mn3(a, b, c)			s2(a, b); s2(a, c);
#define mx3(a, b, c)			s2(b, c); s2(a, c);

#define mnmx3(a, b, c)			mx3(a, b, c); s2(a, b);                                   // 3 exchanges
#define mnmx4(a, b, c, d)		s2(a, b); s2(c, d); s2(a, c); s2(b, d);                   // 4 exchanges
#define mnmx5(a, b, c, d, e)	s2(a, b); s2(c, d); mn3(a, c, e); mx3(b, d, e);           // 6 exchanges
#define mnmx6(a, b, c, d, e, f) s2(a, d); s2(b, e); s2(c, f); mn3(a, b, c); mx3(d, e, f); // 7 exchanges

void main() {

  vec v[6];

  v[0] = toVec(texture2D(src_tex_unit0, gl_TexCoord[0].xy + vec2(-1.0, -1.0) * src_tex_offset0));
  v[1] = toVec(texture2D(src_tex_unit0, gl_TexCoord[0].xy + vec2( 0.0, -1.0) * src_tex_offset0));
  v[2] = toVec(texture2D(src_tex_unit0, gl_TexCoord[0].xy + vec2(+1.0, -1.0) * src_tex_offset0));
  v[3] = toVec(texture2D(src_tex_unit0, gl_TexCoord[0].xy + vec2(-1.0,  0.0) * src_tex_offset0));
  v[4] = toVec(texture2D(src_tex_unit0, gl_TexCoord[0].xy + vec2( 0.0,  0.0) * src_tex_offset0));
  v[5] = toVec(texture2D(src_tex_unit0, gl_TexCoord[0].xy + vec2(+1.0,  0.0) * src_tex_offset0));

  // Starting with a subset of size 6, remove the min and max each time
  vec temp;
  mnmx6(v[0], v[1], v[2], v[3], v[4], v[5]);

  v[5] = toVec(texture2D(src_tex_unit0, gl_TexCoord[0].xy + vec2(-1.0, +1.0) * src_tex_offset0));

  mnmx5(v[1], v[2], v[3], v[4], v[5]);

  v[5] = toVec(texture2D(src_tex_unit0, gl_TexCoord[0].xy + vec2( 0.0, +1.0) * src_tex_offset0);

  mnmx4(v[2], v[3], v[4], v[5]);

  v[5] = toVec(texture2D(src_tex_unit0, gl_TexCoord[0].xy + vec2(+1.0, +1.0) * src_tex_offset0);

  mnmx3(v[3], v[4], v[5]);
  toVec(gl_FragColor) = v[4];
}

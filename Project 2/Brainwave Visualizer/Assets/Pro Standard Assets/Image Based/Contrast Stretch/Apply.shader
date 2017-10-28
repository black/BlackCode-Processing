// Final pass in the contrast stretch effect: apply
// color stretch to the original image, based on currently
// adapted to minimum/maximum luminances.

Shader "Hidden/Contrast Stretch Apply" {
Properties {
	_MainTex ("Base (RGB)", RECT) = "white" {}
	_AdaptTex ("Base (RGB)", RECT) = "white" {}
}

Category {
	SubShader {
		Pass {
			ZTest Always Cull Off ZWrite Off
			Fog { Mode off }
				
CGPROGRAM
#pragma vertex vert
#pragma fragment frag
#pragma fragmentoption ARB_precision_hint_fastest 
#include "UnityCG.cginc"

struct v2f {
	float4 pos		: POSITION;
	float2 uv[2]	: TEXCOORD0;
}; 

uniform samplerRECT _MainTex;
uniform samplerRECT _AdaptTex;

v2f vert (appdata_img v)
{
	v2f o;
	o.pos = mul (glstate.matrix.mvp, v.vertex);
	o.uv[0] = MultiplyUV (glstate.matrix.texture[0], v.texcoord);
	o.uv[1] = float2(0.5,0.5);
	return o;
}

float4 frag (v2f i) : COLOR
{
	float4 col = texRECT(_MainTex, i.uv[0]);
	float4 adapted = texRECT(_AdaptTex, i.uv[1]);
	float vmul = 1.0 / adapted.z;
	float vadd = -adapted.w;
	col.rgb = col.rgb * vmul + vadd;	
	return col;
}
ENDCG

		}
	}
}

Fallback off

}
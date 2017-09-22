#version 150 core

vec3 mod289(vec3 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}
vec4 mod289(vec4 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}
vec4 permute(vec4 x) {
     return mod289(((x*34.0)+1.0)*x);
}
vec4 taylorInvSqrt(vec4 r)
{
  return 1.79284291400159 - 0.85373472095314 * r;
}
float snoise(vec3 v)
  {
  const vec2 C = vec2(1.0/6.0, 1.0/3.0) ;
  const vec4 D = vec4(0.0, 0.5, 1.0, 2.0);
  vec3 i = floor(v + dot(v, C.yyy) );
  vec3 x0 = v - i + dot(i, C.xxx) ;
  vec3 g = step(x0.yzx, x0.xyz);
  vec3 l = 1.0 - g;
  vec3 i1 = min( g.xyz, l.zxy );
  vec3 i2 = max( g.xyz, l.zxy );
  vec3 x1 = x0 - i1 + C.xxx;
  vec3 x2 = x0 - i2 + C.yyy;
  vec3 x3 = x0 - D.yyy;
  i = mod289(i);
  vec4 p = permute( permute( permute(
             i.z + vec4(0.0, i1.z, i2.z, 1.0 ))
           + i.y + vec4(0.0, i1.y, i2.y, 1.0 ))
           + i.x + vec4(0.0, i1.x, i2.x, 1.0 ));
  float n_ = 0.142857142857;
  vec3 ns = n_ * D.wyz - D.xzx;
  vec4 j = p - 49.0 * floor(p * ns.z * ns.z);
  vec4 x_ = floor(j * ns.z);
  vec4 y_ = floor(j - 7.0 * x_ );
  vec4 x = x_ *ns.x + ns.yyyy;
  vec4 y = y_ *ns.x + ns.yyyy;
  vec4 h = 1.0 - abs(x) - abs(y);
  vec4 b0 = vec4( x.xy, y.xy );
  vec4 b1 = vec4( x.zw, y.zw );
  vec4 s0 = floor(b0)*2.0 + 1.0;
  vec4 s1 = floor(b1)*2.0 + 1.0;
  vec4 sh = -step(h, vec4(0.0));
  vec4 a0 = b0.xzyw + s0.xzyw*sh.xxyy ;
  vec4 a1 = b1.xzyw + s1.xzyw*sh.zzww ;
  vec3 p0 = vec3(a0.xy,h.x);
  vec3 p1 = vec3(a0.zw,h.y);
  vec3 p2 = vec3(a1.xy,h.z);
  vec3 p3 = vec3(a1.zw,h.w);
  vec4 norm = taylorInvSqrt(vec4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3)));
  p0 *= norm.x;
  p1 *= norm.y;
  p2 *= norm.z;
  p3 *= norm.w;
  vec4 m = max(0.6 - vec4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0);
  m = m * m;
  return 42.0 * dot( m*m, vec4( dot(p0,x0), dot(p1,x1),
                                dot(p2,x2), dot(p3,x3) ) );
  }
  
precision mediump float;
uniform vec3 U_POINT_LIGHT_1_3F;  // The position of light in world space.
uniform vec3 U_EYE_WORLD_POS_3F;  // The position of eye in world space.
uniform samplerCube U_ENV_CUBE;
uniform float U_TIME_F;

in Fragment {
	vec3 model_pos;
	vec3 world_pos;
	vec3 world_nrm;
	flat vec4 color;
};

out vec4 out_color;

vec3 glass() {

	vec3 from_eye = world_pos - U_EYE_WORLD_POS_3F;
	
	vec3 reflected = reflect(from_eye, world_nrm);
	vec3 refracted = refract(from_eye, world_nrm, 0.8);
	
	vec3 env_reflect = vec3(texture(U_ENV_CUBE, reflected));
	vec3 env_refract = vec3(texture(U_ENV_CUBE, refracted));
	
	return env_reflect * 0.25 + env_refract * 0.70;
}

void main( void )
{
	float t = 0; //U_TIME_F;

	vec3 uvw = model_pos + 0.1*vec3(snoise(model_pos + vec3(0.0, 0.0, t)),
	snoise(model_pos + vec3(43.0, 17.0, t)),
	snoise(model_pos + vec3(-17.0, -43.0, t)));
	float n = snoise(uvw - vec3(0.0, 0.0, t));
	n += 0.5 * snoise(uvw * 2.0 - vec3(0.0, 0.0, t*1.4));
	n += 0.25 * snoise(uvw * 4.0 - vec3(0.0, 0.0, t*2.0));
	n += 0.125 * snoise(uvw * 8.0 - vec3(0.0, 0.0, t*2.8));
	n += 0.0625 * snoise(uvw * 16.0 - vec3(0.0, 0.0, t*4.0));
	n += 0.03125 * snoise(uvw * 32.0 - vec3(0.0, 0.0, t*5.6));
	n = n * 0.7;
	out_color = vec4(vec3(1.0, 0.5, 0.0) + vec3(n, n, n) + 0.4*glass(), 1.0);
}

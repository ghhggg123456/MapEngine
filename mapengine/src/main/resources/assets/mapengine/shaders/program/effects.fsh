#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D BlurSampler;
uniform sampler2D PrevFrameSampler;

uniform vec2  OutSize;
uniform float GameTime;

uniform float PixelSize;
uniform vec3  TintColor;
uniform vec3  Saturation;
uniform float Hue;
uniform float Contrast;
uniform float Brightness;
uniform float Posterize;
uniform float NoiseSize;
uniform float NoiseRate;
uniform float Chromatic;
uniform float Blur;
uniform float MotionStrength;
uniform float MotionX;
uniform float MotionY;
uniform vec3  VignetteColor;
uniform float VignetteSize;
uniform vec3  OutlineColor;
uniform float OutlineSize;

in vec2 texCoord;
out vec4 fragColor;

float hash(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec2 screenUv = texCoord;
    vec2 uv = texCoord;

    // ---- pixelize ----
    if (PixelSize > 1.5) {
        vec2 px = OutSize / PixelSize;
        uv = (min(floor(uv * px), px - 1.0) + 0.5) / px;
    }

    // ---- chromatic ----
    vec4 color;
    if (Chromatic > 0.01) {
        vec2 dir = uv - vec2(0.5);
        float dist = length(dir);
        vec2 offset = dir * dist * Chromatic * 0.001;
        color = vec4(
            texture(DiffuseSampler, uv + offset).r,
            texture(DiffuseSampler, uv).g,
            texture(DiffuseSampler, uv - offset).b,
            1.0
        );
    } else {
        color = texture(DiffuseSampler, uv);
    }

    // ---- noise ----
    if (NoiseSize > 0.5 && NoiseRate > 0.5) {
        vec2 npx = OutSize / max(NoiseSize, 1.0);
        vec2 cell = floor(uv * npx);
        float t = floor(GameTime * NoiseRate / 5.0);
        float n = hash(cell + vec2(t * 17.31, t * 13.73));
        color.rgb = mix(color.rgb, vec3(n), 0.2);
    }

    // ---- tint ----
    color.rgb *= TintColor;

    // ---- per-channel saturation ----
    float lum = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    color.r = mix(lum, color.r, Saturation.r);
    color.g = mix(lum, color.g, Saturation.g);
    color.b = mix(lum, color.b, Saturation.b);

    // ---- hue ----
    if (Hue > 0.001) {
        vec3 hsv = rgb2hsv(color.rgb);
        hsv.x = fract(hsv.x + Hue / 360.0);
        color.rgb = hsv2rgb(hsv);
    }

    // ---- blur ----
    if (Blur > 0.01) {
        float blurFactor = clamp(Blur / 30.0, 0.0, 1.0);
        color.rgb = mix(color.rgb, texture(BlurSampler, uv).rgb, blurFactor);
    }

    // ---- contrast / brightness ----
    color.rgb = (color.rgb - 0.5) * Contrast + 0.5;
    color.rgb *= Brightness;

    // ---- posterize ----
    if (Posterize > 1.5) {
        color.rgb = floor(color.rgb * Posterize) / Posterize;
    }

    // ---- motion blur ----
    if (MotionStrength > 0.01) {
        vec2 motionUv = vec2(MotionX, MotionY) * MotionStrength;
        vec3 accum = color.rgb;
        for (int i = 1; i <= 4; i++) {
            float t = float(i) / 5.0;
            accum += texture(PrevFrameSampler, uv + motionUv * t).rgb;
        }
        color.rgb = accum / 5.0;
    }

    color.rgb = clamp(color.rgb, 0.0, 1.0);

    // ---- vignette ----
    if (VignetteSize > 0.5) {
        vec2 c = screenUv - 0.5;
        float dist = length(c) * 1.41421356;
        float inner = 1.0 - VignetteSize / 50.0;
        float a = smoothstep(inner, 1.0, dist) * 0.85;
        color.rgb = mix(color.rgb, VignetteColor, a);
    }

    // ---- outline ----
    if (OutlineSize > 0.5) {
        vec2 edgePx = min(screenUv, 1.0 - screenUv) * OutSize;
        float d = min(edgePx.x, edgePx.y);
        float a = clamp(1.0 - d / (OutlineSize * 2.0), 0.0, 1.0);
        color.rgb = mix(color.rgb, OutlineColor, a);
    }

    fragColor = vec4(color.rgb, 1.0);
}
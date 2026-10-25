#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
uniform vec2 BlurDir;
uniform float Radius;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    if (Radius < 0.5) {
        fragColor = texture(DiffuseSampler, texCoord);
        return;
    }

    vec2 texel = 1.0 / OutSize;
    vec4 blurred = vec4(0.0);
    float actualRadius = round(clamp(Radius, 1.0, 16.0));
    
    for (float a = -actualRadius + 0.5; a <= actualRadius; a += 2.0) {
        blurred += texture(DiffuseSampler, texCoord + (texel * BlurDir) * a);
    }
    blurred += texture(DiffuseSampler, texCoord + (texel * BlurDir) * actualRadius) / 2.0;
    
    fragColor = blurred / (actualRadius + 0.5);
}
#ifdef GL_ES
precision mediump float;
#endif
//SpriteBatch will use texture unit 0
uniform sampler2D u_texture;
uniform float shadeTimer;
uniform float u_amount;

//"in" varyings from our vertex shader
varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
    //sample the texture
    vec4 texColor = v_color * texture2D(u_texture, v_texCoord);
    float grayscale = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
    texColor.rgb = vec3(grayscale);
    //final color
    gl_FragColor = texColor;
}
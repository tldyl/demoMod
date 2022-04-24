//SpriteBatch will use texture unit 0
uniform sampler2D u_texture;
uniform float shadeTimer;

//"in" varyings from our vertex shader
varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
    //sample the texture
    /*
    texture2D(texture, texCoord)
    查询texture，返回在其texCoord(一个二维向量，用来表示坐标)位置的颜色值
    */
    vec4 texColor = texture2D(u_texture, v_texCoord);
    texColor *= vec4(0.5, 0, 0, 1);

    //final color 一次只能给一个像素着色
    gl_FragColor = texColor;
}
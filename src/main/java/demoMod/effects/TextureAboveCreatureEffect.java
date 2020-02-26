package demoMod.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.vfx.RelicAboveCreatureEffect;

public class TextureAboveCreatureEffect extends RelicAboveCreatureEffect {

    private Texture texture;
    private float x;
    private float y;
    private float offsetY;
    private Color shineColor = new Color(1.0F, 1.0F, 1.0F, 0.0F);

    public TextureAboveCreatureEffect(AbstractCreature source, Texture texture) {
        super (source.hb.cX - source.animX, source.hb.cY + source.hb.height / 2.0F - source.animY, null);
        this.x = source.hb.cX - source.animX;
        this.y = source.hb.cY + source.hb.height / 2.0F - source.animY;
        this.offsetY = 0;
        this.texture = texture;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.draw(this.texture, this.x - 64.0F, this.y - 64.0F + this.offsetY, 64.0F, 64.0F, 128.0F, 128.0F, this.scale * (2.5F - this.duration), this.scale * (2.5F - this.duration), this.rotation, 0, 0, 39, 34, false, false);
        sb.setBlendFunction(770, 1);
        this.shineColor.a = this.color.a / 4.0F;
        sb.setColor(this.shineColor);
        sb.draw(this.texture, this.x - 64.0F, this.y - 64.0F + this.offsetY, 64.0F, 64.0F, 128.0F, 128.0F, this.scale * (2.7F - this.duration), this.scale * (2.7F - this.duration), this.rotation, 0, 0, 39, 34, false, false);
        sb.draw(this.texture, this.x - 64.0F, this.y - 64.0F + this.offsetY, 64.0F, 64.0F, 128.0F, 128.0F, this.scale * (3.0F - this.duration), this.scale * (3.0F - this.duration), this.rotation, 0, 0, 39, 34, false, false);
        sb.setBlendFunction(770, 771);
    }
}

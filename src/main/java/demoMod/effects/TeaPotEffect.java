package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class TeaPotEffect extends AbstractGameEffect {

    private TextureRegion img;

    private float a = 1.0F;
    private float scale = 1.5F;
    private float vS = 0.6F;
    private float x;
    private float y;

    public TeaPotEffect(float x, float y, boolean isLastBullet) {
        if (isLastBullet) {
            this.img = new TextureRegion(new Texture("DemoImages/effects/teaPot_2.png"));
        } else {
            this.img = new TextureRegion(new Texture("DemoImages/effects/teaPot.png"));
        }
        this.x = x;
        this.y = y;
        this.duration = 0.5F;
    }

    @Override
    public void update() {
        this.a -= 0.0334F;
        if (this.a < 0) this.a = 0;
        this.scale += this.vS;
        vS -= 0.02F;
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 0) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1.0F,1.0F,1.0F, this.a);
        sb.draw(this.img, this.x - 48.0F * Settings.scale,
                this.y + 48.0F * Settings.scale,
                32,
                32,
                this.img.getRegionWidth() * 1.5F * Settings.scale,
                this.img.getRegionHeight() * 1.5F * Settings.scale,
                this.scale, this.scale, this.rotation);
    }

    @Override
    public void dispose() {

    }
}

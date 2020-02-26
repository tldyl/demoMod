package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class MazeFogEffect extends AbstractGameEffect {
    private Texture fog;
    private int x = -1920;
    private int v;
    private float a;

    public MazeFogEffect(Texture fog) {
        this.fog = fog;
        this.duration = MathUtils.random(16.0F, 30.0F);
        this.startingDuration = this.duration;
        this.v = (int) (3840 * Gdx.graphics.getDeltaTime() / this.duration);
        this.a = 0.0F;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();

        this.x += this.v;
        if (this.startingDuration - this.duration <= 3.0F) {
            this.a += Gdx.graphics.getDeltaTime() / 3;
            if (this.a > 1) {
                this.a = 1;
            }
        }
        if (this.duration <= 3.0F) {
            this.a -= Gdx.graphics.getDeltaTime() / 3;
            if (this.a < 0) {
                this.a = 0;
            }
        }

        if (this.duration <= 0) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, this.a);
        sb.draw(new TextureRegion(this.fog), this.x, Settings.HEIGHT - 1136,
                0,
                0,
                1920,
                1136,
                1.0F, 1.0F, this.rotation);
    }

    @Override
    public void dispose() {

    }
}

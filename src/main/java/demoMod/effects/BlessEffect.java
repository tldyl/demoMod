package demoMod.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;

public class BlessEffect extends AbstractGameEffect {
    private float x;
    private float y;
    private static Texture[] frames;
    private int idx = 0;

    static {
        frames = new Texture[36];
        for (int i=100000;i<100036;i++) {
            String n = Integer.toString(i).substring(1);
            frames[i - 100000] = new Texture("DemoImages/effects/bless/bless_" + n + ".png");
        }
    }

    public BlessEffect(float cX, float cY) {
        this.x = cX;
        this.y = cY;
        this.isDone = true;
        this.renderBehind = true;
    }

    public void start() {
        this.isDone = false;
        this.idx = 0;
    }

    public void stop() {
        this.isDone = true;
    }

    @Override
    public void update() {
        idx++;
        if (DemoMod.frameRateRemap(idx, 60, frames.length - 1) >= frames.length) {
            idx = 0;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, 1);
        sb.draw(frames[DemoMod.frameRateRemap(idx, 60, frames.length - 1)], this.x - 144.0F * Settings.scale,
                this.y - 72.0F * Settings.scale, 288.0F * Settings.scale, 144.0F * Settings.scale);
    }

    @Override
    public void dispose() {

    }
}

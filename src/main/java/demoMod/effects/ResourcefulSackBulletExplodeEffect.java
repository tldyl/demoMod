package demoMod.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;

public class ResourcefulSackBulletExplodeEffect extends AbstractGameEffect {
    private static final TextureRegion[] frames;
    private int myDuration = 94;
    private int idx = -1;
    private AbstractCreature target;

    private float XRan = (MathUtils.random(96.0F) - 48.0F) * Settings.scale;
    private float YRan = (MathUtils.random(96.0F) - 48.0F) * Settings.scale;

    public ResourcefulSackBulletExplodeEffect(AbstractCreature target) {
        this.duration = 1.57F;
        this.target = target;
    }

    @Override
    public void update() {
        if (myDuration == 94) {
            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, false);
        }
        idx++;
        if (DemoMod.frameRateRemap(idx, 30, frames.length - 1) >= frames.length) {
            idx = 0;
        }
        myDuration--;
        if (myDuration < 0) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, 1);
        sb.draw(frames[DemoMod.frameRateRemap(idx, 30, frames.length - 1)], target.hb.cX + XRan - 384.0F * Settings.scale,
                target.hb.cY + YRan - 384.0F * Settings.scale,
                256,
                256,
                768.0F * Settings.scale,
                768.0F * Settings.scale,
                1.0F, 1.0F, this.rotation);
    }

    @Override
    public void dispose() {

    }

    static {
        frames = new TextureRegion[47];
        for (int i=100000;i<100047;i++) {
            String n = Integer.toString(i).substring(1);
            frames[i - 100000] = new TextureRegion(new Texture("DemoImages/effects/resourcefulSack/resourcefulSack_" + n + ".png"));
        }
    }
}

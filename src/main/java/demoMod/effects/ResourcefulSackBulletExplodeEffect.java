package demoMod.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;

public class ResourcefulSackBulletExplodeEffect extends AbstractGameEffect {
    private static final TextureRegion[] frames;
    private int myDuration = 94;
    private int idx = -1;
    private AbstractCreature target;

    private int XRan = MathUtils.random(64) - 32;
    private int YRan = MathUtils.random(64) - 32;

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
        sb.draw(frames[DemoMod.frameRateRemap(idx, 30, frames.length - 1)], target.hb.cX + XRan - 256,
                target.hb.cY + YRan - 256,
                256,
                256,
                512,
                512,
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

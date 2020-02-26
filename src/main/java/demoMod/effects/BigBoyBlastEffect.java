package demoMod.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;
import demoMod.sounds.DemoSoundMaster;

public class BigBoyBlastEffect extends AbstractGameEffect {
    private static final TextureRegion[] frames;
    private int myDuration = 132;
    private int idx = 0;

    public BigBoyBlastEffect() {
        this.duration = 2.0F;
    }

    @Override
    public void render(SpriteBatch sb) {
        int t = DemoMod.frameRateRemap(idx, 30, frames.length - 1);
        sb.setColor(1, 1, 1, 1);
        sb.draw(frames[t], Settings.WIDTH * 0.6F,
                AbstractDungeon.player.hb.cY / 2,
                frames[t].getRegionWidth() / 2,
                frames[t].getRegionHeight() / 2,
                frames[t].getRegionWidth(),
                frames[t].getRegionHeight(),
                1.0F, 1.0F, this.rotation);
    }

    @Override
    public void update() {
        if (myDuration == 132) {
            DemoMod.effectsQueue.add(new TimeFreezeEffect(2.0F, false));
            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.XLONG, true);
            DemoSoundMaster.playA("RELIC_BIG_BOY", 0.0F);
        }
        idx++;
        myDuration--;
        if (DemoMod.frameRateRemap(idx, 30, frames.length) >= frames.length) {
            this.isDone = true;
        }
    }

    @Override
    public void dispose() {

    }

    static {
        frames = new TextureRegion[66];
        for (int i=100000;i<100066;i++) {
            String n = Integer.toString(i).substring(1);
            frames[i - 100000] = new TextureRegion(new Texture("DemoImages/effects/bigBoy/big_boy_blast_effect_" + n + ".png"));
        }
    }
}

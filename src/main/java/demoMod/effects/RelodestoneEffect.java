package demoMod.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;

public class RelodestoneEffect extends AbstractGameEffect {
    private static final TextureRegion[] frames;
    private int index;
    private int ctr;

    public RelodestoneEffect() {
        this.index = 119;
        this.ctr = 0;
        this.duration = 0.25F;
    }

    public void start() {
        this.isDone = false;
    }

    public void stop() {
        this.isDone = true;
    }

    @Override
    public void update() {
        ctr++;
        if (DemoMod.frameRateRemap(ctr, 60, index) >= index) {
            ctr = 0;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, 1);
        sb.draw(frames[this.index - DemoMod.frameRateRemap(ctr, 60, index)], AbstractDungeon.player.hb.cX - 96,
                AbstractDungeon.player.hb.cY - 96,
                96,
                96,
                192,
                192,
                1.0F, 1.0F, this.rotation);
    }

    @Override
    public void dispose() {

    }

    static {
        frames = new TextureRegion[120];
        for (int i=100060;i<=100179;i++) {
            String n = Integer.toString(i).substring(1);
            frames[i - 100060] = new TextureRegion(new Texture("DemoImages/effects/relodestone/relodestone_" + n + ".png"));
        }
    }
}

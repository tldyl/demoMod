package demoMod.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;

public class HuntressVictoryEffect extends AbstractGameEffect {
    private static TextureRegion[] frames;
    private int ctr = 0;
    private int idx = 0;

    public HuntressVictoryEffect() {

    }

    @Override
    public void update() {
        idx = DemoMod.frameRateRemap(ctr, 30, 29);
        ctr++;
        if (idx >= frames.length - 1) ctr = 0;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, 1);
        sb.draw(frames[idx], Settings.WIDTH / 2.0F - 1024.0F * Settings.scale / 2.0F, 0.0F, 0.0F, 0.0F, 1024.0F, 1080.0F, Settings.scale, Settings.scale, 0.0F);
    }

    @Override
    public void dispose() {

    }

    public static void init() {
        frames = new TextureRegion[30];
        for (int i=100000;i<100030;i++) {
            String n = Integer.toString(i).substring(1);
            frames[i - 100000] = new TextureRegion(new Texture("DemoImages/effects/victoryScene/victoryScene_" + n + ".png"));
        }
    }
}

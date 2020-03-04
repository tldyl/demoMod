package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;
import demoMod.monsters.ResourcefulRat;
import demoMod.sounds.DemoSoundMaster;

public class RatJumpIntoEntryEffect extends AbstractGameEffect {
    private ResourcefulRat rat;
    private float x;
    private float y;
    private float dst;
    private float vY;
    private boolean sfxPlayed = false;

    public RatJumpIntoEntryEffect(ResourcefulRat rat) {
        this.rat = rat;
        this.duration = 2.0F;
        this.x = rat.drawX;
        this.y = rat.drawY;
        this.dst = Settings.WIDTH * 0.5F - this.x + 150;
        this.vY = 18;

    }

    @Override
    public void update() {
        if (this.duration > 1.0F) {
            this.x += Gdx.graphics.getDeltaTime() * this.dst;
        } else {
            this.x -= Gdx.graphics.getDeltaTime() * 150;
            this.vY -= 0.8F * 60.0F / (float)DemoMod.MAX_FPS;
            this.y += vY;
        }

        if (this.vY < 0.0F && !sfxPlayed) {
            sfxPlayed = true;
            DemoSoundMaster.playA("CHAR_FALLING", 0.0F);
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 0) {
            this.isDone = true;
        }
        rat.movePosition(this.x, this.y);
    }

    @Override
    public void render(SpriteBatch sb) {

    }

    @Override
    public void dispose() {

    }
}

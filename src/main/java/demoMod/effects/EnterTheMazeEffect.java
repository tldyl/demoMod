package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;
import demoMod.patches.TreasureRoomPatch;
import demoMod.sounds.DemoSoundMaster;

public class EnterTheMazeEffect extends AbstractGameEffect {

    private Texture key = new Texture(DemoMod.getResourcePath("relics/gnawedKey.png"));
    private float a = 0.0F;
    private float x;
    private float y;
    private float dst = 224.0F;
    private boolean sfxPlayed = false;

    public EnterTheMazeEffect() {
        this.duration = 1.5F;
        this.x = Settings.WIDTH * 0.5F - 64.0F;
        this.y = Settings.HEIGHT * 0.3F - 64.0F + this.dst;
    }

    @Override
    public void update() {
        if (this.duration > 1.0F) {
            this.a += Gdx.graphics.getDeltaTime() * 2;
            if (this.a > 1) {
                this.a = 1;
            }
        } else if (this.duration > 0.5F) {
            this.y -= Gdx.graphics.getDeltaTime() * 2 * this.dst;
        } else {
            this.a -= Gdx.graphics.getDeltaTime() * 2;
            if (this.a < 0) {
                this.a = 0;
            }
            if (!sfxPlayed) {
                DemoSoundMaster.playA("GUN_RELOAD_MAGNUM", 0.0F);
                sfxPlayed = true;
            }
        }
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 0) {
            this.isDone = true;
            DemoSoundMaster.playA("ENTRY_OPEN", 0.0F);
            TreasureRoomPatch.onEntryOpen();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, this.a);
        sb.draw(this.key, this.x, this.y);
    }

    @Override
    public void dispose() {

    }
}

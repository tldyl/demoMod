package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import demoMod.DemoMod;
import demoMod.utils.Point;

public class DarkMarkerBlastEffect extends AbstractGameEffect {

    private float x;
    private float y;
    private float vX;
    private float vY;
    private Texture img1;
    private Texture img2;
    private Point startPoint = new Point((double)(AbstractDungeon.player.hb.x), (double)(AbstractDungeon.player.hb_y + 525.0F * Settings.scale));

    public DarkMarkerBlastEffect() {
        this.duration = 1.0F;
        this.startingDuration = this.duration;
        this.x = (float)startPoint.x;
        this.y = (float)startPoint.y;
        this.vX = (float)(Settings.WIDTH * 0.5);
        this.vY = Settings.HEIGHT * 0.6F;
        this.img1 = new Texture("DemoImages/effects/darkMarkerRed.png");
        this.img2 = new Texture("DemoImages/effects/darkMarkerBlue.png");
    }

    @Override
    public void update() {
        this.y += this.vY * Gdx.graphics.getDeltaTime();
        this.x += this.vX * Gdx.graphics.getDeltaTime();
        this.vY -= Settings.HEIGHT * 1.2F * Gdx.graphics.getDeltaTime();
        this.duration -= Gdx.graphics.getDeltaTime();
        this.rotation = vY / 20.0F;
        if (this.duration <= 0.0F) {
            DemoMod.effectsQueue.add(new TimeFreezeEffect(1.0F, false));
            DemoMod.effectsQueue.add(new BlankWaveEffect(this.x, this.y, Color.WHITE, ShockWaveEffect.ShockWaveType.CHAOTIC));
            this.isDone = true;
            if (this.img1 != null && this.img2 != null) {
                this.dispose();
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (isDone) return;
        sb.setColor(1.0F,1.0F,1.0F,1.0F);
        sb.draw(new TextureRegion(this.img1), this.x, this.y,
                this.img1.getWidth() / 2.0F,
                this.img1.getHeight() / 2.0F,
                this.img1.getWidth(),
                this.img1.getHeight(),
                1.2F * Settings.scale, 1.2F * Settings.scale, this.rotation);
        sb.draw(new TextureRegion(this.img2), this.x, (float)startPoint.y + 450.0F * Settings.scale - this.y,
                this.img2.getWidth() / 2.0F,
                this.img2.getHeight() / 2.0F,
                this.img2.getWidth(),
                this.img2.getHeight(),
                1.2F * Settings.scale, 1.2F * Settings.scale, -this.rotation);
    }

    @Override
    public void dispose() {
        this.img1.dispose();
        this.img2.dispose();
        this.img1 = null;
        this.img2 = null;
    }
}

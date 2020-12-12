package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.DisplayConfig;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;
import demoMod.sounds.DemoSoundMaster;

@SuppressWarnings("StatementWithEmptyBody")
public class ComboEffect extends AbstractGameEffect {
    public AbstractGameEffect nextEffect;

    private static DisplayConfig displayConf = DisplayConfig.readConfig();
    private int width = displayConf.getWidth();
    private int height = displayConf.getHeight();

    private TextureRegion item1;
    private float x1 = width * 0.5F;
    private float y1 = height * 0.6F;
    private float vY1;
    private float a1 = 0.0F;

    private TextureRegion item2;
    private float x2 = width * 0.5F;
    private float y2 = height * 0.3F;
    private float vY2;
    private float a2 = 0.0F;

    private TextureRegion comboArrow;
    private float arrowX = width * 0.5F;
    private float arrowY = height * 0.4F;
    private float arrowVY;
    private float arrowAlpha = 0.0F;

    private String description;
    private float descX = width * 0.5F;
    private float descY = height * 0.3F;
    private float descAlpha = 0.0F;

    public ComboEffect(Texture item1, Texture item2, String description) {
        this.item1 = new TextureRegion(item1);
        this.item2 = new TextureRegion(item2);
        this.description = description;
        this.comboArrow = new TextureRegion(new Texture("DemoImages/effects/comboArrow.png"));
        this.duration = 6.0F;
        this.startingDuration = this.duration;
        this.renderBehind = false;
        DemoSoundMaster.playA("COMBO_ACTIVATED", 0.0F);
    }

    @Override
    public void update() {
        if (this.duration == this.startingDuration) {
            vY1 = (height * 4F * Gdx.graphics.getDeltaTime()) / 15;
            vY2 = height * 0.6F * Gdx.graphics.getDeltaTime();
            arrowVY = (height * 4F * Gdx.graphics.getDeltaTime()) / 15;
        }
        if (this.duration > 4.5) {
            arrowY += arrowVY;
            arrowVY -= ((height * 4F * Gdx.graphics.getDeltaTime()) / 15) * (Gdx.graphics.getDeltaTime() / 1.5);
            arrowAlpha += 0.03F;
            if (arrowAlpha > 1.0F) arrowAlpha = 1.0F;
            if (arrowVY < 0) arrowVY = 0;
        } else if (this.duration > 4) {
            arrowAlpha -= 0.04F;
            if (arrowAlpha < 0) arrowAlpha = 0.0F;
        } else if (this.duration > 3.5) {
            a1 += 0.06F;
            if (a1 > 1) a1 = 1.0F;
        }  else if (this.duration > 2.5) {
            y1 += vY1;
            vY1 -= ((height * 4F) * Gdx.graphics.getDeltaTime() / 15) * Gdx.graphics.getDeltaTime();
            y2 += vY2;
            vY2 -= (height * 0.6F) * Gdx.graphics.getDeltaTime() * Gdx.graphics.getDeltaTime();
            a2 += 0.015F;
            if (a2 > 1) a2 = 1.0F;
            descAlpha += 0.015F;
            if (descAlpha > 1) descAlpha = 1.0F;
            if (vY1 < 0) vY1 = 0;
            if (vY2 < 0) vY2 = 0;
        } else if (this.duration > 1.5) {

        } else {
            a1 -= 0.015F;
            if (a1 < 0) a1 = 0.0F;
            a2 -= 0.015F;
            if (a2 < 0) a2 = 0.0F;
            descAlpha -= 0.015F;
            if (descAlpha < 0) descAlpha = 0.0F;
        }
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
            if (nextEffect != null) {
                DemoMod.effectsQueue.add(nextEffect);
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1.0F,1.0F,1.0F,arrowAlpha);
        sb.draw(this.comboArrow, this.arrowX - this.comboArrow.getTexture().getWidth() / 2,
                this.arrowY - this.comboArrow.getTexture().getHeight() / 2,
                this.comboArrow.getTexture().getWidth() / 2,
                this.comboArrow.getTexture().getHeight() / 2,
                this.comboArrow.getRegionWidth(),
                this.comboArrow.getRegionHeight(),
                1.0F, 1.0F, this.rotation);
        sb.setColor(1.0F,1.0F,1.0F, a1);
        sb.draw(this.item1, this.x1 - this.item1.getTexture().getWidth() / 2,
                this.y1 - this.item1.getTexture().getHeight() / 2,
                this.item1.getTexture().getWidth() / 2,
                this.item1.getTexture().getHeight() / 2,
                this.item1.getRegionWidth(),
                this.item1.getRegionHeight(),
                1.0F, 1.0F, this.rotation);
        sb.setColor(1.0F,1.0F,1.0F, a2);
        sb.draw(this.item2, this.x2 - this.item2.getTexture().getWidth() / 2,
                this.y2 - this.item2.getTexture().getHeight() / 2,
                this.item2.getTexture().getWidth() / 2,
                this.item2.getTexture().getHeight() / 2,
                this.item2.getRegionWidth(),
                this.item2.getRegionHeight(),
                1.0F, 1.0F, this.rotation);
        FontHelper.renderFontCentered(sb, FontHelper.losePowerFont, this.description, this.descX, this.descY, new Color(1.0F, 1.0F, 1.0F, descAlpha));
    }

    @Override
    public void dispose() {

    }
}

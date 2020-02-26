package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.scene.SilentVictoryStarEffect;
import demoMod.utils.Point;

public class ChaffGrenadeEffect extends AbstractGameEffect {
    private float x;
    private float y;
    private float vX;
    private float vY;
    private Texture img;

    public ChaffGrenadeEffect() {
        this.duration = 0.5F;
        this.startingDuration = this.duration;
        this.img = new Texture("DemoImages/effects/chaffGrenade.png");
        Point startPoint = new Point((double)(AbstractDungeon.player.hb.x), (double)(AbstractDungeon.player.hb_y + 220.0F));
        this.x = (float)startPoint.x;
        this.y = (float)startPoint.y;
        this.vX = (float)(Settings.WIDTH * 1.0);
        this.vY = 1000.0F;
        this.renderBehind = false;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1.0F,1.0F,1.0F,1.0F);
        sb.draw(new TextureRegion(this.img), this.x, this.y,
                this.img.getWidth() / 2.0F,
                this.img.getHeight() / 2.0F,
                this.img.getWidth(),
                this.img.getHeight(),
                2.0F, 2.0F, this.rotation);
    }

    @Override
    public void update() {
        this.y += this.vY * Gdx.graphics.getDeltaTime();
        this.x += this.vX * Gdx.graphics.getDeltaTime();
        this.vY -= 4000 * Gdx.graphics.getDeltaTime();
        this.duration -= Gdx.graphics.getDeltaTime();
        this.rotation += 1.5F;
        if (this.duration < 0.0F) {
            for (int i=0;i<100;i++) {
                AbstractGameEffect effect = new SilentVictoryStarEffect();
                effect.duration = 6.0F;
                effect.renderBehind = false;
                AbstractDungeon.effectsQueue.add(effect);
            }
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, AbstractDungeon.player, new StunMonsterPower(m, 1)));
            }
            this.isDone = true;
        }
    }

    @Override
    public void dispose() {

    }
}

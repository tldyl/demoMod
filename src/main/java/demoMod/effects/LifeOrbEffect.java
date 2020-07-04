package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;
import demoMod.monsters.Decoy;

public class LifeOrbEffect extends AbstractGameEffect {

    private AbstractPlayer source;
    private AbstractMonster target;
    private Texture img;
    private AtlasRegion frame;
    private int frameWidth = 0;
    private float x;
    private float y;
    private float scaleX;
    private float scaleY;
    private float scaleYControl;
    public boolean beginToTakeDamage = false;
    private static final int imgWidth = 584;
    private static final int imgHeight = 55;

    public LifeOrbEffect(AbstractMonster target) {
        this.source = AbstractDungeon.player;
        this.target = target;
        if (target == null) {
            this.target = AbstractDungeon.getRandomMonster(AbstractDungeon.getCurrRoom().monsters.getMonster(Decoy.ID));
        }
        this.duration = 2.0F;
        this.img = new Texture(DemoMod.getResourcePath("effects/lifeOrb.png"));
        this.x = target.hb.cX;
        this.y = target.hb.cY;
        this.scaleX = (this.x - source.hb.cX) / imgWidth;
        this.scaleY = 1.0F;
        this.scaleYControl = 1.0F;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        frameWidth += imgWidth * Gdx.graphics.getDeltaTime() / 0.4F;
        if (frameWidth > imgWidth) frameWidth = imgWidth;
        this.frame = new AtlasRegion(this.img, imgWidth - frameWidth, 0, frameWidth, imgHeight);
        this.x -= imgWidth * Gdx.graphics.getDeltaTime() / 0.4F;
        if (this.x < source.hb.cX) this.x = source.hb.cX;
        this.rotation = MathUtils.atan2(target.hb.cY - source.hb.cY , target.hb.cX - source.hb.cX);
        this.scaleY = (MathUtils.sinDeg((2 - this.duration) * 540) + 1.5F) / 2.0F;
        if (this.duration <= 1.6F) {
            this.beginToTakeDamage = true;
        }

        if (this.duration <= 0.4F) {
            this.scaleYControl -= Gdx.graphics.getDeltaTime() / 0.4F;
        }

        if (this.duration <= 0) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, 1);
        sb.draw(this.frame, this.x, this.y,
                this.frameWidth / 2, 27.5F,
                frameWidth, imgHeight,
                this.scaleX, this.scaleY * this.scaleYControl,
                this.rotation);
    }

    @Override
    public void dispose() {

    }
}

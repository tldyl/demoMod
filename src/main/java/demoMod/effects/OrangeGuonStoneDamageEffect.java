package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;

public class OrangeGuonStoneDamageEffect extends AbstractGameEffect {
    private float x;
    private float y;
    private float offsetX;
    private float offsetY;
    private int frame;
    private static final TextureRegion img[];

    public OrangeGuonStoneDamageEffect(float mX, float mY) {
        AbstractPlayer player = AbstractDungeon.player;
        if (!player.hasRelic(DemoMod.makeID("OrangeGuonStone"))) {
            this.isDone = true;
        } else {
            AbstractRelic orangeGuonStone = player.getRelic(DemoMod.makeID("OrangeGuonStone"));
            this.x = orangeGuonStone.hb.cX;
            this.y = orangeGuonStone.hb.cY;
            this.offsetX = mX - this.x;
            this.offsetY = mY - this.y;
            this.duration = 0.5F;
            this.startingDuration = this.duration;
            this.frame = MathUtils.random(89);
        }
    }

    @Override
    public void update() {
        this.x += this.offsetX * Gdx.graphics.getDeltaTime() / this.startingDuration;
        this.y += this.offsetY * Gdx.graphics.getDeltaTime() / this.startingDuration;
        this.frame = (this.frame + 2) % img.length;
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 0) this.isDone = true;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, 1);
        sb.draw(img[frame], this.x,
                this.y,
                64,
                64,
                128,
                128,
                1.0F, 1.0F, this.rotation);
    }

    @Override
    public void dispose() {

    }

    static {
        img = new TextureRegion[90];
        for (int i=100000;i<100090;i++) {
            String n = Integer.toString(i).substring(1);
            img[i - 100000] = new TextureRegion(new Texture(DemoMod.getResourcePath("effects/orange/orange_" + n + ".png")));
        }
    }
}

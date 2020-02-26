package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;
import demoMod.sounds.DemoSoundMaster;

public class ResourcefulRatPhaseTwoIntro extends AbstractGameEffect {

    private AbstractPlayer p = AbstractDungeon.player;
    private float x;
    private float y;
    private boolean playSFX = false;
    private TextureRegion img;
    private float a;
    private static Texture[] rat_phase_2_intro_effect;
    private static Texture[] rat_phase_2_intro_animation;
    private int idx_rat_phase_2_intro_effect = 0;
    private AbstractMonster rat;

    public ResourcefulRatPhaseTwoIntro(AbstractMonster rat) {
        this.duration = 12.7F;
        this.x = Settings.WIDTH / 2.0F;
        this.y = Settings.HEIGHT;
        this.img = new TextureRegion(new Texture(DemoMod.getResourcePath("effects/resourcefulRat/rat_phase_2_intro_mask.png")));
        this.a = 1.0F;
        this.rat = rat;
        this.renderBehind = true;
    }

    @Override
    public void update() {
        float v = Settings.HEIGHT * 0.45F * Gdx.graphics.getDeltaTime();
        if (this.duration == 12.7F) {
            rat.drawX = Settings.WIDTH / 2.0F;
            rat.drawY = Settings.HEIGHT * 0.45F;
            rat.hb.move(rat.drawX + rat.hb_x + rat.animX, rat.drawY + rat.hb_y + rat.hb_h / 2.0F);
            rat.healthHb.move(rat.hb.cX, rat.hb.cY - rat.hb_h / 2.0F - rat.healthHb.height / 2.0F);
            p.movePosition(x, y);
            DemoSoundMaster.playA("CHAR_FALLING", 0.0F);
        }
        if (this.duration >= 11.2F) {
            p.movePosition(x, y);
            y -= v;
        } else if (this.duration <= 10.2F) {
            if (!playSFX) {
                playSFX = true;
                DemoSoundMaster.playA("BOSS_RESOURCEFUL_RAT_APPEAR", 0.0F);
            }
        }
        if (this.duration <= 1.8F) {
            this.a -= Gdx.graphics.getDeltaTime() / 1.8F;
            if (this.a < 0) {
                this.a = 0;
            }
        }
        if (playSFX) idx_rat_phase_2_intro_effect++;
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 0) {
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMBAT;
            AbstractDungeon.getCurrRoom().monsters.init();
            AbstractRoom.waitTimer = 0.1F;
            AbstractDungeon.player.preBattlePrep();
            AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_RESOURCEFUL_RAT_2");
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, this.a);
        sb.draw(this.img, 0,
                0,
                0,
                0,
                Settings.WIDTH,
                Settings.HEIGHT,
                1.0F, 1.0F, this.rotation
        );

        sb.setColor(1, 1, 1, 1);
        sb.draw(new TextureRegion(rat_phase_2_intro_animation[DemoMod.frameRateRemap(idx_rat_phase_2_intro_effect, 30, rat_phase_2_intro_effect.length - 1)]),
                rat.drawX - (float)503 * Settings.scale / 2.0F + rat.animX - 63.0F * Settings.scale * 1.3F,
                rat.drawY + rat.animY - 98.0F * Settings.scale * 1.3F,
                0,
                0,
                512,
                512,
                Settings.scale * 1.3F, Settings.scale * 1.3F, this.rotation
        );

        if (playSFX) {
            sb.setColor(1, 1, 1, this.a);
            sb.draw(new TextureRegion(rat_phase_2_intro_effect[DemoMod.frameRateRemap(idx_rat_phase_2_intro_effect, 30, rat_phase_2_intro_effect.length - 1)]),
                    rat.drawX - (float)503 * Settings.scale / 2.0F + rat.animX - 63.0F * Settings.scale * 1.3F,
                    rat.drawY + rat.animY - 98.0F * Settings.scale * 1.3F,
                    0,
                    0,
                    512,
                    512,
                    Settings.scale * 1.3F, Settings.scale * 1.3F, this.rotation
            );
        }
    }

    @Override
    public void dispose() {

    }

    public static void init() {
        rat_phase_2_intro_effect = new Texture[286];
        rat_phase_2_intro_animation = new Texture[286];

        for (int i=100000;i<100286;i++) {
            String n = Integer.toString(i).substring(1);
            rat_phase_2_intro_effect[i - 100000] = new Texture("DemoImages/effects/resourcefulRat/phase_2_intro/phase_2_intro_" + n + ".png");
            rat_phase_2_intro_animation[i - 100000] = new Texture("DemoImages/monsters/resourcefulRat/phase_2_intro/phase_2_intro_" + n + ".png");
        }
    }
}

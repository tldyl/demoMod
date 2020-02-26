package demoMod.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import com.megacrit.cardcrawl.vfx.scene.CeilingDustEffect;
import com.megacrit.cardcrawl.vfx.scene.LogoFlameEffect;
import demoMod.DemoMod;
import demoMod.effects.MazeFogEffect;
import demoMod.monsters.ResourcefulRat;

public class MazeScene extends AbstractScene {
    private TextureAtlas.AtlasRegion bg;
    private TextureAtlas.AtlasRegion rat_1_bg;
    private TextureAtlas.AtlasRegion rat_2_bg_1;
    private TextureAtlas.AtlasRegion rat_2_bg_2;
    private Texture fog1;
    private Texture fog2;
    private Texture fog3;

    private float flameTimer = 0.0F;
    private float ceilingDustTimer = 1.0F;
    private float fogTimer = 3.0F;

    public MazeScene() {
        super("endingScene/scene.atlas");
        this.ambianceName = "AMBIANCE_BEYOND";
        this.bg = new TextureAtlas.AtlasRegion(new Texture(DemoMod.getResourcePath("mazeScene/bg.png")), 0, 0, 1920, 1136);
        this.rat_1_bg = this.bg;
        this.rat_2_bg_1 = new TextureAtlas.AtlasRegion(new Texture(DemoMod.getResourcePath("mazeScene/rat_2_bg_1.png")), 0, 0, 1472, 832);
        this.rat_2_bg_2 = new TextureAtlas.AtlasRegion(new Texture(DemoMod.getResourcePath("mazeScene/rat_2_bg_2.png")), 0, 0, 1472, 832);
        this.fog1 = new Texture(DemoMod.getResourcePath("mazeScene/bg_fog_1.png"));
        this.fog2 = new Texture(DemoMod.getResourcePath("mazeScene/bg_fog_2.png"));
        this.fog3 = new Texture(DemoMod.getResourcePath("mazeScene/bg_fog_3.png"));
    }

    public void update() {
        super.update();
        if (Settings.DISABLE_EFFECTS) {
            return;
        }
        if (!(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss)) {
            this.ceilingDustTimer -= Gdx.graphics.getDeltaTime();
            this.fogTimer -= Gdx.graphics.getDeltaTime();
            if (this.ceilingDustTimer < 0.0F) {
                int roll = MathUtils.random(4);
                if (roll == 0) {
                    AbstractDungeon.effectsQueue.add(new CeilingDustEffect());
                    playDustSfx(false);
                } else if (roll == 1) {
                    AbstractDungeon.effectsQueue.add(new CeilingDustEffect());
                    AbstractDungeon.effectsQueue.add(new CeilingDustEffect());
                    playDustSfx(false);
                } else {
                    AbstractDungeon.effectsQueue.add(new CeilingDustEffect());
                    AbstractDungeon.effectsQueue.add(new CeilingDustEffect());
                    AbstractDungeon.effectsQueue.add(new CeilingDustEffect());
                    if (!Settings.isBackgrounded) {
                        playDustSfx(true);
                    }
                }
                this.ceilingDustTimer = MathUtils.random(0.5F, 20.0F);
            }
            if (this.fogTimer < 0.0F) {
                int roll = MathUtils.random(2);
                if (roll == 0) {
                    AbstractDungeon.effectsQueue.add(new MazeFogEffect(fog1));
                } else if (roll == 1) {
                    AbstractDungeon.effectsQueue.add(new MazeFogEffect(fog2));
                } else {
                    AbstractDungeon.effectsQueue.add(new MazeFogEffect(fog3));
                    CardCrawlGame.sound.playAV("POWER_INTANGIBLE", -0.2F, 0.6F);
                }
                this.fogTimer = MathUtils.random(9.0F, 20.0F);
            }
        }
    }

    private void playDustSfx(boolean boom) {
        if (boom) {
            int roll = MathUtils.random(2);
            if (roll == 0) {
                CardCrawlGame.sound.play("CEILING_BOOM_1", 0.2F);
            } else if (roll == 1) {
                CardCrawlGame.sound.play("CEILING_BOOM_2", 0.2F);
            } else {
                CardCrawlGame.sound.play("CEILING_BOOM_3", 0.2F);
            }
        } else {
            int roll = MathUtils.random(2);
            if (roll == 0) {
                CardCrawlGame.sound.play("CEILING_DUST_1", 0.2F);
            } else if (roll == 1) {
                CardCrawlGame.sound.play("CEILING_DUST_2", 0.2F);
            } else {
                CardCrawlGame.sound.play("CEILING_DUST_3", 0.2F);
            }
        }
    }

    @Override
    public void renderCombatRoomBg(SpriteBatch sb) {
        sb.setColor(1, 1, 1, 1);
        this.renderAtlasRegionIf(sb, this.bg, !(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss));
        this.renderAtlasRegionIf(sb, this.rat_1_bg, AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss && !ResourcefulRat.phaseTwo);
        if (ResourcefulRat.phaseTwo && !ResourcefulRat.isTrueBeaten) {
            sb.draw(this.rat_2_bg_1, 0,
                    0,
                    0,
                    0,
                    Settings.WIDTH,
                    Settings.HEIGHT,
                    1.0F, 1.0F, 0);
        }
        if (ResourcefulRat.phaseTwo && ResourcefulRat.isTrueBeaten) {
            sb.draw(this.rat_2_bg_2, 0,
                    0,
                    0,
                    0,
                    Settings.WIDTH,
                    Settings.HEIGHT,
                    1.0F, 1.0F, 0);
        }
        if (!(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss)) {
            flameTimer += Gdx.graphics.getDeltaTime();
            if (flameTimer >= 0.05F) {
                LogoFlameEffect effect = new LogoFlameEffect() {
                    @Override
                    public void render(SpriteBatch sb) {
                        color.r = 1;
                        color.g = 0.7F;
                        color.b = 0;
                        render(sb, Settings.WIDTH * 0.83F * Settings.scale, Settings.HEIGHT * 0.76F * Settings.scale);
                    }
                };
                effect.update();
                AbstractDungeon.effectsQueue.add(effect);
                flameTimer = 0.0F;
            }
        }
    }

    @Override
    public void renderCombatRoomFg(SpriteBatch sb) {

    }

    @Override
    public void renderCampfireRoom(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        renderAtlasRegionIf(sb, this.campfireKindling, this.campfireKindling != null);
        renderAtlasRegionIf(sb, this.campfireBg, true);
    }

    @Override
    public void randomizeScene() {

    }
}

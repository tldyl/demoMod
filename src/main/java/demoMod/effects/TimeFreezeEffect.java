package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;
import demoMod.dungeons.Maze;
import demoMod.monsters.ResourcefulRat;

@SuppressWarnings("WeakerAccess")
public class TimeFreezeEffect extends AbstractGameEffect {

    private TextureRegion img;
    private float a = 1.0F;
    private boolean mute = false;

    public TimeFreezeEffect() {
        this(1.0F, true);
    }

    public TimeFreezeEffect(float duration, boolean mute) {
        this.duration = duration;
        this.startingDuration = duration;
        this.mute = mute;
        Pixmap bg = new Pixmap(Settings.SAVED_WIDTH, Settings.SAVED_HEIGHT, Pixmap.Format.RGBA8888);
        bg.setColor(1, 1, 1, 1);
        bg.fill();
        this.img = new TextureRegion(new Texture(bg));
        if (mute) {
            if (!(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss)) {
                CardCrawlGame.music.silenceBGMInstantly();
            }
            CardCrawlGame.music.silenceTempBgmInstantly();
        }
    }

    public void update() {
        this.a -= Gdx.graphics.getDeltaTime() / this.startingDuration;
        if (a < 0) a = 0;
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 0) {
            if (this.mute) {
                if (!(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss)) {
                    if (!(AbstractDungeon.getCurrRoom() instanceof ShopRoom)) {
                        CardCrawlGame.music.unsilenceBGM();
                    } else {
                        CardCrawlGame.music.playTempBGM("GUNGEON_SHOP");
                    }
                } else {
                    switch (AbstractDungeon.actNum) {
                        case 1:
                            CardCrawlGame.music.playTempBGM("BOSS_BOTTOM");
                            break;
                        case 2:
                            if(AbstractDungeon.id.equals(Maze.ID)) {
                                if (!ResourcefulRat.phaseTwo) {
                                    CardCrawlGame.music.playTempBGM("BOSS_RESOURCEFUL_RAT_1");
                                } else {
                                    CardCrawlGame.music.playTempBGM("BOSS_RESOURCEFUL_RAT_2");
                                }
                            } else {
                                CardCrawlGame.music.playTempBGM("BOSS_CITY");
                            }
                            break;
                        case 3:
                            CardCrawlGame.music.playTempBGM("BOSS_BEYOND");
                            break;
                        case 5:
                            if(AbstractDungeon.id.equals("DemoExt:Forge")) {
                                CardCrawlGame.music.playTempBGM("BOSS_DRAGUN_1");
                            } else {
                                CardCrawlGame.music.playTempBGM("BOSS_ENDING");
                            }
                            break;
                        default:
                            CardCrawlGame.music.playTempBGM("BOSS_ENDING");
                            break;
                    }
                }
            }
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (DemoMod.disableVFXForCamera) {
            return;
        }
        sb.setColor(1, 1, 1, this.a * 0.7F);
        sb.draw(this.img, 0,
                0,
                0,
                0,
                this.img.getRegionWidth(),
                this.img.getRegionHeight(),
                1.0F, 1.0F, this.rotation);
    }

    @Override
    public void dispose() {
        this.img.getTexture().dispose();
    }
}

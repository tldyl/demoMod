package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;
import demoMod.dungeons.Maze;
import demoMod.monsters.ResourcefulRat;
import demoMod.sounds.DemoSoundMaster;

import java.lang.reflect.Field;

public class PlayerJumpIntoEntryEffect extends AbstractGameEffect {

    private AbstractPlayer p;
    private float x;
    private float y;
    private float dst;
    private float vY;
    private boolean sfxPlayed = false;
    private boolean enterTheMaze;

    public PlayerJumpIntoEntryEffect(boolean enterTheMaze) {
        this.p = AbstractDungeon.player;
        this.duration = 2.0F;
        this.x = p.drawX;
        this.y = p.drawY;
        this.dst = Settings.WIDTH * 0.5F - this.x - 150;
        this.vY = 18;
        this.enterTheMaze = enterTheMaze;
    }

    @Override
    public void update() {
        if (this.duration > 1.0F) {
            this.x += Gdx.graphics.getDeltaTime() * this.dst;
        } else {
            this.x += Gdx.graphics.getDeltaTime() * 150;
            this.vY -= 48.0F * Gdx.graphics.getDeltaTime();
            this.y += vY;
        }

        if (this.vY < 0.0F && !sfxPlayed) {
            sfxPlayed = true;
            DemoSoundMaster.playA("CHAR_FALLING", 0.0F);
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 0) {
            this.isDone = true;
            if (enterTheMaze) {
                CardCrawlGame.nextDungeon = Maze.ID;
                AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
                AbstractDungeon.fadeOut();
                AbstractDungeon.isDungeonBeaten = true;
            } else {
                AbstractDungeon.bossKey = ResourcefulRat.ID;
                MapRoomNode node = new MapRoomNode(-1, 15);
                node.room = new MonsterRoomBoss();
                AbstractDungeon.nextRoom = node;
                AbstractDungeon.closeCurrentScreen();
                AbstractDungeon.nextRoomTransitionStart();
                try {
                    Field field = AbstractDungeon.class.getDeclaredField("fadeTimer");
                    field.setAccessible(true);
                    field.set(null, 0.8F);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        p.movePosition(this.x, this.y);
    }

    @Override
    public void render(SpriteBatch sb) {

    }

    @Override
    public void dispose() {

    }
}

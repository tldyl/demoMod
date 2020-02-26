package demoMod.rooms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rewards.chests.LargeChest;
import com.megacrit.cardcrawl.rewards.chests.MediumChest;
import com.megacrit.cardcrawl.rewards.chests.SmallChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;
import demoMod.DemoMod;
import demoMod.characters.HuntressCharacter;
import demoMod.monsters.LordOfTheJammed;
import demoMod.monsters.Mimic;

public class MimicRoom extends AbstractRoom {
    private FakeChest fakeChest;

    public MimicRoom(boolean isTreasureRoom) {
        this.phase = RoomPhase.INCOMPLETE;
        if (isTreasureRoom) {
            this.mapImg = ImageMaster.MAP_NODE_TREASURE;
            this.mapImgOutline = ImageMaster.MAP_NODE_TREASURE_OUTLINE;
            this.mapSymbol = "T";
        } else {
            this.mapImg = ImageMaster.MAP_NODE_EVENT;
            this.mapImgOutline = ImageMaster.MAP_NODE_EVENT_OUTLINE;
            this.mapSymbol = "?";
        }
    }

    @Override
    public void onPlayerEntry() {
        this.playBGM(null);
        this.fakeChest = new FakeChest(getRandomMimicType());
        AbstractDungeon.overlayMenu.proceedButton.show();
    }

    public static Mimic.MimicType getRandomMimicType() {
        if (AbstractDungeon.bossCount < 1) {
            return Mimic.MimicType.SMALL;
        } else if (AbstractDungeon.bossCount == 1) {
            return Mimic.MimicType.MEDIUM;
        } else if (AbstractDungeon.bossCount == 2) {
            return Mimic.MimicType.LARGE;
        } else {
            switch(AbstractDungeon.miscRng.random(2)) {
                case 0:
                    return Mimic.MimicType.SMALL;
                case 1:
                    return Mimic.MimicType.MEDIUM;
                default:
                    return Mimic.MimicType.LARGE;
            }
        }
    }

    public void update() {
        super.update();
        if (this.fakeChest != null && this.phase == RoomPhase.INCOMPLETE && !AbstractDungeon.loading_post_combat) {
            this.fakeChest.update();
            if (this.fakeChest.hb.hovered && InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed()) {
                AbstractDungeon.overlayMenu.proceedButton.hide();
                if (this.monsters == null) {
                    this.monsters = CardCrawlGame.dungeon.getMonsterForRoomCreation();
                }

                this.monsters.monsters.clear();
                this.monsters.add(new Mimic(this.fakeChest.type));
                if (HuntressCharacter.curse >= 10)
                    this.monsters.add(new LordOfTheJammed(- 430.0F, this.monsters.monsters.get(0).animY));
                this.monsters.init();
                this.addGoldToRewards(AbstractDungeon.eventRng.random(30, 40));
                this.addRelicToRewards(AbstractDungeon.returnRandomRelic(DemoMod.getRelicTierFromMimicType(this.fakeChest.type)));
                if (!Settings.hasSapphireKey) {
                    this.addSapphireKey(this.rewards.get(this.rewards.size() - 1));
                }

                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    relic.onChestOpen(false);
                }

                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    relic.onChestOpenAfter(false);
                }
                this.enterCombat();
            }
        }
    }

    private void enterCombat() {
        AbstractDungeon.getCurrRoom().phase = RoomPhase.COMBAT;
        AbstractDungeon.getCurrRoom().monsters.init();
        AbstractRoom.waitTimer = 0.1F;
        AbstractDungeon.player.preBattlePrep();
        AbstractDungeon.effectsQueue.add(new SmokeBombEffect(this.fakeChest.hb.cX, this.fakeChest.hb.y));
        CardCrawlGame.sound.play("INTIMIDATE");
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.fakeChest != null && this.phase == RoomPhase.INCOMPLETE && !AbstractDungeon.loading_post_combat) {
            this.fakeChest.render(sb);
            this.fakeChest.hb.render(sb);
        }
    }

    public static class FakeChest {
        Hitbox hb;
        Texture img;
        Mimic.MimicType type;

        FakeChest(Mimic.MimicType type) {
            this.type = type;
            switch(type) {
                case SMALL:
                    (this.hb = new Hitbox(256.0F * Settings.scale, 200.0F * Settings.scale)).move(SmallChest.CHEST_LOC_X, SmallChest.CHEST_LOC_Y - 150.0F * Settings.scale);
                    this.img = ImageMaster.loadImage("images/npcs/smallChest.png");
                    break;
                case MEDIUM:
                    (this.hb = new Hitbox(256.0F * Settings.scale, 270.0F * Settings.scale)).move(MediumChest.CHEST_LOC_X, MediumChest.CHEST_LOC_Y - 90.0F * Settings.scale);
                    this.img = ImageMaster.loadImage("images/npcs/mediumChest.png");
                    break;
                case LARGE:
                    (this.hb = new Hitbox(340.0F * Settings.scale, 200.0F * Settings.scale)).move(LargeChest.CHEST_LOC_X, LargeChest.CHEST_LOC_Y - 120.0F * Settings.scale);
                    this.img = ImageMaster.loadImage("images/npcs/largeChest.png");
            }

        }

        public void update() {
            this.hb.update();
        }

        public void render(SpriteBatch sb) {
            sb.setColor(Color.WHITE);
            sb.draw(this.img, AbstractChest.CHEST_LOC_X - 256.0F, AbstractDungeon.floorY - 128.0F, 256.0F, 256.0F, 512.0F, 512.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 512, false, false);
        }
    }
}

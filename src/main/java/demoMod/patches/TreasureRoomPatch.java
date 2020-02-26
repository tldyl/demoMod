package demoMod.patches;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rewards.chests.LargeChest;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import demoMod.DemoMod;
import demoMod.dungeons.Maze;
import demoMod.effects.EnterTheMazeEffect;
import demoMod.effects.PlayerJumpIntoEntryEffect;
import demoMod.relics.PartiallyEatenCheese;
import demoMod.relics.RatBoots;
import demoMod.relics.ResourcefulSack;

@SuppressWarnings("unused")
public class TreasureRoomPatch {
    private static Texture entry;
    public static Hitbox hb;

    private static boolean isOpen = false;

    static {
        entry = new Texture(DemoMod.getResourcePath("effects/mazeEntry.png"));
        hb = new Hitbox(300, 100);
        hb.move(Settings.WIDTH * 0.5F, Settings.HEIGHT * 0.3F);
    }

    public static void onEntryOpen() {
        entry = new Texture(DemoMod.getResourcePath("effects/mazeEntry_opened.png"));
        isOpen = true;
    }

    public static void closeEntry() {
        entry = new Texture(DemoMod.getResourcePath("effects/mazeEntry.png"));
        isOpen = false;
    }

    @SpirePatch(
            clz = TreasureRoom.class,
            method = "render"
    )
    public static class PatchRender {
        public static void Postfix(TreasureRoom room, SpriteBatch sb) {
            if (AbstractDungeon.actNum == 2 && AbstractDungeon.getCurrRoom().getMapSymbol().equals("T")) {
                render(sb);
            }
        }

        private static void render(SpriteBatch sb) {
            sb.draw(entry, Settings.WIDTH * 0.5F - 150, Settings.HEIGHT * 0.3F - 50);
        }
    }

    @SpirePatch(
            clz = TreasureRoom.class,
            method = "update"
    )
    public static class PatchUpdate {
        public static void Postfix(TreasureRoom room) {
            if (AbstractDungeon.actNum == 2 && AbstractDungeon.getCurrRoom().getMapSymbol().equals("T")) {
                hb.update();
                if (hb.hovered && (InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed()) && !AbstractDungeon.isScreenUp) {
                    InputHelper.justClickedLeft = false;
                    if (AbstractDungeon.player.hasRelic(DemoMod.makeID("GnawedKey")) && !isOpen) {
                        AbstractDungeon.player.loseRelic(DemoMod.makeID("GnawedKey"));
                        AbstractDungeon.effectsQueue.add(new EnterTheMazeEffect());
                    }
                    if (isOpen) {
                        AbstractDungeon.effectsQueue.add(new PlayerJumpIntoEntryEffect(true));
                        isOpen = false;
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz = TreasureRoomBoss.class,
            method = "onPlayerEntry"
    )
    public static class PatchOnPlayerEntry {
        public static SpireReturn Prefix(TreasureRoomBoss room) {
            if (AbstractDungeon.id.equals(Maze.ID)) {
                CardCrawlGame.music.silenceBGM();
                if (AbstractDungeon.actNum < 4 || !AbstractPlayer.customMods.contains("Blight Chests")) {
                    AbstractDungeon.overlayMenu.proceedButton.setLabel(TreasureRoomBoss.TEXT[0]);
                }
                room.chest = new LargeChest();
                AbstractPlayer p = AbstractDungeon.player;
                if (!p.hasRelic(RatBoots.ID)) {
                    room.addRelicToRewards(new RatBoots());
                } else {
                    double ran = AbstractDungeon.treasureRng.random(1.0F) * AbstractDungeon.rareRelicPool.size();
                    room.addRelicToRewards(RelicLibrary.getRelic(AbstractDungeon.uncommonRelicPool.get((int)Math.floor(ran))));
                }
                if (!p.hasRelic(ResourcefulSack.ID)) {
                    room.addRelicToRewards(new ResourcefulSack());
                } else {
                    double ran = AbstractDungeon.treasureRng.random(1.0F) * AbstractDungeon.rareRelicPool.size();
                    room.addRelicToRewards(RelicLibrary.getRelic(AbstractDungeon.uncommonRelicPool.get((int)Math.floor(ran))));
                }
                if (!p.hasRelic(PartiallyEatenCheese.ID)) {
                    room.addRelicToRewards(new PartiallyEatenCheese());
                } else {
                    double ran = AbstractDungeon.treasureRng.random(1.0F) * AbstractDungeon.rareRelicPool.size();
                    room.addRelicToRewards(RelicLibrary.getRelic(AbstractDungeon.uncommonRelicPool.get((int)Math.floor(ran))));
                }
                return SpireReturn.Return(null);
            } else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(
            clz = AbstractChest.class,
            method = "open"
    )
    public static class PatchOpenChest {
        public static SpireReturn Prefix(AbstractChest chest, boolean bossChest) {
            if (AbstractDungeon.id.equals(Maze.ID)) {
                AbstractDungeon.overlayMenu.proceedButton.setLabel(AbstractChest.TEXT[0]);
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    r.onChestOpen(bossChest);
                }
                CardCrawlGame.sound.play("CHEST_OPEN");
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    r.onChestOpenAfter(bossChest);
                }
                AbstractDungeon.combatRewardScreen.open();
                return SpireReturn.Return(null);
            } else {
                return SpireReturn.Continue();
            }
        }
    }
}

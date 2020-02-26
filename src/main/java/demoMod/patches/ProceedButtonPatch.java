package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import demoMod.dungeons.Maze;
import demoMod.monsters.ResourcefulRat;

@SuppressWarnings("unused")
public class ProceedButtonPatch {
    @SpirePatch(
            clz = ProceedButton.class,
            method = "update"
    )
    public static class PatchUpdate {

        @SpireInsertPatch(rloc = 26)
        public static SpireReturn Insert(ProceedButton button) {
            if (AbstractDungeon.id.equals(Maze.ID) && ResourcefulRat.isBeaten && !ResourcefulRat.isTrueBeaten) {
                gotoRatPhaseTwo(button);
            } else if (AbstractDungeon.id.equals(Maze.ID) && ResourcefulRat.isTrueBeaten) {
                gotoRatTreasureRoom(button);
            }
            return SpireReturn.Continue();
        }

        private static void gotoRatPhaseTwo(ProceedButton button) {
            AbstractDungeon.bossKey = AbstractDungeon.bossList.get(0);
            MapRoomNode node = new MapRoomNode(-1, 15);
            node.room = new MonsterRoomBoss();
            AbstractDungeon.nextRoom = node;
            AbstractDungeon.closeCurrentScreen();
            AbstractDungeon.nextRoomTransitionStart();
            button.hide();
        }

        private static void gotoRatTreasureRoom(ProceedButton button) {
            MapRoomNode node = new MapRoomNode(-1, 16);
            node.room = new TreasureRoomBoss();
            AbstractDungeon.nextRoom = node;
            AbstractDungeon.closeCurrentScreen();
            AbstractDungeon.nextRoomTransitionStart();
            button.hide();
        }
    }
}

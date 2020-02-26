package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import demoMod.dungeons.Maze;
import javassist.CtBehavior;

@SpirePatch(
        clz = DungeonTransitionScreen.class,
        method = "setAreaName"
)
public class SetAreaNamePatch {
    @SpireInsertPatch(
            locator = SetAreaNamePatch.Locator.class
    )
    public static void Insert(DungeonTransitionScreen __instance, String key) {
        if (Maze.ID.equals(key)) {
            __instance.levelName = Maze.NAME;
            __instance.levelNum = Maze.NUM;
        }

    }

    private static class Locator extends SpireInsertLocator {
        private Locator() {
        }

        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "name");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}

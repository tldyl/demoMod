package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import demoMod.DemoMod;
import demoMod.characters.HuntressCharacter;
import demoMod.rooms.MimicRoom;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "generateMap"
)
public class CreateMimicRoomPatch {

    @SpireInsertPatch(
            locator = CreateMimicRoomPatch.Locator.class
    )
    public static void AddMimicsToMap() {
        if (DemoMod.spawnMimicForOtherCharacters || AbstractDungeon.player instanceof HuntressCharacter) {
            List<MapRoomNode> chestNodes = new ArrayList<>();

            for (List<MapRoomNode> aMap : AbstractDungeon.map) {
                for (MapRoomNode node : aMap) {
                    if ((node.room instanceof TreasureRoom && AbstractDungeon.actNum != 2) || node.room instanceof EventRoom) {
                        chestNodes.add(node);
                    }
                }
            }
            for (MapRoomNode node : chestNodes) {
                if (AbstractDungeon.mapRng.random(99) <= 2.1 * HuntressCharacter.curse + 2.25) {
                    if (node.room instanceof TreasureRoom) {
                        node.setRoom(new MimicRoom(true));
                    } else {
                        node.setRoom(new MimicRoom(false));
                    }
                }
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        private Locator() {
        }

        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(MapGenerator.class, "toString");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}

package demoMod.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import demoMod.DemoMod;
import demoMod.dungeons.Maze;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class DungeonMapPatch {
    @SpirePatch(
            clz = DungeonMap.class,
            method = "renderMapCenters"
    )
    public static class PatchRenderMapCenters {
        public static SpireReturn Prefix(DungeonMap map, SpriteBatch sb) {
            if (AbstractDungeon.id.equals(DemoMod.makeID("Maze"))) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = DungeonMap.class,
            method = "calculateMapSize"
    )
    public static class PatchCalculateMapSize {
        public static SpireReturn<Float> Prefix(DungeonMap map) {
            if (AbstractDungeon.id.equals(DemoMod.makeID("Maze"))) {
                return SpireReturn.Return(Settings.MAP_DST_Y * 8.0F - 1352.0F * Settings.scale);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = DungeonMap.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 39)
        public static SpireReturn Insert(DungeonMap map) {
            if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMPLETE
                    && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP
                    && (
                    Settings.isDebug ||
                            (AbstractDungeon.id.equals(Maze.ID) && AbstractDungeon.getCurrMapNode().y == 6)
            )) {
                if (map.bossHb.hovered && (InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed())) {
                    AbstractDungeon.getCurrMapNode().taken = true;
                    MapRoomNode node2 = AbstractDungeon.getCurrMapNode();
                    for (MapEdge e : node2.getEdges()) {
                        if (e != null) {
                            e.markAsTaken();
                        }
                    }

                    InputHelper.justClickedLeft = false;
                    CardCrawlGame.music.dispose();
                    MapRoomNode node = new MapRoomNode(-1, 15);
                    node.room = new MonsterRoomBoss();
                    AbstractDungeon.nextRoom = node;

                    if (AbstractDungeon.pathY.size() > 1) {
                        AbstractDungeon.pathX.add(AbstractDungeon.pathX.get(AbstractDungeon.pathX.size() - 1));
                        AbstractDungeon.pathY.add(AbstractDungeon.pathY.get(AbstractDungeon.pathY.size() - 1) + 1);
                    } else {
                        AbstractDungeon.pathX.add(1);
                        AbstractDungeon.pathY.add(15);
                    }
                    AbstractDungeon.nextRoomTransitionStart();
                    map.bossHb.hovered = false;
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = DungeonMapScreen.class,
            method = "open"
    )
    public static class PatchOpen {
        @SpireInsertPatch(rloc = 8)
        public static SpireReturn Insert(DungeonMapScreen screen) {
            if (AbstractDungeon.id.equals(DemoMod.makeID("Maze"))) {
                try {
                    Field field = DungeonMapScreen.class.getDeclaredField("mapScrollUpperLimit");
                    field.setAccessible(true);
                    field.set(screen, -900.0F * Settings.scale);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return SpireReturn.Continue();
        }
    }
}

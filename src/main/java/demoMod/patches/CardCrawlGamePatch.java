package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import demoMod.DemoMod;
import demoMod.dungeons.Maze;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
public class CardCrawlGamePatch {
    public static HashMap<String, AbstractDungeonBuilder> customDungeons = new HashMap<>();
    public static HashMap<String, String> nextDungeons = new HashMap<>();

    public static void addDungeon(String id, AbstractDungeonBuilder builder) {
        customDungeons.put(id, builder);
    }

    public static void addNextDungeon(String fromId, String toId) {
        nextDungeons.put(fromId, toId);
    }

    @SpirePatch(
            clz = TreasureRoomBoss.class,
            method = "getNextDungeonName",
            paramtypez = {}
    )
    public static class getNextDungeonName {
        public getNextDungeonName() {
        }

        public static SpireReturn<String> Prefix(TreasureRoomBoss self) {
            String next = nextDungeons.get(AbstractDungeon.id);
            return next != null ? SpireReturn.Return(next) : SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = CardCrawlGame.class,
            method = "getDungeon",
            paramtypez = {String.class, AbstractPlayer.class}
    )
    public static class getDungeon1 {
        public getDungeon1() {
        }

        public static AbstractDungeon Postfix(AbstractDungeon dungeon, CardCrawlGame self, String key, AbstractPlayer p) {
            if (dungeon == null) {
                AbstractDungeonBuilder builder = customDungeons.get(key);
                if (builder != null) {
                    dungeon = builder.build(p, AbstractDungeon.specialOneTimeEventList);
                }
            }

            return dungeon;
        }
    }

    @SpirePatch(
            clz = CardCrawlGame.class,
            method = "getDungeon",
            paramtypez = {String.class, AbstractPlayer.class, SaveFile.class}
    )
    public static class getDungeon2 {
        public getDungeon2() {
        }

        public static AbstractDungeon Postfix(AbstractDungeon dungeon, CardCrawlGame self, String key, AbstractPlayer p, SaveFile save) {
            if (dungeon == null) {
                AbstractDungeonBuilder builder = customDungeons.get(key);
                if (builder != null) {
                    dungeon = builder.build(p, save);
                }
            }

            return dungeon;
        }
    }

    public interface AbstractDungeonBuilder {
        AbstractDungeon build(AbstractPlayer var1, ArrayList<String> var2);

        AbstractDungeon build(AbstractPlayer var1, SaveFile var2);
    }
}

package demoMod.patches;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import demoMod.DemoMod;
import demoMod.characters.HuntressCharacter;
import demoMod.dungeons.Maze;
import demoMod.events.D20Statue;
import demoMod.events.FountainOfPurify;
import demoMod.interfaces.PostEnterNewActSubscriber;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class AbstractDungeonPatch {
    @SuppressWarnings("Duplicates")
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    String.class,
                    String.class,
                    AbstractPlayer.class,
                    ArrayList.class
            }
    )
    public static class PatchConstructor1 {
        public static void Postfix(AbstractDungeon obj) {
            if (AbstractDungeon.player != null && AbstractDungeon.player.relics != null) {
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic instanceof PostEnterNewActSubscriber) {
                        ((PostEnterNewActSubscriber) relic).onEnterNewAct();
                    }
                }
            }
            if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck != null) {
                for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                    if (card instanceof PostEnterNewActSubscriber) {
                        ((PostEnterNewActSubscriber) card).onEnterNewAct();
                    }
                }
                if (AbstractDungeon.id.equals(Maze.ID) && AbstractDungeon.player instanceof HuntressCharacter) {
                    try {
                        Field field = AbstractPlayer.class.getDeclaredField("img");
                        field.setAccessible(true);
                        field.set(AbstractDungeon.player, new Texture(DemoMod.getResourcePath("char/character2.png")));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SuppressWarnings("Duplicates")
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    String.class,
                    AbstractPlayer.class,
                    SaveFile.class
            }
    )
    public static class PatchConstructor2 {
        public static void Postfix(AbstractDungeon obj) {
            if (AbstractDungeon.player.relics != null) {
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic instanceof PostEnterNewActSubscriber) {
                        ((PostEnterNewActSubscriber) relic).onEnterNewAct();
                    }
                }
            }
            if (AbstractDungeon.id.equals(Maze.ID) && AbstractDungeon.player instanceof HuntressCharacter) {
                try {
                    Field field = AbstractPlayer.class.getDeclaredField("img");
                    field.setAccessible(true);
                    field.set(AbstractDungeon.player, new Texture(DemoMod.getResourcePath("char/character2.png")));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getShrine"
    )
    public static class PatchGetShrine {
        @SpireInsertPatch(rloc = 2, localvars = {"tmp"})
        public static void Insert(Random rng, @ByRef(type = "java.util.ArrayList") Object[] _tmp) {
            ArrayList<String> tmp = (ArrayList) _tmp[0];
            tmp.remove(D20Statue.ID);
            tmp.remove(FountainOfPurify.ID);
            if (AbstractDungeon.player instanceof HuntressCharacter &&
                    AbstractDungeon.shrineList.contains(D20Statue.ID)) {
                tmp.add(D20Statue.ID);
            }
            if (AbstractDungeon.player instanceof HuntressCharacter &&
                    HuntressCharacter.curse >= 0.5) {
                tmp.add(FountainOfPurify.ID);
            }
        }
    }
}

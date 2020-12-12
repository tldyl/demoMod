package demoMod.patches;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import demoMod.characters.HuntressCharacter;
import demoMod.events.D20Statue;
import demoMod.events.FountainOfPurify;
import demoMod.interfaces.PostEnterNewActSubscriber;
import org.apache.logging.log4j.core.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static demoMod.patches.SaveAndContinuePatch.SaveFileClassPatch.misc_seed_count;

@SuppressWarnings("unused")
public class AbstractDungeonPatch implements CustomSavable<Integer> {
    private static int actNum = 0;
    public static AbstractDungeonPatch instance;

    public AbstractDungeonPatch() {
        BaseMod.addSaveField("actNum", this);
    }

    @Override
    public Integer onSave() {
        return actNum;
    }

    @Override
    public void onLoad(Integer i) {
        if (i != null) {
            actNum = i;
        }
    }

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
            int tmp = AbstractDungeon.actNum;
            if (AbstractDungeon.player != null && AbstractDungeon.player.relics != null && actNum != tmp) {
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic instanceof PostEnterNewActSubscriber) {
                        ((PostEnterNewActSubscriber) relic).onEnterNewAct();
                    }
                }
            }
            if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck != null && actNum != tmp) {
                for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                    if (card instanceof PostEnterNewActSubscriber) {
                        ((PostEnterNewActSubscriber) card).onEnterNewAct();
                    }
                }
            }
            actNum = tmp;
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
            PatchConstructor1.Postfix(obj);
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

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "loadSeeds"
    )
    public static class PatchLoadSeeds {
        public static void Postfix(SaveFile file) {
            AbstractDungeon.miscRng = new Random(Settings.seed, misc_seed_count.get(file));
            try {
                Field field = AbstractDungeon.class.getDeclaredField("logger");
                field.setAccessible(true);
                Logger logger = (Logger) field.get(null);
                logger.info("Misc seed:   " + AbstractDungeon.miscRng.counter);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.TheLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import demoMod.DemoMod;
import demoMod.characters.HuntressCharacter;
import demoMod.relics.Ammonomicon;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class LibraryEventPatch {
    private static final EventStrings eventStrings;
    private static final String[] OPTIONS;
    private static final String[] DESCRIPTIONS;
    private static int optionIndex = 0;

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString(DemoMod.makeID("LibraryPatch"));
        OPTIONS = eventStrings.OPTIONS;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    }

    @SpirePatch(
            clz = TheLibrary.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix(TheLibrary instance) {
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                instance.imageEventText.setDialogOption(OPTIONS[0], new Ammonomicon());
                optionIndex = instance.imageEventText.optionList.size() - 1;
            }
        }
    }

    @SpirePatch(
            clz = TheLibrary.class,
            method = "buttonEffect"
    )
    public static class PatchButtonEffect {
        public static SpireReturn Prefix(TheLibrary instance, int buttonPressed) {
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                int screenNum = 1;
                try {
                    Field field = TheLibrary.class.getDeclaredField("screenNum");
                    field.setAccessible(true);
                    screenNum = (Integer) field.get(instance);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (buttonPressed == optionIndex && screenNum == 0) {
                    new Ammonomicon().instantObtain();
                    instance.imageEventText.updateBodyText(DESCRIPTIONS[0]);
                    try {
                        Field field = TheLibrary.class.getDeclaredField("screenNum");
                        field.setAccessible(true);
                        field.set(instance, 1);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    instance.imageEventText.updateDialogOption(0, TheLibrary.OPTIONS[3]);
                    instance.imageEventText.clearRemainingOptions();
                    return SpireReturn.Return(null);
                }
            }
            return SpireReturn.Continue();
        }
    }
}

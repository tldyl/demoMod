package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.beyond.Falling;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.DemoMod;
import demoMod.cards.guns.BalloonGun;

import java.lang.reflect.Field;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class FallingEventPatch {
    private static final EventStrings eventStrings;
    private static final String[] OPTIONS;
    private static final String[] DESCRIPTIONS;
    private static int optionIndex = -1;
    private static boolean hasBalloonGun = false;
    private static boolean hasJetPack = false;
    private static boolean hasWaxWing = false;
    private static boolean hasRatBoots = false;

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString(DemoMod.makeID("FallingPatch"));
        OPTIONS = eventStrings.OPTIONS;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    }

    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    @SpirePatch(
            clz = Falling.class,
            method = "buttonEffect"
    )
    public static class PatchButtonEffect {

        @SpireInsertPatch(rloc = 25)
        public static SpireReturn Insert3(Falling event, int buttonPressed) {
            AbstractCard attackCard;
            try {
                Field field = Falling.class.getDeclaredField("attackCard");
                field.setAccessible(true);
                attackCard = (AbstractCard) field.get(event);
                event.imageEventText.setDialogOption(Falling.OPTIONS[5] +
                                FontHelper.colorString(attackCard.name, "r"),
                        attackCard.makeStatEquivalentCopy());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            Insert1(event, buttonPressed);
            return SpireReturn.Return(null);
        }

        @SpireInsertPatch(rloc = 31)
        public static SpireReturn Insert1(Falling event, int buttonPressed) {
            System.out.println("DemoMod:Generating options...");
            optionIndex = event.imageEventText.optionList.size();
            hasBalloonGun = false;
            hasJetPack = false;
            hasWaxWing = false;
            hasRatBoots = false;
            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                if (card instanceof BalloonGun) {
                    hasBalloonGun = true;
                    break;
                }
            }
            if (AbstractDungeon.player.hasRelic(DemoMod.makeID("JetPack"))) {
                hasJetPack = true;
            }
            if (AbstractDungeon.player.hasRelic(DemoMod.makeID("WaxWing"))) {
                hasWaxWing = true;
            }
            if (AbstractDungeon.player.hasRelic(DemoMod.makeID("RatBoots"))) {
                hasRatBoots = true;
            }
            if (hasBalloonGun || hasJetPack || hasWaxWing || hasRatBoots) {
                event.imageEventText.setDialogOption(OPTIONS[0]);
            } else {
                event.imageEventText.setDialogOption(OPTIONS[1], true);
            }
            return SpireReturn.Continue();
        }

        @SpireInsertPatch(rloc = 37)
        public static SpireReturn Insert2(Falling event, int buttonPressed) {
            if (buttonPressed == optionIndex) {
                int t = 1;
                if (hasJetPack) {
                    t = 2;
                } else if (hasWaxWing) {
                    t = 3;
                } else if (hasRatBoots) {
                    t = 4;
                }
                event.imageEventText.updateBodyText(DESCRIPTIONS[0] + DESCRIPTIONS[t] + DESCRIPTIONS[DESCRIPTIONS.length - 1]);
                AbstractEvent.logMetricIgnored("Falling");
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}

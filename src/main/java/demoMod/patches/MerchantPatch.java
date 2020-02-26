package demoMod.patches;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.shop.Merchant;
import demoMod.DemoMod;

@SuppressWarnings("unused")
public class MerchantPatch {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("DemoMod:AfterStole");
    public static final String[] TEXT = uiStrings.TEXT;
    @SpirePatch(
            clz = Merchant.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 22, localvars = {"msg"})
        public static SpireReturn Insert(Merchant merchant, @ByRef(type = "java.lang.String") Object[] msg) {
            if (DemoMod.afterSteal) {
                msg[0] = TEXT[1];
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = Merchant.class,
            method = "welcomeSfx"
    )
    public static class PatchWelcomeSfx {
        @SpireInsertPatch(rloc = 0)
        public static SpireReturn Insert(Merchant merchant) {
            if (DemoMod.afterSteal) {
                playCantBuySfx();
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = Merchant.class,
            method = "playMiscSfx"
    )
    public static class PatchPlayMiscSfx {
        @SpireInsertPatch(rloc = 0)
        public static SpireReturn Insert(Merchant merchant) {
            if (DemoMod.afterSteal) {
                playCantBuySfx();
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    private static void playCantBuySfx() {
        if (DemoMod.afterSteal) {
            int roll = MathUtils.random(2);
            if (roll == 0) {
                CardCrawlGame.sound.play("VO_MERCHANT_2A");
            } else if (roll == 1) {
                CardCrawlGame.sound.play("VO_MERCHANT_2B");
            } else {
                CardCrawlGame.sound.play("VO_MERCHANT_2C");
            }
        }
    }
}

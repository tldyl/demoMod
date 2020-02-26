package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.SmokeBomb;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;
import demoMod.DemoMod;

@SuppressWarnings("unused")
public class SmokeBombPatch {
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("DemoMod:SmokeBomb");

    @SpirePatch(
            clz = SmokeBomb.class,
            method = "use"
    )
    public static class PatchUse {
        public static void Postfix(SmokeBomb obj) {
            AbstractPlayer p = AbstractDungeon.player;
            if (AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
                AbstractDungeon.getCurrRoom().smoked = true;
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new SmokeBombEffect(p.hb.cX, p.hb.cY)));
                CardCrawlGame.sound.play("ATTACK_WHIFF_2");
                CardCrawlGame.sound.play("APPEAR");
                DemoMod.canSteal = true;
            }
        }
    }

    @SpirePatch(
            clz = SmokeBomb.class,
            method = "canUse"
    )
    public static class PatchCanUse {
        public static SpireReturn<Boolean> Prefix(SmokeBomb obj) {
            if (AbstractDungeon.getCurrRoom() instanceof ShopRoom) return SpireReturn.Return(true);
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = SmokeBomb.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        @SpireInsertPatch(rloc = 3)
        public static void Insert(SmokeBomb obj) {
            obj.description = obj.description + potionStrings.DESCRIPTIONS[0];
        }
    }
}

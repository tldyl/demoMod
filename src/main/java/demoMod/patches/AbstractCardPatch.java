package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.relics.interfaces.PreDamageGive;

@SuppressWarnings("unused")
public class AbstractCardPatch {
    @SpirePatch(
            clz = AbstractCard.class,
            method = "applyPowers"
    )
    public static class PatchApplyPowers {
        @SpireInsertPatch(rloc = 7, localvars = {"tmp"})
        public static void Insert1(AbstractCard card, @ByRef float[] tmp) {
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof PreDamageGive) {
                    tmp[0] = ((PreDamageGive) relic).atDamageGive(tmp[0], card.damageTypeForTurn);
                }
            }
        }

        @SpireInsertPatch(rloc = 53, localvars = {"tmp"})
        public static void Insert2(AbstractCard card, @ByRef float[][] tmp) {
            for (int i=0;i<tmp.length;i++) {
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic instanceof PreDamageGive) {
                        tmp[0][i] = ((PreDamageGive) relic).atDamageGive(tmp[0][i], card.damageTypeForTurn);
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "calculateCardDamage"
    )
    public static class PatchCalculateCardDamage {
        @SpireInsertPatch(rloc = 7, localvars = {"tmp"})
        public static void Insert1(AbstractCard card, AbstractMonster m, @ByRef float[] tmp) {
            DamageInfoPatch.PatchApplyPowers.target = m;
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof PreDamageGive) {
                    tmp[0] = ((PreDamageGive) relic).atDamageGive(tmp[0], card.damageTypeForTurn);
                }
            }
        }

        @SpireInsertPatch(rloc = 63, localvars = {"tmp"})
        public static void Insert2(AbstractCard card, AbstractMonster m, @ByRef float[][] tmp) {
            for (int i=0;i<tmp.length;i++) {
                DamageInfoPatch.PatchApplyPowers.target = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic instanceof PreDamageGive) {
                        tmp[0][i] = ((PreDamageGive) relic).atDamageGive(tmp[0][i], card.damageTypeForTurn);
                    }
                }
            }
        }
    }
}

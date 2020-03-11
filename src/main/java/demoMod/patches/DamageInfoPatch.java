package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.relics.interfaces.PreDamageGive;

@SuppressWarnings("unused")
public class DamageInfoPatch {
    @SpirePatch(
            clz = DamageInfo.class,
            method = "applyPowers"
    )
    public static class PatchApplyPowers {
        public static AbstractCreature target;

        @SpireInsertPatch(rloc = 77, localvars = {"tmp"})
        public static void Insert(DamageInfo info, AbstractCreature owner, AbstractCreature target, @ByRef float[] tmp) {
            PatchApplyPowers.target = target;
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof PreDamageGive) {
                    tmp[0] = ((PreDamageGive)relic).atDamageGive(tmp[0], info.type);
                }
            }
        }
    }
}

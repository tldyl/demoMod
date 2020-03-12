package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.powers.CongealedPower;

@SuppressWarnings("unused")
public class ActionManagerPatch {
    @SpirePatch(
            clz = GameActionManager.class,
            method = "getNextAction"
    )
    public static class PatchGetNextAction {
        @SpireInsertPatch(rloc = 209, localvars={"m"})
        public static SpireReturn Insert(GameActionManager actionManager, @ByRef(type="monsters.AbstractMonster") Object[] _m) {
            AbstractMonster m = (AbstractMonster) _m[0];
            if (m.hasPower(CongealedPower.POWER_ID)) {
                CongealedPower power = (CongealedPower) m.getPower(CongealedPower.POWER_ID);
                if (power.activated) {
                    m.applyTurnPowers();
                    actionManager.monsterQueue.remove(0);
                    if (actionManager.monsterQueue.isEmpty()) {
                        actionManager.addToBottom(new WaitAction(0.8F));
                    }
                    return SpireReturn.Return(null);
                } else {
                    return SpireReturn.Continue();
                }
            }
            return SpireReturn.Continue();
        }
    }
}

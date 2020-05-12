package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SuppressWarnings("unused")
public class DamageActionPatch {
    @SpirePatch(
            clz = DamageAction.class,
            method = "update"
    )
    public static class PatchUpdate {
        public static SpireReturn<Void> Prefix(DamageAction action) {
            if (AbstractDungeon.effectList == null) {
                action.isDone = true;
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}

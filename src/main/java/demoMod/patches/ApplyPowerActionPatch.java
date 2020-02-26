package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class ApplyPowerActionPatch {
    @SpirePatch(
            clz = ApplyPowerAction.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 66)
        public static void Insert(ApplyPowerAction action) {
            try {
                Field field = ApplyPowerAction.class.getDeclaredField("powerToApply");
                field.setAccessible(true);
                AbstractPower power = (AbstractPower) field.get(action);
                for (AbstractPower powerInTarget : action.target.powers) {
                    powerInTarget.onApplyPower(power, action.target, action.source);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

package demoMod.patches;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.powers.StunImmunePower;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class ApplyPowerActionPatch {
    @SpirePatch(
            clz = ApplyPowerAction.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 66)
        public static SpireReturn<Void> Insert(ApplyPowerAction action) {
            try {
                Field field = ApplyPowerAction.class.getDeclaredField("powerToApply");
                field.setAccessible(true);
                AbstractPower power = (AbstractPower) field.get(action);
                Field dur = AbstractGameAction.class.getDeclaredField("duration");
                dur.setAccessible(true);
                float duration = (float) dur.get(action);
                if (power instanceof StunMonsterPower) {
                    if (action.target.hasPower(StunImmunePower.POWER_ID)) {
                        AbstractDungeon.actionManager.addToTop(new TextAboveCreatureAction(action.target, ApplyPowerAction.TEXT[0]));
                        action.target.getPower(StunImmunePower.POWER_ID).flash();
                        duration -= Gdx.graphics.getDeltaTime();
                        dur.set(action, duration);
                        return SpireReturn.Return(null);
                    } else {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(action.target, AbstractDungeon.player, new StunImmunePower((AbstractMonster) action.target, 3)));
                    }
                }
                for (AbstractPower powerInTarget : action.target.powers) {
                    powerInTarget.onApplyPower(power, action.target, action.source);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return SpireReturn.Continue();
        }
    }
}

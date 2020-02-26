package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ChangeTargetPatch {
    public static AbstractCreature target;
    public static List<AbstractCreature> source = new ArrayList<>();
    @SpirePatch(
            clz = AbstractGameAction.class,
            method = "setValues",
            paramtypez = {AbstractCreature.class, DamageInfo.class}
    )
    public static class ChangeDamageTarget {
        public static void Postfix(AbstractGameAction action, AbstractCreature target, DamageInfo info) {
            if (info.type == DamageType.THORNS) {
                return;
            }
            if (ChangeTargetPatch.target != null && target == AbstractDungeon.player && ChangeTargetPatch.source.contains(info.owner)) {
                action.target = ChangeTargetPatch.target;
            }
        }
    }
}

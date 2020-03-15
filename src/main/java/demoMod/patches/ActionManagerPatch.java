package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SuppressWarnings("unused")
public class ActionManagerPatch {
    @SpirePatch(
            clz = GameActionManager.class,
            method = "getNextAction"
    )
    public static class PatchGetNextAction {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals("com.megacrit.cardcrawl.monsters.AbstractMonster") && m.getMethodName().equals("takeTurn")) {
                        m.replace("if (m.intent != demoMod.patches.AbstractMonsterEnum.CONGEALED) {$_ = $proceed($$);}");
                    }
                }
            };
        }
    }
}

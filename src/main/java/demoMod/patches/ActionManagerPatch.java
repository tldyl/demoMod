package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;
import java.util.List;

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

    @SpirePatch(
            clz = GameActionManager.class,
            method = SpirePatch.CLASS
    )
    public static class AddFieldPatch {
        public static SpireField<Integer> gunsReloadedThisCombat = new SpireField<>(() -> 0);
    }

    @SpirePatch(
            clz = GameActionManager.class,
            method = "clear"
    )
    public static class PatchClear {
        public static void Prefix(GameActionManager actionManager) {
            AddFieldPatch.gunsReloadedThisCombat.set(actionManager, 0);
        }
    }

    @SpirePatch(
            clz = GameActionManager.class,
            method = "addToBottom"
    )
    @SpirePatch(
            clz = GameActionManager.class,
            method = "addToTop"
    )
    public static class PatchAddActions {
        public static List<AbstractGameAction> cachedAction = new ArrayList<>();

        public static void Postfix(GameActionManager actionManager, AbstractGameAction action) {
            cachedAction.add(action);
        }
    }

    @SpirePatch(
            clz = GameActionManager.class,
            method = "clearPostCombatActions"
    )
    public static class PatchClearPostCombatActions {
        public static void Postfix(GameActionManager actionManager) {
            PatchAddActions.cachedAction.clear();
        }
    }
}

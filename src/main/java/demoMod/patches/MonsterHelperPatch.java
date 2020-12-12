package demoMod.patches;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import demoMod.monsters.ResourcefulRat;

@SuppressWarnings("unused")
public class MonsterHelperPatch {

    private static String name = ResourcefulRat.DIALOG[MathUtils.random(9) + 2];

    @SpirePatch(
            clz = MonsterHelper.class,
            method = "getEncounterName"
    )
    public static class PatchGetEncounterName {
        public static SpireReturn<String> Prefix(String key) {
            if (key.equals(ResourcefulRat.ID)) {
                return SpireReturn.Return(name);
            }
            return SpireReturn.Continue();
        }

        public static void changeName() {
            name = ResourcefulRat.DIALOG[MathUtils.random(3) + 2];
        }
    }
}

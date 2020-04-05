package demoMod.patches;

import basemod.abstracts.CustomRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches;

@SpirePatches({
        @SpirePatch(
                clz = CustomRelic.class,
                method = "setTexture"
        ),
        @SpirePatch(
                clz = CustomRelic.class,
                method = "setTextureOutline"
        )
})
public class CustomRelicPatch {
    public static void Postfix(CustomRelic relic) {
        relic.largeImg = null;
    }
}

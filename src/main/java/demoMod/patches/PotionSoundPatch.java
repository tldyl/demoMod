package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;

@SuppressWarnings("unused")
@SpirePatch(cls="com.megacrit.cardcrawl.potions.AbstractPotion", method="playPotionSound")
public class PotionSoundPatch {
    public PotionSoundPatch() { }

    @SpireInsertPatch(rloc=0)
    public static SpireReturn Insert() {
        return SpireReturn.Return(null);
    }
}

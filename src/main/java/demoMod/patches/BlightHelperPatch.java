package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import demoMod.DemoMod;
import demoMod.blights.SpiceCounter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class BlightHelperPatch {
    private static final Logger logger = LogManager.getLogger(BlightHelper.class.getName());

    @SpirePatch(
            clz = BlightHelper.class,
            method = "getBlight"
    )
    public static class PatchBlights {
        public PatchBlights() { }

        public static SpireReturn<AbstractBlight> Prefix(String id) {
            if (id.equals(DemoMod.makeID("SpiceCounter"))) {
                SaveFile saveFile = SaveAndContinue.loadSaveFile(AbstractDungeon.player.chosenClass);
                return SpireReturn.Return(new SpiceCounter(saveFile.blight_counters.get(0)));
            }
            return SpireReturn.Continue();
        }
    }
}

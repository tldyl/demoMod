package demoMod.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;

@SuppressWarnings("unused")
public class MuseDashRenderMapPatchPatch {
    @SpirePatch(
            cls = "MuseDashReskin.patches.CharacterManagerPatch$RenderMapPatch",
            method = "Prefix",
            optional = true
    )
    public static class PatchPrefix {
        public static SpireReturn<SpireReturn<Void>> Prefix(MenuCancelButton _instance, SpriteBatch sb) {
            if (CardCrawlGame.mainMenuScreen == null) {
                return SpireReturn.Return(SpireReturn.Continue());
            }
            if (CardCrawlGame.mainMenuScreen.charSelectScreen == null) {
                return SpireReturn.Return(SpireReturn.Continue());
            }
            if (CardCrawlGame.mainMenuScreen.charSelectScreen.cancelButton == null) {
                return SpireReturn.Return(SpireReturn.Continue());
            }
            return SpireReturn.Continue();
        }
    }
}

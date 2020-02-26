package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;
import demoMod.ui.buttons.ComboManualButton;

import java.lang.reflect.Field;

import static com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen.PanelScreen.COMPENDIUM;

@SuppressWarnings("unused")
public class MenuPanelScreenPatch {
    @SpirePatch(
            clz = MenuPanelScreen.class,
            method = "initializePanels"
    )
    public static class PatchInitializePanels {
        @SpireInsertPatch(rloc = 1)
        public static void Insert(MenuPanelScreen screen) {
            MenuPanelScreen.PanelScreen screen1;
            try {
                Field field = MenuPanelScreen.class.getDeclaredField("screen");
                field.setAccessible(true);
                screen1 = (MenuPanelScreen.PanelScreen) field.get(screen);
                if (screen1 == COMPENDIUM) {
                    screen.panels.add(new ComboManualButton(MainMenuPanelButton.PanelClickResult.INFO_CARD, MainMenuPanelButton.PanelColor.BLUE, Settings.WIDTH / 2.0F + 900.0F * Settings.scale, Settings.HEIGHT / 2.0F));
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

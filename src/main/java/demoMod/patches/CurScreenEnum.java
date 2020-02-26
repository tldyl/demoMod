package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

public class CurScreenEnum {
    @SpireEnum
    public static MainMenuScreen.CurScreen COMBO_MANUAL;

    @SpireEnum
    public static AbstractDungeon.CurrentScreen DUNGEON_COMBO_MANUAL;
}

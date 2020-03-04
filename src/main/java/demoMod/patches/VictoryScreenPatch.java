package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.effects.HuntressVictoryEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class VictoryScreenPatch {
    @SpirePatch(
            clz = VictoryScreen.class,
            method = "updateVfx"
    )
    public static class PatchUpdateVfx {
        public static void Postfix(VictoryScreen screen) {
            if (AbstractDungeon.player.chosenClass == HuntressEnum.HUNTRESS) {
                try {
                    Field field = VictoryScreen.class.getDeclaredField("effect");
                    field.setAccessible(true);
                    ArrayList<AbstractGameEffect> effect = (ArrayList) field.get(screen);
                    if (effect.size() < 1) {
                        effect.add(new HuntressVictoryEffect());
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package demoMod.patches;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.cutscenes.Cutscene;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import demoMod.DemoMod;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class CutscenePatch {
    @SpirePatch(
            clz = Cutscene.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix(Cutscene cutscene, AbstractPlayer.PlayerClass chosenClass) {
            if (chosenClass == HuntressEnum.HUNTRESS) {
                ArrayList<CutscenePanel> cutscenePanels = new ArrayList<>();
                Texture bgImg = new Texture("images/scenes/purpleBg.jpg");
                if (AbstractDungeon.id.equals(TheEnding.ID)) {
                    cutscenePanels.add(
                            new CutscenePanel(DemoMod.getResourcePath("scenes/huntress1.png"), "RELIC_VORPAL_GUN")
                    );
                    cutscenePanels.add(
                            new CutscenePanel(DemoMod.getResourcePath("scenes/huntress2.png"), "ELEVATOR_OPEN") {
                                @Override
                                public void activate() {
                                    CardCrawlGame.sound.playA("ELEVATOR_OPEN", 0.0F);
                                    activated = true;
                                }
                            }
                    );
                    cutscenePanels.add(
                            new CutscenePanel(DemoMod.getResourcePath("scenes/huntress3.png"), "ELEVATOR_CLOSE") {
                                @Override
                                public void activate() {
                                    CardCrawlGame.sound.playA("ELEVATOR_CLOSE", 0.0F);
                                    activated = true;
                                }
                            }
                    );
                } else if (AbstractDungeon.id.equals("DemoExt:Forge")) {
                    cutscenePanels.add(new CutscenePanel(DemoMod.getResourcePath("scenes/huntress4.png"), "RELIC_VORPAL_GUN"));
                    cutscenePanels.add(new CutscenePanel(DemoMod.getResourcePath("scenes/huntress5.png")));
                    cutscenePanels.add(new CutscenePanel(DemoMod.getResourcePath("scenes/huntress6.png")));
                }
                try {
                    Field field = Cutscene.class.getDeclaredField("panels");
                    field.setAccessible(true);
                    field.set(cutscene, cutscenePanels);
                    field = Cutscene.class.getDeclaredField("bgImg");
                    field.setAccessible(true);
                    field.set(cutscene, bgImg);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

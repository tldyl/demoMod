package demoMod.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import demoMod.DemoMod;
import demoMod.characters.HuntressCharacter;

import java.lang.reflect.Field;

@SuppressWarnings({"WeakerAccess", "unused"})
public class CurseValuePatch {
    public static Texture curseIcon = new Texture("DemoImages/ui/panel/curse.png");
    public static final UIStrings uiStrings;
    public static final String label;
    public static final String tip;

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(DemoMod.makeID("CurseValueTip"));
        label = uiStrings.TEXT[0];
        tip = uiStrings.TEXT[1];
    }

    @SpirePatch(
            clz = TopPanel.class,
            method = "renderGold"
    )
    public static class RenderGold {
        public RenderGold() {
        }

        public static SpireReturn Prefix(TopPanel topPanel, SpriteBatch sb) {
            sb.setColor(Color.WHITE);
            try {
                Field goldIconX = TopPanel.class.getDeclaredField("goldIconX");
                goldIconX.setAccessible(true);
                float goldX = goldIconX.getFloat(topPanel);

                Field iconW = TopPanel.class.getDeclaredField("ICON_Y");
                iconW.setAccessible(true);
                float ICON_Y = iconW.getFloat(topPanel);

                iconW = TopPanel.class.getDeclaredField("ICON_W");
                iconW.setAccessible(true);
                float ICON_W = iconW.getFloat(topPanel);

                Field goldNumOffsetX = TopPanel.class.getDeclaredField("GOLD_NUM_OFFSET_X");
                goldNumOffsetX.setAccessible(true);
                float GOLD_NUM_OFFSET_X = goldNumOffsetX.getFloat(topPanel);

                Field infoTextY = TopPanel.class.getDeclaredField("INFO_TEXT_Y");
                infoTextY.setAccessible(true);
                float INFO_TEXT_Y = infoTextY.getFloat(topPanel);

                if (topPanel.goldHb.hovered) {
                    sb.draw(ImageMaster.TP_GOLD, goldX - 32.0F + 32.0F * Settings.scale, ICON_Y - 32.0F + 32.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale * 1.2F, Settings.scale * 1.2F, 0.0F, 0, 0, 64, 64, false, false);
                } else {
                    sb.draw(ImageMaster.TP_GOLD, goldX - 32.0F + 32.0F * Settings.scale, ICON_Y - 32.0F + 32.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                }
                if (AbstractDungeon.player.displayGold == AbstractDungeon.player.gold) {
                    FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(AbstractDungeon.player.displayGold), goldX + GOLD_NUM_OFFSET_X, INFO_TEXT_Y, Settings.GOLD_COLOR);
                } else if (AbstractDungeon.player.displayGold > AbstractDungeon.player.gold) {
                    FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(AbstractDungeon.player.displayGold), goldX + GOLD_NUM_OFFSET_X, INFO_TEXT_Y, Settings.RED_TEXT_COLOR);
                } else {
                    FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(AbstractDungeon.player.displayGold), goldX + GOLD_NUM_OFFSET_X, INFO_TEXT_Y, Settings.GREEN_TEXT_COLOR);
                }
                if (AbstractDungeon.player instanceof HuntressCharacter) {
                    sb.draw(CurseValuePatch.curseIcon, goldX - 32.0F + 32.0F * Settings.scale + 684.0F * Settings.scale, ICON_Y, ICON_W, ICON_W);
                    FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, String.format("%.1f", HuntressCharacter.curse), goldX - 32.0F + 32.0F * Settings.scale + 750.0F * Settings.scale, INFO_TEXT_Y, Color.PURPLE);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            topPanel.goldHb.render(sb);
            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(
            clz = TopPanel.class,
            method = "updateTips"
    )
    public static class UpdateTips {
        public static void Postfix(TopPanel topPanel) {
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                DemoMod.curseHb.update();
                if (!Settings.hideTopBar) {
                    if (DemoMod.curseHb.hovered) {
                        TipHelper.renderGenericTip((float) InputHelper.mX - 140.0F * Settings.scale, (float) Settings.HEIGHT - 120.0F * Settings.scale, label, tip);
                    }
                }
            }
        }
    }
}

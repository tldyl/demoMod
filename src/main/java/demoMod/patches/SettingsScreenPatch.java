package demoMod.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.options.SettingsScreen;
import demoMod.DemoMod;

@SuppressWarnings("unused")
public class SettingsScreenPatch {

    private static final ComboManualPopup comboManualPopup = new ComboManualPopup();

    @SpirePatch(
            clz = SettingsScreen.class,
            method = "update"
    )
    public static class PatchUpdate {
        public static void Postfix(SettingsScreen settingsScreen) {
            comboManualPopup.update();
        }
    }

    @SpirePatch(
            clz = SettingsScreen.class,
            method = "render"
    )
    public static class PatchRender {
        public static void Postfix(SettingsScreen settingsScreen, SpriteBatch sb) {
            comboManualPopup.render(sb);
        }
    }

    static class ComboManualPopup {
        private Texture btn = new Texture(DemoMod.getResourcePath("ui/buttons/ComboManual_Settings.png"));
        private UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(DemoMod.makeID("ComboManualScreen"));
        private float x = 48.0F * Settings.scale;
        private float y = Settings.HEIGHT * 0.75F - 240.0F * Settings.scale;

        public Hitbox hb = new Hitbox(240.0F * Settings.scale, 90.0F * Settings.scale);

        ComboManualPopup() {
            this.hb.move(x + 120.0F * Settings.scale, y + 45.0F * Settings.scale);
        }

        public void update() {
            this.hb.update();
            if (this.hb.justHovered) {
                CardCrawlGame.sound.play("UI_HOVER");
            }
            if ((this.hb.hovered) && (InputHelper.justClickedLeft)) {
                InputHelper.justClickedLeft = false;
                DemoMod.comboManualScreen.openInDungeon();
                AbstractDungeon.overlayMenu.cancelButton.hide();
                CardCrawlGame.sound.play("UI_CLICK_1");
            }
        }

        public void render(SpriteBatch sb) {
            if (this.hb.hovered) {
                sb.setColor(Color.WHITE);
            } else {
                sb.setColor(0.6F, 0.6F, 0.6F, 1.0F);
            }
            sb.draw(this.btn, x, y, this.hb.width, this.hb.height);
            sb.setColor(1, 1, 1, 1);
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, uiStrings.TEXT[1], this.x + 120.0F * Settings.scale, this.y + 45.0F * Settings.scale, Settings.CREAM_COLOR.cpy());
            this.hb.render(sb);
        }
    }
}

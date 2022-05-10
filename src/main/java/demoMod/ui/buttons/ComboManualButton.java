package demoMod.ui.buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton;
import demoMod.DemoMod;

import java.lang.reflect.Field;

public class ComboManualButton extends MainMenuPanelButton {
    public Texture portraitImg;
    public Texture panelImg;

    public ComboManualButton(PanelClickResult setResult, PanelColor setColor, float x, float y) {
        super(setResult, setColor, x, y);
        this.hb.width = 240.0F * Settings.scale;
        this.hb.height = 60.0F * Settings.scale;
        this.hb.translate(Settings.WIDTH - this.hb.width, Settings.HEIGHT - this.hb.height);
        String language = DemoMod.getLanguageString();
        portraitImg = new Texture(DemoMod.getResourcePath("ui/buttons/ComboManual.png")); //背景图
        panelImg = new Texture(DemoMod.getResourcePath("ui/buttons/ComboManualText_" + language + ".png")); //文字层
        try {
            Field field = MainMenuPanelButton.class.getDeclaredField("portraitImg");
            field.setAccessible(true);
            field.set(this, portraitImg);
            field = MainMenuPanelButton.class.getDeclaredField("panelImg");
            field.setAccessible(true);
            field.set(this, panelImg);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.hb.move(Settings.WIDTH - 120.0F * Settings.scale, Settings.HEIGHT - 30.0F * Settings.scale);
    }

    @SpireOverride
    protected void buttonEffect() {
        DemoMod.comboManualScreen.open();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, 1);
        sb.draw(this.portraitImg, Settings.WIDTH - this.hb.width, Settings.HEIGHT - this.hb.height, 0.0F, 0.0F, 240.0F * Settings.scale, 60.0F * Settings.scale, 1.0F, 1.0F, 0.0F, 0, 0, 160, 40, false, false);
        sb.draw(this.panelImg, Settings.WIDTH - this.hb.width, Settings.HEIGHT - this.hb.height, 0.0F, 0.0F, 240.0F * Settings.scale, 60.0F * Settings.scale, 1.0F, 1.0F, 0.0F, 0, 0, 160, 40, false, false);
        this.hb.render(sb);
    }
}

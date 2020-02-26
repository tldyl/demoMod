package demoMod.ui.buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.ui.buttons.Button;
import demoMod.ui.screens.ComboDetailScreen;
import demoMod.ui.screens.ComboManualScreen;

import java.util.List;

public class ComboItemButton extends Button {
    private Texture img;
    private String caption;
    private List<String> comboIds;

    public ComboItemButton(float x, float y, Texture img, String caption, List<String> comboIds) {
        super(x, y, img);
        this.caption = caption;
        this.img = img;
        this.comboIds = comboIds;
    }

    @Override
    public void update() {
        super.update();
        if (this.hb.justHovered) {
            CardCrawlGame.sound.play("UI_HOVER");
        }
        if (this.pressed) {
            ComboManualScreen.isDetailScreenOpen = true;
            ComboDetailScreen.comboIds = comboIds;
            ComboDetailScreen.comboName = caption;
            CardCrawlGame.sound.play("UI_CLICK_1");
            this.pressed = false;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.hb.hovered) {
            sb.setColor(this.activeColor);
        } else {
            sb.setColor(this.inactiveColor);
        }
        sb.draw(this.img, this.x, this.y, Settings.WIDTH * 0.6F, 60.0F * Settings.scale);
        sb.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        FontHelper.renderFontCenteredHeight(sb, FontHelper.eventBodyText, this.caption, this.x, this.y + 20.0F, Settings.WIDTH * 0.6F, Settings.CREAM_COLOR.cpy());
    }
}

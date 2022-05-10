package demoMod.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class ComboDetailScreen {
    private TextureRegion img;
    public boolean isOpen = false;
    private float currentA = 0.0F;
    private float targetA = 0.0F;
    private float actualA = 0.0F;

    public static List<String> comboIds;
    private Map<String, List<Combo>> combos;
    public static String comboName;
    public static String description;
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("DemoMod:ComboDetailScreen");
    private static final String OR = uiStrings.TEXT[0];
    private static final String CARD = uiStrings.TEXT[1];
    private static final String RELIC = uiStrings.TEXT[2];
    private static final String POTION = uiStrings.TEXT[3];
    private static final String label = uiStrings.TEXT[4];
    private static final String tip = uiStrings.TEXT[5];
    private static final String PLS = "+";

    private float startingY;
    private Map<String, Float> startingX;

    public ComboDetailScreen() {
        Pixmap bg = new Pixmap(Settings.SAVED_WIDTH, Settings.SAVED_HEIGHT, Pixmap.Format.RGBA8888);
        bg.setColor(0, 0, 0, 1);
        bg.fill();
        this.img = new TextureRegion(new Texture(bg));
        this.combos = new HashMap<>();
        this.startingX = new HashMap<>();
    }

    public void open() {
        isOpen = true;
        this.targetA = 0.5F;
        if (comboIds != null) {
            String[] comboId = comboIds.get(0).split(":");
            description = CardCrawlGame.languagePack.getUIString(comboId[0] + ":" + comboId[1]).TEXT[1];
            combos.clear();
            this.startingX.clear();
            for (String cmbId : comboIds) {
                combos.put(cmbId, ComboManager.getAllCombo(cmbId));
                this.startingX.put(cmbId, Settings.WIDTH / 2.0F - (combos.get(cmbId).size() * 2 - 1) * 96.0F * Settings.scale);
            }
            this.startingY = Settings.HEIGHT / 2.0F + combos.size() * 96.0F * Settings.scale + (combos.size() - 1) * 30.0F * Settings.scale;
        }
    }

    public void update() {
        if ((InputHelper.justClickedLeft) || (InputHelper.pressedEscape)) {
            InputHelper.justClickedLeft = false;
            InputHelper.pressedEscape = false;
            ComboManualScreen.isDetailScreenOpen = false;
            comboIds = null;
            isOpen = false;
            this.currentA = 0.0F;
            this.targetA = 0.0F;
            this.actualA = 0.0F;
        }
        if (Math.abs(this.actualA - this.targetA) > 0.03F) {
            this.actualA += (this.targetA - this.currentA) * Gdx.graphics.getDeltaTime() / 0.25F;
            if (this.actualA > 1) this.actualA = 1;
        } else {
            this.currentA = this.targetA;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, this.actualA);
        sb.draw(this.img, 0,
                0,
                0,
                0,
                this.img.getRegionWidth(),
                this.img.getRegionHeight(),
                1.0F, 1.0F, 0.0F);
        sb.setColor(1, 1, 1, 1);
        FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, tip, Settings.WIDTH / 2.0F, Settings.HEIGHT * 0.9F, new Color(1.0F, 1.0F, 1.0F, 1.0F));
        float y = this.startingY - 128.0F;
        int lines = 0;
        for (String comboId : this.startingX.keySet()) {
            float x = this.startingX.get(comboId);
            int ctr = 0;
            for (Combo combo : combos.get(comboId)) {
                sb.draw(combo.getComboPortrait(), x, y, 384.0F * Settings.scale, 384.0F * Settings.scale);
                if (combo instanceof AbstractCard) {
                    AbstractCard card = (AbstractCard) combo;
                    FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, card.name + CARD, x + 192.0F * Settings.scale, y + 48.0F * Settings.scale, new Color(1.0F, 1.0F, 1.0F, 1.0F));
                } else if (combo instanceof AbstractRelic) {
                    AbstractRelic relic = (AbstractRelic) combo;
                    FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, relic.name + RELIC, x + 192.0F * Settings.scale, y + 48.0F * Settings.scale, new Color(1.0F, 1.0F, 1.0F, 1.0F));
                } else if (combo instanceof AbstractPotion) {
                    AbstractPotion potion = (AbstractPotion) combo;
                    FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, potion.name + POTION, x + 192.0F * Settings.scale, y + 48.0F * Settings.scale, new Color(1.0F, 1.0F, 1.0F, 1.0F));
                }
                ctr++;
                x += 240 * Settings.scale;
                if (ctr < combos.get(comboId).size()) {
                    FontHelper.renderFontCentered(sb, FontHelper.losePowerFont, PLS, x + 111.0F * Settings.scale, y + 192.0F * Settings.scale, new Color(1.0F, 1.0F, 1.0F, 1.0F));
                    x += 90 * Settings.scale;
                }
            }
            y -= 252.0F * Settings.scale;
            lines++;
            if (lines < this.startingX.keySet().size()) {
                FontHelper.renderFontCentered(sb, FontHelper.losePowerFont, OR, Settings.WIDTH / 2.0F, y + 240.0F * Settings.scale, new Color(1.0F, 1.0F, 1.0F, 1.0F));
                y -= 60 * Settings.scale;
            }
        }
        if (comboName != null && description != null) {
            FontHelper.renderFontCentered(sb, FontHelper.losePowerFont, comboName, Settings.WIDTH / 2.0F, Settings.HEIGHT * 0.1F, new Color(1.0F, 1.0F, 1.0F, 1.0F));
            TipHelper.renderGenericTip(Settings.WIDTH * 0.03F, Settings.HEIGHT * 0.9F, label, description);
            TipHelper.render(sb);
        }
    }
}

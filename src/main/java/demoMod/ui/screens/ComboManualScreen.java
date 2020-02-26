package demoMod.ui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import com.megacrit.cardcrawl.ui.buttons.CancelButton;
import demoMod.DemoMod;
import demoMod.combo.ComboManager;
import demoMod.patches.CurScreenEnum;
import demoMod.ui.buttons.ComboItemButton;

import java.text.CollationKey;
import java.text.Collator;
import java.util.*;

@SuppressWarnings("SuspiciousNameCombination")
public class ComboManualScreen implements ScrollBarListener {
    private TextureRegion img;
    private boolean isOpen = false;
    public static boolean isDetailScreenOpen = false;
    public MenuCancelButton button = new MenuCancelButton();
    private CancelButton cancelButton = new CancelButton() {
        @Override
        public void update() {
            super.update();
            if ((hb.hovered) && (InputHelper.justClickedLeft)) {
                DemoMod.comboManualScreen.close();
                AbstractDungeon.screen = AbstractDungeon.CurrentScreen.SETTINGS;
                hide();
                AbstractDungeon.overlayMenu.cancelButton.show(uiStrings.TEXT[0]);
            }
        }
    };
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(DemoMod.makeID("ComboManualScreen"));
    public static final String[] TEXT = uiStrings.TEXT;
    public float currentDiffY;
    private float scrollLowerBound = 0;
    private float scrollUpperBound;
    private ScrollBar scrollBar;
    private boolean grabbedScreen = false;
    private float grabStartY = 0.0F;
    private static ComboDetailScreen comboDetailScreen = new ComboDetailScreen();
    private List<ComboItemButton> combos;
    private Texture cursor = ImageMaster.loadImage("images/ui/cursors/gold2.png");
    private float cursorRotation;

    public ComboManualScreen() {
        this.scrollBar = new ScrollBar(this);
        combos = new ArrayList<>();
        List<String> comboId = new ArrayList<>(ComboManager.getAllComboId());
        List<String> comboNames = new ArrayList<>();
        Map<String, List<String>> namesAndIds = new HashMap<>();
        for (String s : comboId) {
            String name = ComboManager.getComboName(s);
            if (!comboNames.contains(name)) comboNames.add(name);
            if (!namesAndIds.containsKey(name)) {
                namesAndIds.put(name, new ArrayList<>());
            }
            namesAndIds.get(name).add(s);
        }
        comboNames.sort(new Comparator<String>() {
            Collator collator = Collator.getInstance(Locale.CHINA);
            @Override
            public int compare(String o1, String o2) {
                CollationKey key1 = collator.getCollationKey(o1);
                CollationKey key2 = collator.getCollationKey(o2);
                return key1.compareTo(key2);
            }
        });
        float y = Settings.HEIGHT * 0.7F;
        for (String s : comboNames) {
            ComboItemButton button = new ComboItemButton(Settings.WIDTH * 0.2F, y, new Texture(DemoMod.getResourcePath("ui/buttons/comboItemButton.png")), s, namesAndIds.get(s));
            combos.add(button);
            y -= 80.0F;
        }
        scrollUpperBound = combos.size() * 60 + (combos.size() - 1) * 20 - Settings.HEIGHT * 0.7F;

        Pixmap bg = new Pixmap(Settings.SAVED_WIDTH, Settings.SAVED_HEIGHT, Pixmap.Format.RGBA8888);
        bg.setColor(0, 0, 0, 1);
        bg.fill();
        this.img = new TextureRegion(new Texture(bg));
    }

    public void open() {
        this.button.show(TEXT[0]);
        CardCrawlGame.mainMenuScreen.screen = CurScreenEnum.COMBO_MANUAL;
        isOpen = true;
        this.currentDiffY = this.scrollLowerBound;
    }

    public void openInDungeon() {
        this.cancelButton.show(TEXT[0]);
        AbstractDungeon.screen = CurScreenEnum.DUNGEON_COMBO_MANUAL;
        isOpen = true;
        this.currentDiffY = this.scrollLowerBound;
    }

    public void close() {
        isOpen = false;
    }

    public void update() {
        this.button.update();
        cancelButton.update();
        if (!isDetailScreenOpen && isOpen) {
            boolean isScrollBarScrolling = this.scrollBar.update();
            if (!isScrollBarScrolling) {
                updateScrolling();
            }
            if ((this.button.hb.clicked) || (InputHelper.pressedEscape)) {
                InputHelper.pressedEscape = false;
                this.button.hb.clicked = false;
                this.button.hide();
                CardCrawlGame.mainMenuScreen.panelScreen.refresh();
                this.close();
            }
            for (ComboItemButton button : combos) {
                button.y += this.currentDiffY;
                button.update();
                button.y -= this.currentDiffY;
            }
        }
        if (isOpen && isDetailScreenOpen) {
            if (!comboDetailScreen.isOpen) comboDetailScreen.open();
            comboDetailScreen.update();
        }
        if (InputHelper.isMouseDown) {
            cursorRotation = 6.0F;
        } else {
            cursorRotation = 0.0F;
        }
    }

    private void updateScrolling() {
        int y = InputHelper.mY;
        if (!this.grabbedScreen) {
            if (InputHelper.scrolledDown) {
                this.currentDiffY += Settings.SCROLL_SPEED;
            } else if (InputHelper.scrolledUp) {
                this.currentDiffY -= Settings.SCROLL_SPEED;
            }
            if (InputHelper.justClickedLeft) {
                this.grabbedScreen = true;
                this.grabStartY = (y - this.currentDiffY);
            }
        } else if (InputHelper.isMouseDown) {
            this.currentDiffY = (y - this.grabStartY);
        } else {
            this.grabbedScreen = false;
        }
        resetScrolling();
        updateBarPosition();
    }

    private void resetScrolling() {
        if (this.currentDiffY < this.scrollLowerBound) {
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollLowerBound);
        } else if (this.currentDiffY > this.scrollUpperBound) {
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollUpperBound);
        }
    }

    public void render(SpriteBatch sb) {
        this.button.render(sb);
        this.cancelButton.render(sb);
        if (isOpen) {
            if (AbstractDungeon.screen == CurScreenEnum.DUNGEON_COMBO_MANUAL) {
                sb.setColor(1, 1, 1, 0.5F);
                sb.draw(this.img, 0,
                        0,
                        0,
                        0,
                        this.img.getRegionWidth(),
                        this.img.getRegionHeight(),
                        1.0F, 1.0F, 0.0F);
            }
            this.scrollBar.render(sb);
            for (ComboItemButton button : combos) {
                button.y += this.currentDiffY;
                button.render(sb);
                button.y -= this.currentDiffY;
            }
            if (isDetailScreenOpen) {
                comboDetailScreen.render(sb);
            }
            sb.setColor(0.0F, 0.0F, 0.0F, 0.15F);
            sb.draw(this.cursor, InputHelper.mX - 32.0F + 10.0F * Settings.scale + 24.0F * Settings.scale, InputHelper.mY - 32.0F - 8.0F * Settings.scale - 24.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, this.cursorRotation, 0, 0, 64, 64, false, false);
            sb.setColor(Color.WHITE);
            sb.draw(this.cursor, InputHelper.mX - 32.0F + 24.0F * Settings.scale, InputHelper.mY - 32.0F - 24.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, this.cursorRotation, 0, 0, 64, 64, false, false);
        }
    }

    private void updateBarPosition() {
        float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.currentDiffY);
        this.scrollBar.parentScrolledToPercent(percent);
    }

    @Override
    public void scrolledUsingBar(float newPercent) {
        this.currentDiffY = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);
        updateBarPosition();
    }
}

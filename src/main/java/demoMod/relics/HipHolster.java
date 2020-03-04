package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.interfaces.PostEnterNewActSubscriber;

import java.lang.reflect.Field;

@SuppressWarnings("Duplicates")
public class HipHolster extends CustomRelic implements Combo, PostEnterNewActSubscriber {
    public static final String ID = "DemoMod:HipHolster";
    public static final String IMG_PATH = "relics/hipHolster.png";
    public static final String OUTLINE_IMG_PATH = "relics/hipHolsterOutline.png";

    public static boolean[] combos = new boolean[]{false};

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/hipHolster.png"));

    private boolean isRemoving = false;

    public HipHolster() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)), new Texture(DemoMod.getResourcePath(OUTLINE_IMG_PATH)),
                RelicTier.RARE, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        ComboManager.detectComboInGame();
        for (boolean combo : combos) {
            if (combo) {
                setDescriptionAfterLoading();
                break;
            }
        }
    }

    @Override
    public void onUnequip() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        for (boolean combo : combos) {
            if (combo) {
                setDescriptionAfterLoading();
                break;
            }
        }
    }

    private void setDescriptionAfterLoading() {
        this.description = this.DESCRIPTIONS[0];
        for (int i=0;i<combos.length;i++)
            if (combos[i]) {
                this.description = this.description + this.DESCRIPTIONS[i + 1];
            }
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void onEnterNewAct() { //每进入新的一层或读取存档时触发
        if (combos[0]) {
            activateComboEffect("DemoMod:PrettyGood");
        }
    }

    @Override
    public String getItemId() {
        return ID;
    }

    public static void activateComboEffect(String comboId) {
        switch (comboId) {
            case "DemoMod:PrettyGood":
                PotionHelper.POTION_COMMON_CHANCE = 40;
                PotionHelper.POTION_UNCOMMON_CHANCE = 30;
                try {
                    Field commonRelicChanceField = AbstractDungeon.class.getDeclaredField("commonRelicChance");
                    commonRelicChanceField.setAccessible(true);
                    commonRelicChanceField.set(CardCrawlGame.dungeon, 40);
                    Field uncommonRelicChanceField = AbstractDungeon.class.getDeclaredField("uncommonRelicChance");
                    uncommonRelicChanceField.setAccessible(true);
                    uncommonRelicChanceField.set(CardCrawlGame.dungeon, 30);
                    Field rareRelicChanceField = AbstractDungeon.class.getDeclaredField("rareRelicChance");
                    rareRelicChanceField.setAccessible(true);
                    rareRelicChanceField.set(CardCrawlGame.dungeon, 30);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("DemoMod:HipHolster:Error - Invalid combo ID:" + comboId);
        }
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:PrettyGood":
                combos[0] = true;
                break;
        }
        activateComboEffect(comboId);
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:PrettyGood":
                combos[0] = false;
                PotionHelper.POTION_COMMON_CHANCE = 70;
                PotionHelper.POTION_UNCOMMON_CHANCE = 25;
                try {
                    Field commonRelicChanceField = AbstractDungeon.class.getDeclaredField("commonRelicChance");
                    commonRelicChanceField.setAccessible(true);
                    commonRelicChanceField.set(CardCrawlGame.dungeon, 50);
                    Field uncommonRelicChanceField = AbstractDungeon.class.getDeclaredField("uncommonRelicChance");
                    uncommonRelicChanceField.setAccessible(true);
                    uncommonRelicChanceField.set(CardCrawlGame.dungeon, 33);
                    Field rareRelicChanceField = AbstractDungeon.class.getDeclaredField("rareRelicChance");
                    rareRelicChanceField.setAccessible(true);
                    rareRelicChanceField.set(CardCrawlGame.dungeon, 17);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public boolean isRemoving() {
        return isRemoving;
    }

    @Override
    public Texture getComboPortrait() {
        return comboTexture;
    }

    static {
        ComboManager.addCombo(DemoMod.makeID("PrettyGood:HipHolster"), HipHolster.class);
    }
}

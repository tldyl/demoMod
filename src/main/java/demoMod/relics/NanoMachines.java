package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.DemoMod;
import demoMod.actions.ObtainRelicAction;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.interfaces.PostEnterNewActSubscriber;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static demoMod.relics.HipHolster.activateComboEffect;

@SuppressWarnings({"Duplicates", "StringConcatenationInLoop"})
public class NanoMachines extends CustomRelic implements Combo, PostEnterNewActSubscriber {
    public static final String ID = "DemoMod:NanoMachines";
    public static final String IMG_PATH = "relics/nanoMachines.png";

    public static boolean[] combos = new boolean[]{false, false};

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/nanoMachines.png"));

    private boolean isRemoving = false;
    private static final int THRESHOLD = 4;

    public NanoMachines() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.RARE, LandingSound.CLINK);
        this.counter = 0;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        ComboManager.detectComboInGame();
        if (AbstractDungeon.player.hasRelic(Armor.ID)) {
            AbstractDungeon.player.getRelic(Armor.ID).counter += 2;
        } else {
            new Armor().instantObtain();
            AbstractDungeon.player.getRelic(Armor.ID).counter += 1;
        }
    }

    @Override
    public void wasHPLost(int damageAmount) {
        if (damageAmount > 0) {
            this.counter++;
            if (this.counter == THRESHOLD - 1) {
                this.beginLongPulse();
            }
            if (this.counter >= THRESHOLD) {
                this.flash();
                this.stopPulse();
                this.counter = 0;
                if (AbstractDungeon.player.hasRelic(Armor.ID)) {
                    AbstractDungeon.player.getRelic(Armor.ID).counter += 1;
                } else {
                    if (AbstractDungeon.player.hasRelic(Armor.ID)) {
                        AbstractDungeon.player.getRelic(Armor.ID).counter++;
                    } else {
                        this.addToBot(new ObtainRelicAction(new Armor()));
                    }
                }
            }
        }
    }

    @Override
    public void onTrigger() {

    }

    @Override
    public void onUnequip() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    private void setDescriptionAfterLoading() {
        AbstractRelic relic = AbstractDungeon.player.getRelic(ID);
        relic.description = relic.DESCRIPTIONS[0];
        for (int i=0;i<combos.length;i++)
            if (combos[i]) {
                relic.description = relic.description + relic.DESCRIPTIONS[i + 1];
            }
        relic.tips.clear();
        relic.tips.add(new PowerTip(relic.name, relic.description));
        try {
            Method initializeTips = AbstractRelic.class.getDeclaredMethod("initializeTips");
            initializeTips.setAccessible(true);
            initializeTips.invoke(relic);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:SteelSkin":
                combos[0] = true;
                break;
            case "DemoMod:PrettyGood":
                combos[1] = true;
                break;
        }
        setDescriptionAfterLoading();
        activateComboEffect(comboId);
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:SteelSkin":
                combos[0] = false;
                break;
            case "DemoMod:PrettyGood":
                combos[1] = false;
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

    @Override
    public void onEnterNewAct() {
        if (combos[0]) {
            activateComboEffect("DemoMod:PrettyGood");
        }
    }

    static {
        ComboManager.addCombo(DemoMod.makeID("PrettyGood:NanoMachines"), NanoMachines.class);
        ComboManager.addCombo(DemoMod.makeID("SteelSkin"), NanoMachines.class);
    }
}

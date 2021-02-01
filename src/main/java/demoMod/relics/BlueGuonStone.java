package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.powers.BlueGuonStonePower;

public class BlueGuonStone extends CustomRelic implements Combo {
    public static final String ID = DemoMod.makeID("BlueGuonStone");
    public static final String IMG_PATH = "relics/blueGuonStone.png";

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/blueGuonStone.png"));

    private boolean activated = false;

    private boolean isRemoving = false;
    private int blockAmount = 20;

    public BlueGuonStone() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.UNCOMMON, LandingSound.CLINK);
        this.description = this.getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void onUnequip() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public void atBattleStart() {
        activated = true;
    }

    @Override
    public void onVictory() {
        activated = false;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + this.blockAmount + this.DESCRIPTIONS[1];
    }

    @Override
    public void onPlayerEndTurn() {
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, 2));
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (activated && info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new BlueGuonStonePower(3)));
            activated = false;
        }
        return damageAmount;
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        this.blockAmount = 30;
        setDescriptionAfterLoading();
    }

    @Override
    public void onComboDisabled(String comboId) {
        this.blockAmount = 20;
        setDescriptionAfterLoading();
    }

    private void setDescriptionAfterLoading() {
        this.description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
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
        ComboManager.addCombo(DemoMod.makeID("BluerGuonStone"), BlueGuonStone.class);
    }
}

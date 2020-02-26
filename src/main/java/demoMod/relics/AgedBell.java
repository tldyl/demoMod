package demoMod.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.watcher.SkipEnemiesTurnAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.effects.TimeFreezeEffect;
import demoMod.sounds.DemoSoundMaster;

public class AgedBell extends AbstractClickRelic implements Combo {
    public static final String ID = DemoMod.makeID("AgedBell");
    public static final String IMG_PATH = "relics/agedBell.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/agedBell.png"));

    private boolean enabled = false;
    private int maxCharge = 4;
    private boolean combos[] = new boolean[]{false};
    private boolean isRemoving = false;

    public AgedBell() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.RARE, LandingSound.CLINK);
        this.counter = this.maxCharge;
        this.beginPulse();
    }

    @Override
    public void onEquip() {
        ComboManager.detectComboInGame();
        if (combos[0]) setDescriptionAfterLoading();
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

    private void setDescriptionAfterLoading() {
        this.description = this.DESCRIPTIONS[0] + this.DESCRIPTIONS[1];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void onVictory() {
        enabled = false;
        if (this.counter < maxCharge) {
            counter++;
        } else {
            if (!this.pulse) this.beginPulse();
        }
    }

    @Override
    public void atBattleStart() {
        DemoMod.canSteal = false;
        if (combos[0]) {
            setDescriptionAfterLoading();
        }
    }

    @Override
    public void atTurnStart() {
        enabled = true;
    }

    @Override
    public void onPlayerEndTurn() {
        enabled = false;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new AgedBell();
    }

    @Override
    protected void onRightClick() {
        if (this.enabled && this.counter == this.maxCharge) {
            this.flash();
            this.stopPulse();
            DemoSoundMaster.playA("RELIC_AGED_BELL", 0.0F);
            DemoMod.effectsQueue.add(new TimeFreezeEffect());
            AbstractDungeon.actionManager.addToBottom(new SkipEnemiesTurnAction());
            this.counter = 0;
        }
        if (this.counter == this.maxCharge && AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
            this.flash();
            this.stopPulse();
            DemoSoundMaster.playA("RELIC_AGED_BELL", 0.0F);
            DemoMod.effectsQueue.add(new TimeFreezeEffect());
            DemoMod.canSteal = true;
            this.counter = 0;
        }
    }

    @Override
    public String getItemId() {
        return DemoMod.makeID("AgedBell");
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:-F-":
                combos[0] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:-F-":
                combos[0] = false;
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
        ComboManager.addCombo(DemoMod.makeID("-F-:AgedBell"), AgedBell.class);
    }
}

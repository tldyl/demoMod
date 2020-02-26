package demoMod.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.DemoMod;
import demoMod.cards.tempCards.EatIt;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.patches.AbstractPlayerPatch;
import demoMod.powers.PacManPower;
import demoMod.sounds.DemoSoundMaster;

public class PartiallyEatenCheese extends AbstractClickRelic implements Combo {
    public static final String ID = DemoMod.makeID("PartiallyEatenCheese");
    public static final String IMG_PATH = "relics/partiallyEatenCheese.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/partiallyEatenCheese.png"));

    private boolean activated = false;
    private boolean isRemoving = false;

    private int maxCharge = 600;

    public PartiallyEatenCheese() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)), RelicTier.RARE, LandingSound.FLAT);
        this.counter = 600;
    }

    @Override
    public void onEquip() {
        ComboManager.detectComboInGame();
    }

    @Override
    public void onUnequip() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public int onAttackToChangeDamage(DamageInfo info, int damageAmount) {
        this.counter += damageAmount;
        if (this.counter >= maxCharge) {
            this.counter = maxCharge;
            this.activated = true;
            this.beginLongPulse();
        }
        return damageAmount;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atTurnStart() {
        if (this.counter >= maxCharge) {
            this.activated = true;
            this.beginLongPulse();
        }
        if (AbstractDungeon.player.hasPower(PacManPower.POWER_ID)) {
            if (AbstractDungeon.player.getPower(PacManPower.POWER_ID).amount > 1)
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new EatIt()));
        }
    }

    @Override
    public void onPlayerEndTurn() {
        this.activated = false;
    }

    @Override
    public void onVictory() {
        if (AbstractDungeon.player.hasPower(PacManPower.POWER_ID)) {
            DemoSoundMaster.stopL("CHEESE_LOOP");
            DemoSoundMaster.playA("CHEESE_OUTRO", 0.0F);
            AbstractPlayerPatch.PatchUpdate.enabled = false;
            AbstractPlayerPatch.PatchUpdate.idx.put("outro", -1);
            AbstractPlayerPatch.PatchUpdate.animStatus = "intro";
        }
        this.activated = false;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new PartiallyEatenCheese();
    }

    @Override
    protected void onRightClick() {
        if (activated && this.counter >= this.maxCharge) {
            this.flash();
            this.counter = 0;
            this.activated = false;
            this.stopPulse();
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new EatIt()));
            DemoSoundMaster.playA("CHEESE_INTRO", 0.0F);
            this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new PacManPower(AbstractDungeon.player, 2)));
        }
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {

    }

    @Override
    public void onComboDisabled(String comboId) {

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
        ComboManager.addCombo(DemoMod.makeID("ResourcefulIndeed"), PartiallyEatenCheese.class);
    }
}

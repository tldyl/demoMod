package demoMod.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.DemoMod;
import demoMod.characters.HuntressCharacter;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.effects.BigBoyBlastEffect;
import demoMod.sounds.DemoSoundMaster;

public class BigBoy extends AbstractClickRelic implements Combo {
    public static final String ID = DemoMod.makeID("BigBoy");
    public static final String IMG_PATH = "relics/bigBoy.png";

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/bigBoy.png"));

    private boolean enabled = false;
    private int maxCharge = 3;
    private boolean isRemoving = false;

    public BigBoy() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.RARE, LandingSound.HEAVY);
        this.counter = 3;
        this.beginPulse();
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse += 1;
        }
        ComboManager.detectComboInGame();
    }

    public void onUnequip() {
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse -= 1;
        }
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public void onVictory() {
        enabled = false;
        if (this.counter < this.maxCharge) {
            this.counter++;
        } else {
            if (!this.pulse) this.beginPulse();
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
        return new BigBoy();
    }

    @Override
    protected void onRightClick() {
        if (enabled && this.counter == this.maxCharge) {
            this.flash();
            this.stopPulse();
            DemoMod.effectsQueue.add(new BigBoyBlastEffect());
            AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(AbstractDungeon.player, DamageInfo.createDamageMatrix(40, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE));
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDeadOrEscaped()) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, AbstractDungeon.player, new PoisonPower(m, AbstractDungeon.player, 10)));
                }
            }
            this.counter = 0;
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
        ComboManager.addCombo(DemoMod.makeID("NaturalSelection:BigBoy"), BigBoy.class);
    }
}

package demoMod.powers;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;

public class StunImmunePower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("StunImmunePower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public AbstractMonster target;

    public StunImmunePower(AbstractMonster target, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.target = target;
        this.owner = target;
        this.amount = amount;
        this.loadRegion("time");
        this.isTurnBased = true;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (this.amount > 1) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[2];
        }
    }

    @Override
    public void atEndOfRound() {
        if (this.amount == 0) {
            this.addToBot(new RemoveSpecificPowerAction(this.target, this.target, POWER_ID));
        } else {
            this.addToBot(new ReducePowerAction(this.target, this.target, POWER_ID, 1));
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

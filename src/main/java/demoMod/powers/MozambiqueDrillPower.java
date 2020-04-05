package demoMod.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

@Deprecated
public class MozambiqueDrillPower extends AbstractPower {
    public static final String[] DESCRIPTIONS;
    private int multiplier = 2;
    private static final PowerStrings powerStrings;
    public static final String NAME;
    private int defaultAmount;

    public MozambiqueDrillPower(int amount) {
        this.amount = amount;
        this.defaultAmount = this.amount;
        this.name = NAME;
        this.ID = DemoMod.makeID("MozambiqueDrillPower");
        this.owner = AbstractDungeon.player;
        this.loadRegion("accuracy");
        this.updateDescription();
    }

    public void stackPower(int stackAmount) {
        multiplier += 1;
        this.updateDescription();
    }

    public void updateDescription() {
        if (this.amount > 0) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.multiplier + DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[3] + this.multiplier + DESCRIPTIONS[4];
        }
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card instanceof AbstractGunCard && this.amount > 0) {
            this.amount--;
            if (this.amount == 0) {
                this.flash();
            }
        } else if (this.amount <= 0) {
            this.amount = this.defaultAmount;
        }
        this.updateDescription();
    }

    public void atStartOfTurn() {
        this.amount = this.defaultAmount;
        updateDescription();
    }

    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        return type == DamageInfo.DamageType.NORMAL && this.amount == 0 ? damage * multiplier : damage;
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(DemoMod.makeID("MozambiqueDrillPower"));
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

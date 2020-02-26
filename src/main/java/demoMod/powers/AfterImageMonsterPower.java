package demoMod.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AfterImagePower;
import demoMod.DemoMod;

public class AfterImageMonsterPower extends AfterImagePower {

    public AfterImageMonsterPower(AbstractCreature owner, int amount) {
        super(owner, amount);
        this.ID = DemoMod.makeID("AfterImageMonsterPower");
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (Settings.FAST_MODE) {
            this.addToBot(new GainBlockAction(this.owner, this.owner, this.amount, true));
        } else {
            this.addToBot(new GainBlockAction(this.owner, this.owner, this.amount));
        }
        this.flash();
    }
}

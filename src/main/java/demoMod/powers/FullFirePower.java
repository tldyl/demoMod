package demoMod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

public class FullFirePower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("FullFirePower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public FullFirePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.loadRegion("doubleTap");
        this.updateDescription();
    }

    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0];
        } else {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        }
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (!card.purgeOnUse && card instanceof AbstractGunCard && this.amount > 0) {
            this.flash();
            AbstractMonster m = null;
            if (action.target != null) {
                m = (AbstractMonster)action.target;
            }
            int capacity = ((AbstractGunCard) card).capacity;
            for (int i=0;i<capacity;i++) {
                if (m != null) {
                    card.calculateCardDamage(m);
                }
                AbstractCard tmp = card.makeSameInstanceOf();
                tmp.purgeOnUse = true;
                tmp.current_x = card.current_x;
                tmp.current_y = card.current_y;
                tmp.freeToPlayOnce = true;
                AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(tmp, m, tmp.energyOnUse, true));
                ((AbstractGunCard) card).capacity--;
            }
            if (!((AbstractPlayer)this.owner).hasRelic("DemoMod:HipHolster") && !this.owner.hasPower(DemoMod.makeID("SlingerPower"))) card.target = AbstractCard.CardTarget.NONE;
            --this.amount;
            if (this.amount == 0) {
                this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, DemoMod.makeID("FullFirePower")));
            }
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(DemoMod.makeID("FullFirePower"));
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

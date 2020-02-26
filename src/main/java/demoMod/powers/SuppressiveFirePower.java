package demoMod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

public class SuppressiveFirePower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("SuppressiveFirePower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private boolean upgraded;
    private AbstractGunCard targetCard;

    public SuppressiveFirePower(boolean upgraded) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = AbstractDungeon.player;
        this.loadRegion("doubleTap");
        this.upgraded = upgraded;
        if (this.upgraded) this.updateDescription();
    }

    public SuppressiveFirePower(AbstractGunCard targetCard) {
        this(false);
        this.targetCard = targetCard;
        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        if (upgraded) {
            this.description = DESCRIPTIONS[0];
        } else {
            this.description = DESCRIPTIONS[1] + targetCard.name + DESCRIPTIONS[2];
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            for (AbstractCard card : AbstractDungeon.player.hand.group) {
                if (card instanceof AbstractGunCard) {
                    AbstractGunCard gunCard = (AbstractGunCard) card;
                    gunCard.isSemiAutomaticForTurn = false;
                    gunCard.returnToHand = false;
                }
            }
            for (AbstractCard card : AbstractDungeon.player.discardPile.group) {
                if (card instanceof AbstractGunCard) {
                    AbstractGunCard gunCard = (AbstractGunCard) card;
                    gunCard.isSemiAutomaticForTurn = false;
                    gunCard.returnToHand = false;
                }
            }
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

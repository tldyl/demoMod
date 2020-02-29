package demoMod.powers;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

public class EurekaPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("EurekaPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public EurekaPower(int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = AbstractDungeon.player;
        this.loadRegion("draw");
        this.updateDescription();
        this.amount = amount;
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void onCardDraw(AbstractCard c) {
        if (c instanceof AbstractGunCard) {
            this.amount--;
        }
        if (this.amount >= 0 && ((AbstractPlayer)owner).hand.size() < Settings.MAX_HAND_SIZE) {
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(1));
        } else {
            AbstractDungeon.actionManager.clearPostCombatActions();
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(owner, owner, DemoMod.makeID("EurekaPower")));
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(owner, owner, DemoMod.makeID("EurekaPower")));
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(DemoMod.makeID("EurekaPower"));
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

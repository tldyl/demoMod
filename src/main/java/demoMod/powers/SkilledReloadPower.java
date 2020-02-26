package demoMod.powers;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.interfaces.PostReloadSubscriber;

public class SkilledReloadPower extends AbstractPower implements PostReloadSubscriber {
    public static final String POWER_ID = DemoMod.makeID("SkilledReloadPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public SkilledReloadPower(int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = AbstractDungeon.player;
        this.loadRegion("draw");
        this.updateDescription();
        this.amount = amount;
        updateDescription();
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    public void onReload() {
        this.flash();
        updateDescription();
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(this.amount));
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(DemoMod.makeID("SkilledReloadPower"));
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

package demoMod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.actions.CountCardTypeAction;

import java.util.ArrayList;
import java.util.List;

public class HighPressurePower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("HighPressurePower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public AbstractCard.CardType typeForTurn;
    private boolean useOnce = false;

    public static boolean isEndByThis = false;

    public HighPressurePower() {
        this(false);
    }

    public HighPressurePower(boolean useOnce) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = AbstractDungeon.player;
        this.amount = -1;
        this.type = PowerType.DEBUFF;
        this.loadRegion("swivel");
        this.useOnce = useOnce;
        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + getTypeName(this.typeForTurn) + DESCRIPTIONS[1];
    }

    private String getTypeName(AbstractCard.CardType type) {
        if (type == AbstractCard.CardType.ATTACK) {
            return DESCRIPTIONS[3];
        } else if (type == AbstractCard.CardType.SKILL) {
            return DESCRIPTIONS[4];
        } else {
            return DESCRIPTIONS[5];
        }
    }

    @Override
    public void atStartOfTurnPostDraw() {
        AbstractDungeon.actionManager.addToBottom(new CountCardTypeAction(this));
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == this.typeForTurn) {
            if (useOnce) {
                atEndOfTurn(true);
                return;
            }
            atStartOfTurnPostDraw();
            AbstractDungeon.actionManager.addToBottom(new TextAboveCreatureAction(this.owner, DESCRIPTIONS[2]));
        } else {
            this.flash();
            isEndByThis = true;
            AbstractDungeon.actionManager.addToBottom(new PressEndTurnButtonAction());
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(DemoMod.makeID("HighPressurePower"));
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

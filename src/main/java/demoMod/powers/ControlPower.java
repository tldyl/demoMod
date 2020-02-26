package demoMod.powers;

import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.patches.ChangeTargetPatch;

public class ControlPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("ControlPower");
    public static String[] DESCRIPTIONS;
    private static AbstractCreature actionTarget;
    private static boolean isTargetReset = false;

    public ControlPower(AbstractMonster target) {
        this.ID = POWER_ID;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;
        this.owner= target;
        DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.loadRegion("confusion");
        this.type = PowerType.DEBUFF;
        this.amount = 10;
        if (ChangeTargetPatch.target != null && actionTarget == null) actionTarget = ChangeTargetPatch.target;
        isTargetReset = false;
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        this.owner.flipHorizontal = true;
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            this.flash();
            ChangeTargetPatch.source.clear();
            ChangeTargetPatch.source.addAll(AbstractDungeon.getCurrRoom().monsters.monsters);
            ChangeTargetPatch.target = action.target;
            if (action.target != null && action.target.isDeadOrEscaped()) {
                ChangeTargetPatch.target = AbstractDungeon.getRandomMonster();
            }
            if (action.target != this.owner) ((AbstractMonster)this.owner).takeTurn();
        }
    }

    @Override
    public void atStartOfTurn() {
        if (!isTargetReset) ChangeTargetPatch.target = null;
        if (actionTarget != null) {
            ChangeTargetPatch.target = actionTarget;
            actionTarget = null;
            isTargetReset = true;
        }
        this.owner.flipHorizontal = false;
        ChangeTargetPatch.source.clear();
        this.flash();
        this.addToBot(new LoseHPAction(this.owner, AbstractDungeon.player, this.amount));
        this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
    }

    @Override
    public void onDeath() {
        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!monster.isDeadOrEscaped()) {
                if (monster.hasPower(POWER_ID)) {
                    return;
                }
            }
        }
        atStartOfTurn();
    }
}

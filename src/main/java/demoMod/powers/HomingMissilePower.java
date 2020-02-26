package demoMod.powers;

import basemod.BaseMod;
import basemod.interfaces.PostPotionUseSubscriber;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.ExplosivePower;
import demoMod.DemoMod;
import demoMod.potions.BlankPotion;

public class HomingMissilePower extends ExplosivePower implements PostPotionUseSubscriber {
    public static final String POWER_ID = DemoMod.makeID("HomingMissilePower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public HomingMissilePower(AbstractCreature owner, int turns) {
        super(owner, turns);
        this.ID = POWER_ID;
        this.owner = owner;
        this.name = NAME;
        this.loadRegion("the_bomb");
        BaseMod.subscribe(this);
        this.updateDescription();
    }

    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[3] + 25 + DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + 25 + DESCRIPTIONS[2];
        }
    }

    public void duringTurn() {
        if (this.amount == 1) {
            DamageInfo damageInfo = new DamageInfo(this.owner, 25, DamageInfo.DamageType.THORNS);
            this.addToBot(new DamageAction(AbstractDungeon.player, damageInfo, AbstractGameAction.AttackEffect.FIRE, true));
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
            BaseMod.unsubscribe(this);
        } else {
            this.addToBot(new ReducePowerAction(this.owner, this.owner, POWER_ID, 1));
            this.updateDescription();
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

    @Override
    public void receivePostPotionUse(AbstractPotion potion) {
        if (potion.ID.equals(BlankPotion.ID)) {
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
            BaseMod.unsubscribe(this);
        }
    }
}

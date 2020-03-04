package demoMod.powers;

import basemod.BaseMod;
import basemod.interfaces.PostPotionUseSubscriber;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.effects.BulletSprayEffect;
import demoMod.potions.BlankPotion;

public class BulletSprayPower extends AbstractPower implements PostPotionUseSubscriber {
    public static final String POWER_ID = DemoMod.makeID("BulletSprayPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private int ctr;
    private int times;

    private static BulletSprayEffect bulletSprayEffect;

    public BulletSprayPower(AbstractCreature owner, int amount) {
        this(owner, amount, 5);
    }

    public BulletSprayPower(AbstractCreature owner, int amount, int times) {
        this.ID = POWER_ID;
        this.owner = owner;
        this.name = NAME;
        this.ctr = amount;
        this.amount = amount;
        this.times = times;
        this.loadRegion("accuracy");
        this.updateDescription();
        if (bulletSprayEffect == null) bulletSprayEffect = new BulletSprayEffect();
    }

    @Override
    public void onInitialApplication() {
        bulletSprayEffect.fadeIn();
        bulletSprayEffect.owner = (AbstractMonster) this.owner;
        AbstractDungeon.effectList.add(bulletSprayEffect);
        BaseMod.subscribe(this);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.times + DESCRIPTIONS[2];
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        this.amount--;
        if (this.amount <= 0) {
            this.flash();
            this.times--;
            this.amount = this.ctr;
            bulletSprayEffect.shrink();
            if (this.times <= 0) {
                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
                bulletSprayEffect.fadeOut();
            }
        }
        this.updateDescription();
    }

    @Override
    public void onDeath() {
        bulletSprayEffect.fadeOut();
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

    @Override
    public void receivePostPotionUse(AbstractPotion potion) {
        final PostPotionUseSubscriber power = this;
        if (potion.ID.equals(BlankPotion.ID)) {
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
            bulletSprayEffect.fadeOut();
            DemoMod.actionsQueue.add(new AbstractGameAction() {
                @Override
                public void update() {
                    BaseMod.unsubscribe(power);
                    isDone = true;
                }
            });
        }
    }
}

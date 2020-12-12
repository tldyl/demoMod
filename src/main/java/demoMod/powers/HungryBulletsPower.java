package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.interfaces.PostReloadSubscriber;
import demoMod.relics.HipHolster;

public class HungryBulletsPower extends TwoAmountPower implements PostReloadSubscriber {
    public static final String POWER_ID = DemoMod.makeID("HungryBulletsPower");
    private static final PowerStrings powerStrings;
    public static final String[] descriptions;
    private int defaultAmount;

    public HungryBulletsPower(int amount) {
        this.ID = POWER_ID;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;
        this.owner = AbstractDungeon.player;
        this.amount = amount;
        this.defaultAmount = this.amount;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/HungryBullets84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/HungryBullets32.png")), 0, 0, 32, 32);
    }

    @Override
    public void onInitialApplication() {
        this.amount2 = 1;
        updateDescription();
    }

    public void stackPower(int stackAmount) {
        if (stackAmount < this.defaultAmount) {
            this.defaultAmount = stackAmount;
            if (stackAmount < this.amount) this.amount = this.defaultAmount;
        }
        this.amount2++;
        updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (this.amount > 0) {
            if (card instanceof AbstractGunCard) {
                this.amount--;
                if (this.amount == 0) {
                    addActionToBottom();
                }
            }
        } else {
            this.amount = this.defaultAmount;
        }
        updateDescription();
    }

    private void addActionToBottom() {
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        HungryBulletsPower.this.amount = defaultAmount;
                        isDone = true;
                    }
                });
                isDone = true;
            }
        });
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (this.amount <= 0 && damageAmount > 0) {
            this.flash();
            this.addToBot(new GainBlockAction(this.owner, Math.min(damageAmount * this.amount2, target.currentHealth)));
        }
    }

    @Override
    public void updateDescription() {
        if (this.amount2 == 1) {
            if (this.amount > 1) {
                this.description = descriptions[0] + this.amount + descriptions[1];
            } else {
                this.description = descriptions[2];
            }
        } else {
            if (this.amount > 1) {
                this.description = descriptions[0] + this.amount + descriptions[3] + this.amount2 + descriptions[4];
            } else {
                this.description = descriptions[5] + this.amount2 + descriptions[6];
            }
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        descriptions = powerStrings.DESCRIPTIONS;
    }

    @Override
    public void onReload() {
        if (AbstractDungeon.player.hasRelic(HipHolster.ID)) {
            if (this.amount > 0) {
                this.amount--;
                if (this.amount == 0) {
                    addActionToBottom();
                }
            } else {
                this.amount = this.defaultAmount;
            }
            updateDescription();
        }
    }
}

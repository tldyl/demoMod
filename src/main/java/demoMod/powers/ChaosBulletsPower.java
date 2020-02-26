package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

public class ChaosBulletsPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("ChaosBulletsPower");
    public static String[] DESCRIPTIONS;

    public ChaosBulletsPower(AbstractCreature owner, int percentage) {
        this.ID = POWER_ID;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;
        this.owner= owner;
        this.amount = percentage;
        DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/ChaosBullets84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/ChaosBullets32.png")), 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card instanceof AbstractGunCard) {
            int ran = AbstractDungeon.cardRandomRng.random(99);
            if (ran < this.amount) {
                this.flash();
                AbstractPower powerToGive = getRandomDebuff(action.target, AbstractDungeon.player);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(action.target, owner, powerToGive));
            }
        }
    }

    private static AbstractPower getRandomDebuff(AbstractCreature owner, AbstractPlayer p) {
        int ran = AbstractDungeon.miscRng.random(10) + 1;
        if (owner == null) owner = AbstractDungeon.getRandomMonster();
        switch (ran) {
            case 1:
                return new WeakPower(owner, 1, false);
            case 2:
                return new VulnerablePower(owner, 1, false);
            case 3:
                return new PoisonPower(owner, p, 1);
            case 4:
                return new StrengthPower(owner, -1);
            case 5:
                return new ChokePower(owner, 1);
            case 6:
                return new LockOnPower(owner, 1);
            case 7:
                return new StunMonsterPower((AbstractMonster) owner);
            case 8:
                return new OutOfBodyPower(owner, p, 1);
            case 9:
                return new CorpseExplosionPower(owner);
            case 10:
                return new SlowPower(owner, 1);
            case 11:
                return new ConstrictedPower(owner, p, 1);
        }
        return new WeakPower(owner, 1, false);
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}

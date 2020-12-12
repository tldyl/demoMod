package demoMod.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;

public class OutOfBodyPower extends AbstractPower implements HealthBarRenderPower {
    public static final String POWER_ID = DemoMod.makeID("OutOfBodyPower");
    public static PowerType POWER_TYPE = PowerType.DEBUFF;
    private AbstractCreature source;

    public static String[] DESCRIPTIONS;

    public OutOfBodyPower(AbstractCreature owner, AbstractCreature source, int amount) {
        this.ID = POWER_ID;
        this.owner = owner;
        this.source = source;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/OutOfBody84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/OutOfBody32.png")), 0, 0, 32, 32);
        this.type = POWER_TYPE;
        this.amount = amount;
        DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;
        updateDescription();
    }

    public OutOfBodyPower(AbstractCreature owner, int amount) {
        this(owner, AbstractDungeon.player, amount);
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.owner == source && info.type == DamageInfo.DamageType.NORMAL && damageAmount > owner.currentBlock) {
            this.flash();
            AbstractDungeon.actionManager.addToTop(new LoseHPAction(this.owner, AbstractDungeon.player, this.amount));
            this.amount -= 1;
            updateDescription();
        }
        if (this.amount < 1) {
            AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, DemoMod.makeID("OutOfBodyPower")));
        }
        return damageAmount;
    }

    @Override
    public int getHealthBarAmount() {
        return this.amount;
    }

    @Override
    public Color getColor() {
        return Color.GRAY.cpy();
    }
}

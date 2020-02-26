package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;

public class OutOfBodyPower extends AbstractPower {
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
            AbstractDungeon.actionManager.addToBottom(new LoseHPAction(this.owner, AbstractDungeon.player, this.amount));
            this.amount -= 1;
        }
        if (this.amount < 1) {
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, DemoMod.makeID("OutOfBodyPower")));
        }
        return damageAmount;
    }
}

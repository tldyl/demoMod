package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.interfaces.PreAttackDecreaseBlock;

public class HardPower extends AbstractPower implements PreAttackDecreaseBlock {
    public static final String POWER_ID = DemoMod.makeID("HardPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public HardPower(int amount) {
        this.ID = POWER_ID;
        this.owner = AbstractDungeon.player;
        this.name = NAME;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/HardPower84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/HardPower32.png")), 0, 0, 32, 32);
        this.amount = amount;
        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public int onAttackBeforeDecreaseBlock(DamageInfo info, int damageAmount) {
        if (info.type != DamageInfo.DamageType.NORMAL) return damageAmount;
        if (owner.currentBlock >= damageAmount) this.flash();
        return owner.currentBlock >= damageAmount ? 1 : damageAmount;
    }

    @Override
    public void atStartOfTurn() {
        if (this.amount > 1) {
            addToBot(new ReducePowerAction(this.owner, this.owner, POWER_ID, 1));
        } else {
            addToBot(new RemoveSpecificPowerAction(owner, owner, ID));
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(DemoMod.makeID("HardPower"));
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.combo.ComboManager;

public class BlueGuonStonePower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("BlueGuonStonePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(DemoMod.makeID("BlueGuonStonePower"));
    public static final String NAME = powerStrings.NAME;
    public static String[] DESCRIPTIONS;

    public BlueGuonStonePower(int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = AbstractDungeon.player;
        this.amount = amount;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/BlueGuonStone84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/BlueGuonStone32.png")), 0, 0, 32, 32);
        DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;
        this.updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        this.flash();
        if (ComboManager.hasComboActivated(DemoMod.makeID("BluerGuonStone"))) {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this.owner, 30));
        } else {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this.owner, 20));
        }
        AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this.owner, this.owner, this.ID, 0));
        this.amount -= 1;
        if (this.amount <= 0) {
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
        }
    }

    public void updateDescription() {
        if (ComboManager.hasComboActivated(DemoMod.makeID("BluerGuonStone"))) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + 30 + DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + 20 + DESCRIPTIONS[2];
        }
    }
}

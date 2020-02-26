package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.IncreaseMaxHpAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import demoMod.DemoMod;
import demoMod.characters.HuntressCharacter;

public class StrengthOfCursePower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("StrengthOfCursePower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private int strengthToApply = (int)(Math.log(1 + HuntressCharacter.curse) * 1.5);

    public StrengthOfCursePower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("DemoImages/ui/panel/curse.png"), 0, 0, 64, 64);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("DemoImages/ui/panel/curse32.png"), 0, 0, 32, 32);
        this.updateDescription();
        if (this.owner instanceof AbstractMonster) {
            AbstractDungeon.actionManager.addToBottom(new IncreaseMaxHpAction((AbstractMonster)owner, (float)(0.05 * HuntressCharacter.curse), true));
        }
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(owner, owner, new StrengthPower(owner, strengthToApply)));
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + (int)(HuntressCharacter.curse * 3) + DESCRIPTIONS[1] + strengthToApply + DESCRIPTIONS[2];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(DemoMod.makeID("StrengthOfCursePower"));
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

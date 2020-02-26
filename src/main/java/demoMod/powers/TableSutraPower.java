package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;

public class TableSutraPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("TableSutraPower");
    public static PowerType POWER_TYPE = PowerType.BUFF;
    private AbstractCreature source;

    public static String[] DESCRIPTIONS;

    public TableSutraPower(AbstractCreature owner, AbstractCreature source, int amount) {
        this.ID = POWER_ID;
        this.owner = owner;
        this.source = source;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/TableSutra84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/TableSutra32.png")), 0, 0, 32, 32);
        this.type = POWER_TYPE;
        this.amount = amount;
        DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.flash();
            for (int i=0;i<this.amount;i++) {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(DemoMod.getRandomTableCard(), 1, false));
            }
        }
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}

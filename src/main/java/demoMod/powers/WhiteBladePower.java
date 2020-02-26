package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;

@SuppressWarnings("Duplicates")
public class WhiteBladePower extends AbstractPower {
    public static final String POWER_ID = "DemoMod:WhiteBladePower";
    public static PowerType POWER_TYPE = PowerType.DEBUFF;

    public static String[] DESCRIPTIONS;
    private AbstractCreature source;

    public void setImage(String bigImageName, String smallImageName){
        String path = DemoMod.getResourcePath("");

        String path128 = path + "powers/" + bigImageName;
        String path48 = path + "powers/" + smallImageName;

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);
    }

    public WhiteBladePower(AbstractCreature owner, AbstractCreature source, int amount) {
        this.ID = POWER_ID;
        this.owner = owner;
        this.source = source;
        this.setImage("WhiteBlade84.png", "WhiteBlade32.png");
        this.type = POWER_TYPE;
        this.amount = amount;
        DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;

        updateDescription();

    }

    @Override
    public void atEndOfRound() {
        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.source, "DemoMod:WhiteBladePower"));
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}

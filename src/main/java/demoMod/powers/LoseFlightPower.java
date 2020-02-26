package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;

@SuppressWarnings("Duplicates")
public class LoseFlightPower extends AbstractPower {
    public static final String POWER_ID = "DemoMod:LoseFlightPower";
    private static final PowerStrings powerStrings = com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings("DemoMod:LoseFlightPower");
    public static final String NAME = powerStrings.NAME;
    public static String[] DESCRIPTIONS;

    public void setImage(String bigImageName, String smallImageName){
        String path = DemoMod.getResourcePath("");

        String path128 = path + "powers/" + bigImageName;
        String path48 = path + "powers/" + smallImageName;

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);
    }

    public LoseFlightPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.setImage("LoseFlight84.png", "LoseFlight32.png");
        this.type = AbstractPower.PowerType.BUFF;
        DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;

        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        if (AbstractDungeon.player.hasPower("DemoMod:PlayerFlightPower")) {
            AbstractDungeon.player.getPower("DemoMod:PlayerFlightPower").amount--;
            if (AbstractDungeon.player.getPower("DemoMod:PlayerFlightPower").amount <= 0) {
                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, "DemoMod:PlayerFlightPower"));
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, "DemoMod:LoseFlightPower"));
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}

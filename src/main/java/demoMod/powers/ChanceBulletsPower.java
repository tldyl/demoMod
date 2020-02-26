package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;

public class ChanceBulletsPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("ChanceBulletsPower");
    public static PowerType POWER_TYPE = PowerType.BUFF;
    public boolean upgraded = false;
    private AbstractPlayer p;

    public static String[] DESCRIPTIONS;

    public ChanceBulletsPower(int percentage) {
        this.ID = POWER_ID;
        this.p = AbstractDungeon.player;
        this.owner = p;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/ChanceBullets84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/ChanceBullets32.png")), 0, 0, 32, 32);
        this.type = POWER_TYPE;
        this.amount = percentage;
        DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;
        updateDescription();
    }

    public void updateDescription() {
        if (!upgraded) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[2];
        }
    }
}

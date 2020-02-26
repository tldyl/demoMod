package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

import java.util.Random;

public class PlatinumBulletsPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("PlatinumBulletsPower");
    private static Random random = new Random();
    public static String[] DESCRIPTIONS;

    public PlatinumBulletsPower(AbstractCreature owner, int amount) {
        this.ID = POWER_ID;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;
        this.owner= owner;
        this.amount = amount;
        DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/PlatinumBullets84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/PlatinumBullets32.png")), 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card instanceof AbstractGunCard) {
            if (random.nextBoolean()) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(owner, owner, new StrengthPower(owner, amount)));
            } else {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(owner, owner, new DexterityPower(owner, amount)));
            }
        }
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }
}

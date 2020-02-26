package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

public class SlingerPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("SlingerPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public SlingerPower() {
        this.ID = POWER_ID;
        this.owner = AbstractDungeon.player;
        this.name = NAME;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/Slinger84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/Slinger32.png")), 0, 0, 32, 32);
        this.updateDescription();
        AbstractPlayer p = (AbstractPlayer)this.owner;
        for (AbstractCard c : p.drawPile.group) {
            if (c instanceof AbstractGunCard) {
                ((AbstractGunCard)c).target = ((AbstractGunCard)c).defaultTarget;
            }
        }
        for (AbstractCard c : p.hand.group) {
            if (c instanceof AbstractGunCard) {
                ((AbstractGunCard)c).target = ((AbstractGunCard)c).defaultTarget;
            }
        }
        for (AbstractCard c : p.discardPile.group) {
            if (c instanceof AbstractGunCard) {
                ((AbstractGunCard)c).target = ((AbstractGunCard)c).defaultTarget;
            }
        }
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onUseCard(AbstractCard c, UseCardAction action) {
        if (c instanceof AbstractGunCard) {
            AbstractGunCard gunCard = (AbstractGunCard)c;
            if (gunCard.isReload) {
                this.flash();
                action.exhaustCard = true;
            }
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(DemoMod.makeID("SlingerPower"));
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

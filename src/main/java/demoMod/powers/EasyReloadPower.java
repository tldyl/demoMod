package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.cards.EasyReloadBullets;
import demoMod.cards.guns.AbstractGunCard;

import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class EasyReloadPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("EasyReloadPower");
    public static PowerType POWER_TYPE = PowerType.BUFF;

    public static String[] DESCRIPTIONS;

    public EasyReloadPower(int amount) {
        this.ID = POWER_ID;
        this.owner = AbstractDungeon.player;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/EasyReload84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/EasyReload32.png")), 0, 0, 32, 32);
        this.type = POWER_TYPE;
        this.amount = amount;
        DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;
        updateDescription();
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        if (EasyReloadBullets.combos[0]) {
            this.description = this.description + DESCRIPTIONS[2];
        }
    }

    @Override
    public void onAfterCardPlayed(AbstractCard c) {
        if (c.type != AbstractCard.CardType.SKILL) return;
        this.flash();
        ArrayList<AbstractCard> cardsToReload = new ArrayList<>();
        ArrayList<AbstractCard> cardsNeededToReload = new ArrayList<>();
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (card instanceof AbstractGunCard) cardsToReload.add(card);
        }
        for (AbstractCard card : cardsToReload) {
            AbstractGunCard gunCard = (AbstractGunCard) card;
            if (gunCard.capacity < gunCard.maxCapacity || (gunCard.canFullReload() && EasyReloadBullets.combos[0])) {
                cardsNeededToReload.add(gunCard);
            }
        }
        if (cardsToReload.size() == 0) return;
        AbstractGunCard gunCard;
        if (cardsNeededToReload.isEmpty()) {
            gunCard = (AbstractGunCard) cardsToReload.get(AbstractDungeon.cardRandomRng.random(cardsToReload.size() - 1));
        } else {
            gunCard = (AbstractGunCard) cardsNeededToReload.get(AbstractDungeon.cardRandomRng.random(cardsNeededToReload.size() - 1));
        }
        if (gunCard.maxCapacity - gunCard.capacity > this.amount || !EasyReloadBullets.combos[0]) {
            gunCard.capacity += this.amount;
        } else {
            gunCard.reload();
        }
        if (gunCard.capacity > gunCard.maxCapacity) {
            gunCard.capacity = gunCard.maxCapacity;
        }
        gunCard.target = gunCard.defaultTarget;
        gunCard.superFlash();
    }
}

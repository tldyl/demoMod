package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

public class Limited extends CustomRelic {
    public static final String ID = DemoMod.makeID("Limited");
    public static final String IMG_PATH = "relics/strengthOfFortune.png";

    public Limited() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card instanceof AbstractGunCard) {
                AbstractGunCard gunCard = (AbstractGunCard) card;
                gunCard.clearAmmo();
            }
        }
    }

    @Override
    public void onMasterDeckChange() {
        this.flash();
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card instanceof AbstractGunCard) {
                AbstractGunCard gunCard = (AbstractGunCard) card;
                gunCard.clearAmmo();
            }
        }
    }
}

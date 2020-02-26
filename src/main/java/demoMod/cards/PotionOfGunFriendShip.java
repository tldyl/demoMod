package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.sounds.DemoSoundMaster;

import java.util.ArrayList;
import java.util.List;

public class PotionOfGunFriendShip extends CustomCard {
    public static final String ID = DemoMod.makeID("PotionOfGunFriendShip");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/potionOfGunFriendShip.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    private static final int COST = 0;

    public PotionOfGunFriendShip() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
        this.exhaust = true;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        DemoSoundMaster.playV("ITEM_DOUBLE_VISION", 0.1F);
        List<AbstractCard> cards = new ArrayList<>();
        for (AbstractCard c : p.hand.group) {
            if (c instanceof AbstractGunCard) cards.add(c);
        }
        if (this.upgraded) {
            for (AbstractCard c : p.drawPile.group) {
                if (c instanceof AbstractGunCard) cards.add(c);
            }
            for (AbstractCard c : p.discardPile.group) {
                if (c instanceof AbstractGunCard) cards.add(c);
            }
        }
        for (AbstractCard c : cards) {
            if (!c.upgraded) {
                c.upgrade();
                c.superFlash();
            }
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

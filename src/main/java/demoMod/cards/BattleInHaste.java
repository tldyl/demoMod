package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BattleInHaste extends CustomCard {
    public static final String ID = DemoMod.makeID("BattleInHaste");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/battleInHaste.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    private static final int COST = 0;

    public BattleInHaste() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        List<AbstractCard> cardsToMove = new ArrayList<>();
        for (AbstractCard c : p.drawPile.group) {
            if (c instanceof AbstractGunCard) cardsToMove.add(c);
        }
        Collections.shuffle(cardsToMove);
        int ctr = 0;
        for (AbstractCard c : cardsToMove) {
            ctr++;
            AbstractGunCard gunCard = (AbstractGunCard)c;
            if (p.hand.size() <= Settings.MAX_HAND_SIZE) {
                p.drawPile.moveToHand(gunCard);
            } else {
                p.createHandIsFullDialog();
                break;
            }
            if (ctr >= this.baseMagicNumber) break;
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

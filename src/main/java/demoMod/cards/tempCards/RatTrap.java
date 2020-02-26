package demoMod.cards.tempCards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;

import java.util.ArrayList;

public class RatTrap extends CustomCard {
    public static final String ID = DemoMod.makeID("RatTrap");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/tempCards/ratTrap.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.STATUS;
    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.NONE;

    private static final int COST = -2;

    public RatTrap() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, CardColor.COLORLESS, RARITY, TARGET);
        this.magicNumber = 4;
        this.baseMagicNumber = this.magicNumber;
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        ArrayList<AbstractCard> cards = AbstractDungeon.actionManager.cardsPlayedThisTurn;
        boolean trigger = false;
        for (AbstractCard card : cards) {
            if (card.type == CardType.ATTACK) {
                trigger = true;
                break;
            }
        }
        if (trigger) {
            this.exhaust = true;
            this.dontTriggerOnUseCard = true;
            AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
        }
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            this.addToBot(new DamageAction(AbstractDungeon.player, new DamageInfo(AbstractDungeon.player, this.magicNumber, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
            this.addToBot(new MakeTempCardInDrawPileAction(new Dazed(), 1, true, true));
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

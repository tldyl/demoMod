package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;

import java.util.ArrayList;

public class Unmoved extends CustomCard {
    public static final String ID = DemoMod.makeID("Unmoved");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/unmoved.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    private static final int COST = 1;

    public Unmoved() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
        this.baseBlock = 6;
        this.baseMagicNumber = 1;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(3);
            this.upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, this.block));
        ArrayList<AbstractCard> cards = new ArrayList<>();
        int ctr = 0;
        for (int i = AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1;i >= 0;i--) {
            if (AbstractDungeon.actionManager.cardsPlayedThisCombat.get(i).type == CardType.ATTACK) {
                AbstractCard attackCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(i);
                if (AbstractDungeon.actionManager.cardsPlayedThisCombat.size() > 1) {
                    for (int j=i-1;j>=0;j--) {
                        if (attackCard.uuid.equals(AbstractDungeon.actionManager.cardsPlayedThisCombat.get(j).uuid)) {
                            i = j;
                            attackCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(j);
                        } else {
                            if (AbstractDungeon.actionManager.cardsPlayedThisCombat.get(j).type == CardType.ATTACK) break;
                        }
                    }
                }
                if (!p.exhaustPile.contains(attackCard)) {
                    cards.add(attackCard);
                    ctr++;
                }
            }
            if (ctr >= this.baseMagicNumber) break;
        }
        for (AbstractCard card : cards) {
            if (p.hand.size() < Settings.MAX_HAND_SIZE) {
                if (p.discardPile.contains(card)) {
                    p.discardPile.moveToHand(card);
                } else if (p.drawPile.contains(card)) {
                    p.drawPile.moveToHand(card);
                }
            } else {
                p.createHandIsFullDialog();
                break;
            }
        }
        p.hand.refreshHandLayout();
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

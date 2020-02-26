package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;

public class FeignSurrender extends CustomCard {
    public static final String ID = DemoMod.makeID("FeignSurrender");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/feignSurrender.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final int COST = 0;

    public FeignSurrender() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
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
        int ctr = 0;
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (card.type == CardType.ATTACK) {
                ctr++;
                AbstractDungeon.actionManager.addToBottom(new DiscardSpecificCardAction(card));
            }
        }
        if (this.upgraded && ctr < 2) {
            ctr = 2;
        }
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(ctr));
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

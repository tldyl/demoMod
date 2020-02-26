package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import demoMod.DemoMod;

public class Deposit extends CustomCard {
    public static final String ID = DemoMod.makeID("Deposit");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/deposit.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final int COST = 0;

    private int ctr = 0;

    public Deposit() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, CardColor.COLORLESS, RARITY, TARGET);
        this.baseMagicNumber = 3;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(-1);
        }
    }

    @Override
    public void triggerWhenDrawn() {
        ctr++;
        this.rawDescription = DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0] + this.ctr + cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
        if (ctr >= this.baseMagicNumber) {
            AbstractPlayer p = AbstractDungeon.player;
            p.gold += 30;
            AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(this, p.hand, true));
            AbstractDungeon.effectList.add(new RainingGoldEffect(30));
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

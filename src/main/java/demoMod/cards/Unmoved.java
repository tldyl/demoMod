package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.actions.UnmovedAction;
import demoMod.cards.interfaces.TriggerInAnywhereOnOtherCardPlayed;

public class Unmoved extends CustomCard implements TriggerInAnywhereOnOtherCardPlayed {
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
        this.baseBlock = 8;
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
    public void triggerInAnywhereOnOtherCardPlayed(AbstractCard c) {
        if (c.type == CardType.ATTACK && !c.exhaust && !c.purgeOnUse) {
            UnmovedAction.findCards(this.baseMagicNumber, CardType.ATTACK);
            if (this.upgraded) {
                this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            } else {
                this.rawDescription = cardStrings.DESCRIPTION;
            }
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[0] + UnmovedAction.cards.size();
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[1];
            for (int i=0;i<UnmovedAction.cards.size() - 1;i++) {
                this.rawDescription += UnmovedAction.cards.get(i).name;
                this.rawDescription += ",";
            }
            this.rawDescription += UnmovedAction.cards.get(UnmovedAction.cards.size() - 1).name;
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[2];
            this.initializeDescription();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, this.block));
        this.addToBot(new UnmovedAction(this.baseMagicNumber, CardType.ATTACK));
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

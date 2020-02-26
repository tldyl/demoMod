package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;

public class TableTechBlanks extends CustomCard {
    public static final String ID = DemoMod.makeID("TableTechBlanks");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/tableTech.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final int COST = 0;

    public TableTechBlanks() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, CardColor.COLORLESS, RARITY, TARGET);
        this.exhaust = true;
        this.isEthereal = true;
        this.baseBlock = 6;
        this.block = this.baseBlock;
        this.baseMagicNumber = 4;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(3);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster monster) {
        int blockToAdd = 0;
        for (int i=0;i<AbstractDungeon.getCurrRoom().monsters.monsters.size();i++) {
            int unitBlock = AbstractDungeon.getCurrRoom().monsters.monsters.get(i).isDeadOrEscaped() ? 0 : this.baseMagicNumber;
            if (unitBlock == 0) continue;
            if (p.hasPower("Dexterity")) {
                int dexterity = p.getPower("Dexterity").amount;
                unitBlock += dexterity;
            }
            if (p.hasPower("Frail")) unitBlock *= 0.75;
            blockToAdd += unitBlock;
        }
        if (blockToAdd < 0) blockToAdd = 0;
        blockToAdd += this.block;
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, blockToAdd));
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;

public class TableTechStun extends CustomCard {
    public static final String ID = DemoMod.makeID("TableTechStun");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/tableTech.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final int COST = 0;

    public TableTechStun() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, CardColor.COLORLESS, RARITY, TARGET);
        this.exhaust = true;
        this.isEthereal = true;
        this.baseBlock = 6;
        this.initializeDescriptionCN();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(3);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster monster) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, this.block));
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m.type != AbstractMonster.EnemyType.BOSS && !m.isDeadOrEscaped()) AbstractDungeon.actionManager.addToBottom(new StunMonsterAction(m, p));
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

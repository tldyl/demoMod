package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.actions.BetterAttackDamageRandomEnemyAction;
import demoMod.monsters.Decoy;

public class TableTechShotgun extends CustomCard {
    public static final String ID = DemoMod.makeID("TableTechShotgun");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/tableTech.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final int COST = 0;

    public TableTechShotgun() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, CardColor.COLORLESS, RARITY, TARGET);
        this.exhaust = true;
        this.isEthereal = true;
        this.baseDamage = 3;
        this.baseBlock = 6;
        this.baseMagicNumber = 5;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(2);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster monster) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, this.block));
        for (int i=0;i<this.baseMagicNumber;i++) {
            this.addToBot(new BetterAttackDamageRandomEnemyAction(this, AbstractGameAction.AttackEffect.BLUNT_LIGHT, AbstractDungeon.getCurrRoom().monsters.getMonster(Decoy.ID)));
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.actions.UnmovedAction;

public class OldTrick extends CustomCard {
    public static final String ID = DemoMod.makeID("OldTrick");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/oldTrick.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;

    public OldTrick() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 4;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(2);
        }
    }

    @Override
    public void triggerOnOtherCardPlayed(AbstractCard c) {
        if (c.type == CardType.SKILL && !c.exhaust && !c.purgeOnUse) {
            UnmovedAction.findCards(1, CardType.SKILL);
            this.rawDescription = cardStrings.DESCRIPTION;
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
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        this.addToBot(new UnmovedAction(1, CardType.SKILL));
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

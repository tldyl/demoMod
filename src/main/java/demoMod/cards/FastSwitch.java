package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.actions.BetterGunCardInDrawPileToHandAction;
import demoMod.actions.FastSwitchAction;

public class FastSwitch extends CustomCard {
    public static final String ID = DemoMod.makeID("FastSwitch");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/fastSwitch.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final int COST = 0;

    public FastSwitch() {
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
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
        if (!this.upgraded) {
            AbstractDungeon.actionManager.addToBottom(new FastSwitchAction(1));
        } else {
            AbstractDungeon.actionManager.addToBottom(new BetterGunCardInDrawPileToHandAction(1));
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

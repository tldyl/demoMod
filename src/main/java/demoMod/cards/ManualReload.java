package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.actions.BetterGunCardInAllPileToHandAction;
import demoMod.actions.BetterGunCardInDiscardPileToHandAction;

public class ManualReload extends CustomCard {
    public static final String ID = DemoMod.makeID("ManualReload");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/manualReload.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final CardTarget TARGET = CardTarget.NONE;

    private static final int COST = 0;

    public ManualReload() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
        if (!this.upgraded) {
            AbstractDungeon.actionManager.addToBottom(new BetterGunCardInDiscardPileToHandAction(1));
        } else {
            AbstractDungeon.actionManager.addToBottom(new BetterGunCardInAllPileToHandAction(1));
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

package demoMod.cards.tempCards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import demoMod.DemoMod;

public class Flaw extends CustomCard {
    public static final String ID = DemoMod.makeID("Flaw");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/tempCards/flaw.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.STATUS;
    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.NONE;

    private static final int COST = -2;

    public Flaw() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, CardColor.COLORLESS, RARITY, TARGET);
        this.isEthereal = true;
    }

    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    public void triggerWhenDrawn() {
        AbstractPlayer p = AbstractDungeon.player;
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new VulnerablePower(p, 1, false)));
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

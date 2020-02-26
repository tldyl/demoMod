package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.powers.WhiteBladePower;

@SuppressWarnings("Duplicates")
public class WhiteBlade extends CustomCard implements PostAddedToMasterDeckSubscriber {

    public static final String ID = DemoMod.makeID("WhiteBlade");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/whiteBlade.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;
    private static int BASE_DMG = 6;

    public WhiteBlade() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, CardColor.RED, RARITY, TARGET);
        this.baseDamage = 6;
        this.damage = this.baseDamage;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(3);
            BASE_DMG = 9;
        }
    }

    @Override
    public void applyPowers() {
        this.baseDamage = BASE_DMG;
        super.applyPowers();
        this.initializeDescription();
        if (AbstractDungeon.getCurrRoom().monsters.hoveredMonster == null) return;
        for (AbstractPower power : AbstractDungeon.getCurrRoom().monsters.hoveredMonster.powers) {
            if (power.ID.equals("DemoMod:BlackBladePower")) {
                this.baseDamage = BASE_DMG + power.amount;
                super.applyPowers();
                this.initializeDescription();
                break;
            }
        }
    }

    public void onAddedToMasterDeck() {
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card instanceof BlackBlade) {
                return;
            }
        }
        AbstractDungeon.player.masterDeck.group.add(CardLibrary.getCard("DemoMod:BlackBlade").makeCopy());
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        boolean powerExists = false;
        for (AbstractPower power : m.powers) {
            if (power.ID.equals("DemoMod:BlackBladePower")) {
                powerExists = true;
                break;
            }
        }
        this.calculateCardDamage(m);
        int mBlock = m.currentBlock;
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        if (mBlock < this.damage && !m.isDying) AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new WhiteBladePower(m, p, this.damage - mBlock)));
        if (powerExists && !m.hasPower("Artifact")) {
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(m, p, "DemoMod:BlackBladePower"));
        }
        this.damage = this.baseDamage;
        this.rawDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

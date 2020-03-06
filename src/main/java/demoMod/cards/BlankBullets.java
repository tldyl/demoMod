package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.characters.HuntressCharacter;
import demoMod.powers.BlankBulletsPower;
import demoMod.sounds.DemoSoundMaster;

@SuppressWarnings("Duplicates")
public class BlankBullets extends CustomCard implements PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("BlankBullets");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/blankBullets.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.POWER;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.NONE;

    private static final int COST = 1;
    private boolean isAdded = false;

    public BlankBullets() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
        this.baseMagicNumber = 7;
        this.magicNumber = this.baseMagicNumber;
    }


    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(3);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new BlankBulletsPower(this.baseMagicNumber)));
    }

    @Override
    public void onAddedToMasterDeck() {
        if (!isAdded) {
            DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                HuntressCharacter.curse += 1.5;
            }
            isAdded = true;
        }
    }

    @Override
    public void onRemoveFromMasterDeck() {
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse -= 1.5;
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

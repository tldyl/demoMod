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
import demoMod.powers.SkilledReloadPower;
import demoMod.sounds.DemoSoundMaster;

public class SkilledReload extends CustomCard implements PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("SkilledReload");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/skilledReload.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.POWER;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.NONE;

    private static final int COST = 2;

    public SkilledReload() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
    }


    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(1);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new SkilledReloadPower(1)));
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }

    @Override
    public void onAddedToMasterDeck() {
        DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse += 1.5;
        }
    }

    @Override
    public void onRemoveFromMasterDeck() {
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse -= 1.5;
        }
    }
}

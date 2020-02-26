package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.actions.CaseyAction;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.characters.HuntressCharacter;
import demoMod.sounds.DemoSoundMaster;

public class Casey extends CustomCard implements PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("Casey");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/casey.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 2;

    public Casey() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
        this.baseMagicNumber = 2;
        this.magicNumber = this.baseMagicNumber;
        this.baseDamage = 20;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(5);
            this.upgradeMagicNumber(1);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new CaseyAction(m, new DamageInfo(AbstractDungeon.player, this.damage, DamageInfo.DamageType.NORMAL), this.baseMagicNumber));
    }

    @Override
    public void onAddedToMasterDeck() {
        DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse += 1;
        }
    }

    @Override
    public void onRemoveFromMasterDeck() {
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse -= 1;
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

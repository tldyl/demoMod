package demoMod.cards.optionCards;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.interfaces.ChargeCard;
import demoMod.cards.managers.ChargeManager;

public class ChooseNonCharge extends AbstractCard {
    public static final String ID = DemoMod.makeID("NonCharge");
    private static final CardStrings cardStrings;
    private static final String IMG_URL = DemoMod.getResourcePath("cards/optionCards/nonCharge.png");
    private static final Texture IMG = new Texture(IMG_URL);

    public ChooseNonCharge() {
        super(ID, cardStrings.NAME, IMG_URL, -2, cardStrings.DESCRIPTION, CardType.STATUS, CardColor.COLORLESS, CardRarity.COMMON, CardTarget.NONE);
        this.portrait = new TextureAtlas.AtlasRegion(IMG, 0, 0, 250, 190);
    }

    @Override
    public void onChoseThisOption() {
        ChargeCard card = ChargeManager.getChargeCard();
        if (card != null) card.onNonCharge(AbstractDungeon.player, ChargeManager.getTarget());
    }

    @Override
    public void upgrade() {

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public AbstractCard makeCopy() {
        return new ChooseNonCharge();
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    }
}

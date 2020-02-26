package demoMod.cards.tempCards;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.actions.EatMonsterAction;

public class EatIt extends CustomCard {
    public static final String ID = DemoMod.makeID("EatIt");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/tempCards/eatIt.png";
    private static final Texture IMG = new Texture(DemoMod.getResourcePath(IMG_PATH));

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 0;

    public EatIt() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, CardColor.COLORLESS, RARITY, TARGET);
        this.isEthereal = true;
        this.exhaust = true;
        this.portrait = new TextureAtlas.AtlasRegion(IMG, 0, 0, 250, 190);
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new EatMonsterAction(m));
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

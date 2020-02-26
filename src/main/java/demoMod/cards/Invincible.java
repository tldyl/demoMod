package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.InvinciblePower;
import com.megacrit.cardcrawl.vfx.combat.HeartMegaDebuffEffect;
import com.megacrit.cardcrawl.vfx.combat.VerticalAuraEffect;
import demoMod.DemoMod;

public class Invincible extends CustomCard {

    public static final String ID = DemoMod.makeID("Invincible");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/invincible.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.POWER;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;

    private static final int COST = 3;

    public Invincible() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, CardColor.COLORLESS, RARITY, TARGET);
        this.baseMagicNumber = 18;
        this.magicNumber = this.baseMagicNumber;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(-6);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Invincible();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new VFXAction(p, new VerticalAuraEffect(new Color(0.66F, 0.1F, 0.89F, 1.0F), p.hb.cX, p.hb.cY), 0.33F));
        AbstractDungeon.actionManager.addToBottom(new SFXAction("ATTACK_FIRE"));
        AbstractDungeon.actionManager.addToBottom(new VFXAction(p, new VerticalAuraEffect(Color.PURPLE, p.hb.cX, p.hb.cY), 0.33F));
        AbstractDungeon.actionManager.addToBottom(new VFXAction(p, new VerticalAuraEffect(Color.CYAN, p.hb.cX, p.hb.cY), 0.0F));
        AbstractDungeon.actionManager.addToBottom(new VFXAction(new HeartMegaDebuffEffect()));

        boolean powerExists = false;

        for (AbstractPower power : p.powers) {
            if (power.ID.equals("Invincible")) {
                powerExists = true;
                break;
            }
        }

        if (!powerExists) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new InvinciblePower(p, this.magicNumber), this.magicNumber));
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

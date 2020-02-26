package demoMod.cards.guns;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.sounds.DemoSoundMaster;

public class BSG extends AbstractGunCard {

    public static final String ID = DemoMod.makeID("BSG");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/bsg.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 2;

    public BSG() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 15;
        this.damage = this.baseDamage;
        this.capacity = 1;
        this.maxCapacity = 1;
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(6);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new BSG();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        int mblk = m.currentBlock;
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        if (this.damage > mblk) {
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                if (!monster.isDying && !monster.isEscaping) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(monster, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
                }
            }
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

package demoMod.cards.guns;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.sounds.DemoSoundMaster;

public class TripleGun extends AbstractGunCard {
    public static final String ID = DemoMod.makeID("TripleGun");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/tripleGun.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;
    private int BASE_DMG;

    public TripleGun() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.capacity = 3;
        this.maxCapacity = 3;
        this.baseDamage = 7;
        this.baseMagicNumber = 30;
        BASE_DMG = this.baseDamage;
    }

    private void tripleFire(AbstractPlayer p, AbstractMonster m) {
        if (this.capacity == 1) {
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0] + BASE_DMG + cardStrings.EXTENDED_DESCRIPTION[1] + BASE_DMG + cardStrings.EXTENDED_DESCRIPTION[2];
            this.initializeDescription();
        }
        if (this.capacity == 2) {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        } else if (this.capacity == 1) {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
            if (!m.isDying && !m.isEscaping) {
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
            }
            this.target = CardTarget.ALL_ENEMY;
        } else {
            int i = 0;
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                this.baseDamage = this.baseMagicNumber;
                this.calculateCardDamage(null);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(monster, new DamageInfo(p, this.multiDamage[i], this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
                i++;
            }
            this.target = this.defaultTarget;
        }
    }

    @Override
    public void reload() {
        if (this.capacity == this.maxCapacity) return; //不允许满弹夹装弹
        this.capacity = this.maxCapacity;
        this.baseDamage = BASE_DMG;
        this.rawDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
        DemoSoundMaster.playA(this.reloadSoundKey, 0F);
        if (AbstractDungeon.player.hasRelic("DemoMod:HipHolster")) {
            AbstractDungeon.player.getRelic("DemoMod:HipHolster").flash();
            AbstractMonster m = AbstractDungeon.getRandomMonster();
            tripleFire(AbstractDungeon.player, m);
        }
        afterReload();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
        this.baseDamage = BASE_DMG;
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        tripleFire(p, m);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(3);
            BASE_DMG += 3;
            this.upgradeMagicNumber(10);
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

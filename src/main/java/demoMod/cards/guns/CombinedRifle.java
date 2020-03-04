package demoMod.cards.guns;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.actions.PlaySoundAction;
import demoMod.cards.interfaces.MultiAttackCard;
import demoMod.powers.GunslingerPower;
import demoMod.sounds.DemoSoundMaster;

public class CombinedRifle extends AbstractGunCard implements MultiAttackCard {
    public static final String ID = DemoMod.makeID("CombinedRifle");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/combinedRifle.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;
    private boolean defaultMode = true;
    private int multi;

    public CombinedRifle() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 3;
        this.capacity = 7;
        this.maxCapacity = 7;
        this.baseMagicNumber = 0;
        this.multi = 3;
        this.reloadSoundKey = "GUN_RELOAD_AWP";
    }

    @Override
    public void reload() {
        if (this.capacity == this.maxCapacity) return;
        DemoSoundMaster.playA(this.reloadSoundKey, 0F);
        this.defaultMode = !this.defaultMode;
        if (defaultMode) {
            this.maxCapacity = 7;
            this.capacity = this.maxCapacity;
            this.baseDamage = 3;
            this.rawDescription = cardStrings.DESCRIPTION;
        } else {
            this.maxCapacity = 1;
            this.capacity = this.maxCapacity;
            this.baseDamage = 26 + this.baseMagicNumber;
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
        }
        this.initializeDescription();
        afterReload();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
        reload();
        if (p.hasPower(GunslingerPower.POWER_ID)) {
            this.calculateCardDamage(m);
        }
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        if (defaultMode) {
            for (int i=0;i<this.multi;i++) {
                this.addToBot(new PlaySoundAction("GUN_FIRE_COMBINED_RIFLE_1", 0.0F));
                this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
            }
        } else {
            this.addToBot(new PlaySoundAction("GUN_FIRE_COMBINED_RIFLE_2", 0.0F));
            this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(6);
            this.multi += 1;
        }
    }

    @Override
    public int getMulti() {
        return this.multi;
    }

    @Override
    public boolean isMultiModified() {
        return this.upgraded;
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

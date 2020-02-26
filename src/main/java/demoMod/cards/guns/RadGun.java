package demoMod.cards.guns;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.powers.GunslingerPower;
import demoMod.sounds.DemoSoundMaster;

import java.util.Random;

public class RadGun extends AbstractGunCard {
    public static final String ID = DemoMod.makeID("RadGun");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/radGun.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;
    private static int BASE_DMG = 4;
    private Random random;

    public RadGun() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 4;
        random = new Random();
        this.capacity = 4;
        this.maxCapacity = 4;
        this.canFullReload = true;
    }

    @Override
    public void reload() {
        int ran = random.nextInt(3) + 1;
        if (this.capacity == this.maxCapacity) {
            DemoSoundMaster.playA("GUN_RELOAD_RAD_SUCCESS_" + ran, 0.0F);
            this.baseDamage *= 2;
        } else {
            if (this.upgraded) {
                if (this.capacity == 2) {
                    DemoSoundMaster.playA("GUN_RELOAD_RAD_SUCCESS_" + ran, 0.0F);
                    this.baseDamage *= 2;
                } else {
                    this.baseDamage = BASE_DMG;
                    DemoSoundMaster.playA("GUN_RELOAD_RAD_FAIL_" + ran, 0.0F);
                }
            } else {
                this.baseDamage = BASE_DMG;
                DemoSoundMaster.playA("GUN_RELOAD_RAD_FAIL_" + ran, 0.0F);
            }
        }
        if (AbstractDungeon.player.hasRelic("DemoMod:HipHolster")) {
            AbstractDungeon.player.getRelic("DemoMod:HipHolster").flash();
            this.calculateCardDamage(null);
            AbstractDungeon.actionManager.addToBottom(new DamageRandomEnemyAction(new DamageInfo(AbstractDungeon.player, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        }
        this.capacity = this.maxCapacity;
        afterReload();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
        int ran = random.nextInt(3) + 1;
        if (!p.hasPower(GunslingerPower.POWER_ID)) {
            this.baseDamage = BASE_DMG;
            DemoSoundMaster.playA("GUN_RELOAD_RAD_FAIL_" + ran, 0.0F);
        } else {
            DemoSoundMaster.playA("GUN_RELOAD_RAD_SUCCESS_" + ran, 0.0F);
            this.baseDamage *= 2;
        }
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        DemoSoundMaster.playV("GUN_FIRE_RAD", 0.1F);
        if (this.damage < 64) {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        } else {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

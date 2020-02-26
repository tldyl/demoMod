package demoMod.cards.guns;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;
import com.megacrit.cardcrawl.vfx.combat.ThrowDaggerEffect;
import demoMod.DemoMod;
import demoMod.powers.ExplosiveArrowheadsPower;
import demoMod.sounds.DemoSoundMaster;

public class StickyCrossBow extends AbstractGunCard {

    public static final String ID = DemoMod.makeID("StickyCrossBow");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/stickyCrossBow.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;

    public StickyCrossBow() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 3;
        this.damage = this.baseDamage;
        this.capacity = 4;
        this.maxCapacity = 4;
        this.baseMagicNumber = 12;
        this.reloadSoundKey = "GUN_RELOAD_CROSSBOW";
        this.isSemiAutomatic = true;
    }

    @Override
    public void reload() {
        this.capacity = this.maxCapacity;   //允许满弹夹装弹
        AbstractPlayer p = AbstractDungeon.player;
        DemoSoundMaster.playA(this.reloadSoundKey, 0F);
        explodeArrows(p);
        if (AbstractDungeon.player.hasRelic("DemoMod:HipHolster")) {
            AbstractDungeon.player.getRelic("DemoMod:HipHolster").flash();
            AbstractMonster m = AbstractDungeon.getRandomMonster();
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new ThrowDaggerEffect(m.hb.cX, m.hb.cY)));
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(m, this.damage, this.damageTypeForTurn)));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new ExplosiveArrowheadsPower(m, p, this.baseMagicNumber)));
        }
        afterReload();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster monster) {
        explodeArrows(p);
    }

    private void explodeArrows(AbstractPlayer p) {
        boolean isExploded = false;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped() && m.hasPower(DemoMod.makeID("ExplosiveArrowheadsPower"))) {
                isExploded = true;
                CardCrawlGame.sound.playA("ATTACK_FIRE", MathUtils.random(-0.2F, -0.1F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ExplosionSmallEffect(m.hb.cX, m.hb.cY), 0.1F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, m.getPower(DemoMod.makeID("ExplosiveArrowheadsPower")).amount, DamageType.NORMAL)));
                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(m, p, DemoMod.makeID("ExplosiveArrowheadsPower")));
            }
        }
        if (isExploded) CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.SHORT, false);
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new VFXAction(new ThrowDaggerEffect(m.hb.cX, m.hb.cY)));
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new ExplosiveArrowheadsPower(m, p, this.baseMagicNumber)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            upgradeDamage(1);
            upgradeMagicNumber(6);
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

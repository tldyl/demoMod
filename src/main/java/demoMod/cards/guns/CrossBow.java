package demoMod.cards.guns;

import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.ThrowDaggerEffect;
import demoMod.DemoMod;
import demoMod.sounds.DemoSoundMaster;

public class CrossBow extends AbstractGunCard {
    public static final String ID = DemoMod.makeID("CrossBow");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/crossBow.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;

    public CrossBow() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 9;
        this.damage = this.baseDamage;
        this.capacity = 1;
        this.maxCapacity = 1;
        this.reloadSoundKey = "GUN_RELOAD_CROSSBOW";
    }

    @Override
    public void reload() {
        if (this.capacity == this.maxCapacity) return; //不允许满弹夹装弹
        this.capacity = this.maxCapacity;
        DemoSoundMaster.playA(this.reloadSoundKey, 0F);
        if (AbstractDungeon.player.hasRelic("DemoMod:HipHolster")) {
            AbstractDungeon.player.getRelic("DemoMod:HipHolster").flash();
            AbstractMonster m = AbstractDungeon.getRandomMonster();
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new ThrowDaggerEffect(m.hb.cX, m.hb.cY)));
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(AbstractDungeon.player, this.damage, this.damageTypeForTurn)));
        }
        afterReload();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new VFXAction(new ThrowDaggerEffect(m.hb.cX, m.hb.cY)));
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            upgradeDamage(5);
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

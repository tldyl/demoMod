package demoMod.actions;

import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.AttackDamageRandomEnemyAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;

public class BetterAttackDamageRandomEnemyAction extends AttackDamageRandomEnemyAction {
    private AbstractCard card;
    private AttackEffect effect;
    private AbstractMonster exception;
    private boolean isPureDamage = false;

    public BetterAttackDamageRandomEnemyAction(AbstractCard card, AttackEffect effect, AbstractMonster exception) {
        super(card, effect);
        this.card = card;
        this.effect = effect;
        this.exception = exception;
    }

    public BetterAttackDamageRandomEnemyAction(int damage, AttackEffect effect, AbstractMonster exception) {
        super(null, effect);
        this.effect = effect;
        this.exception = exception;
        this.isPureDamage = true;
        this.amount = damage;
    }

    public void update() {
        this.target = AbstractDungeon.getMonsters().getRandomMonster(exception, true, AbstractDungeon.cardRandomRng);
        if (this.target != null) {
            if (!isPureDamage) this.card.calculateCardDamage((AbstractMonster)this.target);
            if (AttackEffect.LIGHTNING == this.effect) {
                this.addToTop(new DamageAction(this.target, new DamageInfo(AbstractDungeon.player, isPureDamage ? amount : this.card.damage, isPureDamage ? DamageInfo.DamageType.THORNS : this.card.damageTypeForTurn), AttackEffect.NONE));
                this.addToTop(new SFXAction("ORB_LIGHTNING_EVOKE", 0.1F));
                this.addToTop(new VFXAction(new LightningEffect(this.target.hb.cX, this.target.hb.cY)));
            } else {
                this.addToTop(new DamageAction(this.target, new DamageInfo(AbstractDungeon.player, isPureDamage ? amount : this.card.damage, isPureDamage ? DamageInfo.DamageType.THORNS : this.card.damageTypeForTurn), this.effect));
            }
        }

        this.isDone = true;
    }
}

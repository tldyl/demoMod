package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import demoMod.DemoMod;

public class BottleAction extends AbstractGameAction {

    private final DamageInfo info;
    private AbstractPlayer p;

    public BottleAction(AbstractMonster m, DamageInfo info) {
        this.info = info;
        this.setValues(m, info);
        this.actionType = ActionType.DAMAGE;
        this.duration = 0.1F;
        this.p = AbstractDungeon.player;
    }

    @Override
    public void update() {
        if (this.duration == 0.1F && this.target != null) {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.NONE));
            this.target.damage(this.info);
            if ((((AbstractMonster)this.target).isDying || this.target.currentHealth <= 0) && !this.target.halfDead && !this.target.hasPower("Minion")) {
                if (this.p.hasRelic(DemoMod.makeID("Bottle"))) {
                    this.p.getRelic(DemoMod.makeID("Bottle")).counter += 3;
                }
            }

            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        this.tickDuration();
    }
}

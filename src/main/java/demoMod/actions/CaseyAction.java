package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import demoMod.DemoMod;
import demoMod.sounds.DemoSoundMaster;

public class CaseyAction extends AbstractGameAction {

    private final DamageInfo info;
    private AbstractPlayer p;
    private int multi;

    public CaseyAction(AbstractMonster m, DamageInfo info, int multi) {
        this.info = info;
        this.setValues(m, info);
        this.actionType = ActionType.DAMAGE;
        this.duration = 0.1F;
        this.p = AbstractDungeon.player;
        this.multi = multi;
    }

    @Override
    public void update() {
        if (this.duration == 0.1F && this.target != null) {
            DemoSoundMaster.playV("CASEY_BLUNT", 0.1F);
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.BLUNT_HEAVY));
            int curHp = this.target.currentHealth;
            this.target.damage(this.info);
            if ((((AbstractMonster)this.target).isDying || this.target.currentHealth <= 0)) {
                int flowDamage = info.output - curHp;
                flowDamage *= this.multi;
                this.info.output = flowDamage;
                this.info.type = DamageInfo.DamageType.THORNS;
                AbstractMonster m = AbstractDungeon.getRandomMonster();
                if (m != null)
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(m, this.info, AttackEffect.BLUNT_HEAVY));
            }

            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        this.tickDuration();
    }
}

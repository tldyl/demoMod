package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.sounds.DemoSoundMaster;

public class SelfExplodeAction extends AbstractGameAction {
    private AbstractCreature owner;
    private int damageAmount;

    public SelfExplodeAction(AbstractCreature owner, int damageAmount) {
        this.duration = 0.3F;
        this.damageAmount = damageAmount;
        this.owner = owner;
    }

    @Override
    public void update() {
        this.tickDuration();
        if (this.isDone) {
            DemoSoundMaster.playV("MONSTER_SHOTGUN_KIN_EXPLODE", 0.1F);
            DemoMod.actionsQueue.add(new DamageAction(AbstractDungeon.player, new DamageInfo(owner, this.damageAmount, DamageInfo.DamageType.THORNS), AttackEffect.FIRE));
        }
    }
}

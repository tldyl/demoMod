package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;
import demoMod.effects.OrangeGuonStoneDamageEffect;
import demoMod.sounds.DemoSoundMaster;

public class OrangeGuonStoneDamageAction extends AbstractGameAction {

    private AbstractGameEffect effect;
    private int damageAmount;
    private AbstractMonster monster;

    public OrangeGuonStoneDamageAction(AbstractMonster m, int damageAmount) {
        this.monster = m;
        float mX = m.hb.cX;
        float mY = m.hb.cY;
        effect = new OrangeGuonStoneDamageEffect(mX, mY);
        this.duration = effect.startingDuration;
        this.damageAmount = damageAmount;
    }

    @Override
    public void update() {
        if (this.duration == effect.startingDuration) {
            System.out.println("DemoMod:OrangeGuonStoneDamageAction");
            DemoSoundMaster.playA("GUN_FIRE_MEGAHAND_1", 0.0F);
            DemoMod.effectsQueue.add(effect);
        }
        this.tickDuration();
        if (this.isDone) {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(monster, new DamageInfo(AbstractDungeon.player, damageAmount, DamageInfo.DamageType.THORNS)));
        }
    }
}

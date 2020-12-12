package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.effects.ResourcefulSackBulletExplodeEffect;

public class ResourcefulSackAction extends AbstractGameAction {
    private AbstractMonster target;
    private int amount;

    public ResourcefulSackAction(AbstractMonster target, int amount) {
        this.target = target;
        this.amount = amount;
    }

    @Override
    public void update() {
        for (int i=0;i<this.amount;i++) {
            AbstractDungeon.actionManager.addToBottom(new PlaySoundAction("ARROW_EXPLODE", 0.0F));
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new ResourcefulSackBulletExplodeEffect(this.target), 0.0F));
            AbstractDungeon.actionManager.addToBottom(new DamageAction(target, new DamageInfo(AbstractDungeon.player, 3, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.NONE));
        }
        this.isDone = true;
    }
}

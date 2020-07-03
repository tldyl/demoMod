package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.InvinciblePower;

import java.lang.reflect.Field;

public class CheckInvincibleAction extends AbstractGameAction {
    private float percent;

    public CheckInvincibleAction(AbstractMonster m, float percent) {
        this.target = m;
        this.percent = percent;
    }

    @Override
    public void update() {
        if (this.target.hasPower(InvinciblePower.POWER_ID)) {
            try {
                Field field = InvinciblePower.class.getDeclaredField("maxAmt");
                field.setAccessible(true);
                int maxAmt = (int) field.get(this.target.getPower(InvinciblePower.POWER_ID));
                maxAmt *= 1 + this.percent;
                field.set(this.target.getPower(InvinciblePower.POWER_ID), maxAmt);
                this.target.getPower(InvinciblePower.POWER_ID).amount = maxAmt;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        this.isDone = true;
    }
}

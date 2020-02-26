package demoMod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.FlightPower;

public class PlayerFlightPower extends FlightPower {
    public PlayerFlightPower(AbstractCreature owner, int amount) {
        super(owner, amount);
        this.name = NAME;
        this.ID = "DemoMod:PlayerFlightPower";
        this.owner = owner;
        this.amount = amount;

        updateDescription();
        loadRegion("flight");
        this.priority = 50;
    }

    private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type) {
        return type != DamageInfo.DamageType.HP_LOSS && type != DamageInfo.DamageType.THORNS ? damage / 2.0F : damage;
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        Boolean willLive = this.calculateDamageTakenAmount((float)damageAmount, info.type) < (float)this.owner.currentHealth;
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0 && willLive) {
            this.flash();
            this.amount--;
            if (this.amount < 0) this.amount = 0;
            atStartOfTurn();
        }

        return damageAmount;
    }

    @Override
    public void atStartOfTurn() {
        this.updateDescription();
        if (this.amount <= 0) {
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, "DemoMod:PlayerFlightPower"));
        }
    }

    @Override
    public void onRemove() {

    }
}

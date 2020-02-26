package demoMod.powers;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.monsters.Mouser;

public class AlertModePower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("AlertModePower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public AlertModePower(AbstractMonster owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("amplify");
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    public void onDeath() {
        for (int i=0;i<this.amount;i++) {
            AbstractMonster monster = new Mouser(MathUtils.random(-300.0F, 180.0F), MathUtils.random(-100.0F, 380.0F), this.amount - 1);
            AbstractGameAction action = new SpawnMonsterAction(monster, false);
            action.update();
            monster.usePreBattleAction();
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

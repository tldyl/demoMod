package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import demoMod.interfaces.Resetable;

public class ResetMonsterStatusAction extends AbstractGameAction {
    private Resetable target;

    public ResetMonsterStatusAction(Resetable monsterToReset) {
        this.target = monsterToReset;
        this.duration = 0.25F;
    }

    @Override
    public void update() {
        this.target.reset();
        this.isDone = true;
    }
}

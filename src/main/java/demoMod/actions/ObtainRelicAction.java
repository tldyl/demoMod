package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class ObtainRelicAction extends AbstractGameAction {
    private AbstractRelic relicToObtain;

    public ObtainRelicAction(AbstractRelic relicToObtain) {
        this.relicToObtain = relicToObtain;
    }

    @Override
    public void update() {
        relicToObtain.instantObtain();
        this.isDone = true;
    }
}

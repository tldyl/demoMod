package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class LoseRelicAction extends AbstractGameAction {
    private AbstractRelic relicToLose;

    public LoseRelicAction(AbstractRelic relicToLose) {
        this.relicToLose = relicToLose;
    }

    @Override
    public void update() {
        AbstractDungeon.player.relics.remove(relicToLose);
        AbstractDungeon.player.reorganizeRelics();
        this.isDone = true;
    }
}

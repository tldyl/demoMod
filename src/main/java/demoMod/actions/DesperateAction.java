package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.cards.guns.AbstractGunCard;

public class DesperateAction extends AbstractGameAction {

    public DesperateAction() {
        this.duration = Settings.ACTION_DUR_FASTER;
    }

    @Override
    public void update() {
        AbstractDungeon.actionManager.addToTop(new WaitAction(0.4F));
        this.tickDuration();
        if (this.isDone) {
            for (AbstractCard c : DrawCardAction.drawnCards) {
                if (c instanceof AbstractGunCard) {
                    this.addToBot(new GainEnergyAction(1));
                }
            }
        }
    }
}

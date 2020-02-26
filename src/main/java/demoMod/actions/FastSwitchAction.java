package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

public class FastSwitchAction extends AbstractGameAction {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private float startingDuration;

    public FastSwitchAction(int numCards) {
        this.amount = numCards;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.startingDuration = Settings.ACTION_DUR_FAST;
        this.duration = this.startingDuration;
    }

    @Override
    public void update() {
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.isDone = true;
        } else {
            if (this.duration == this.startingDuration) {
                if (AbstractDungeon.player.drawPile.isEmpty()) {
                    this.isDone = true;
                    return;
                }
                CardGroup tmpGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                if (this.amount != -1) {
                    for(int i = 0; i < Math.min(this.amount, AbstractDungeon.player.drawPile.size()); ++i) {
                        tmpGroup.addToTop(AbstractDungeon.player.drawPile.group.get(AbstractDungeon.player.drawPile.size() - i - 1));
                    }
                } else {
                    for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
                        tmpGroup.addToBottom(c);
                    }
                }
                AbstractDungeon.gridSelectScreen.open(tmpGroup, this.amount, true, TEXT[0]);
            } else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    if (AbstractDungeon.player.hand.size() < Settings.MAX_HAND_SIZE && c instanceof AbstractGunCard) {
                        AbstractDungeon.player.drawPile.moveToHand(c);
                    } else {
                        if (c instanceof AbstractGunCard) AbstractDungeon.player.drawPile.moveToDiscardPile(c);
                    }
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
            this.tickDuration();
        }
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(DemoMod.makeID("FastSwitchAction"));
        TEXT = uiStrings.TEXT;
    }
}

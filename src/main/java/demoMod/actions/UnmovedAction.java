package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class UnmovedAction extends AbstractGameAction {

    public UnmovedAction(int amount) {
        this.amount = amount;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        AbstractPlayer p = AbstractDungeon.player;
        ArrayList<AbstractCard> cards = new ArrayList<>();
        int ctr = 0;
        for (int i = AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1; i >= 0; i--) {
            if (AbstractDungeon.actionManager.cardsPlayedThisCombat.get(i).type == AbstractCard.CardType.ATTACK) {
                AbstractCard attackCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(i);
                if (AbstractDungeon.actionManager.cardsPlayedThisCombat.size() > 1) {
                    for (int j=i-1;j>=0;j--) {
                        if (attackCard.uuid.equals(AbstractDungeon.actionManager.cardsPlayedThisCombat.get(j).uuid)) {
                            i = j;
                            attackCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(j);
                        } else {
                            if (AbstractDungeon.actionManager.cardsPlayedThisCombat.get(j).type == AbstractCard.CardType.ATTACK) break;
                        }
                    }
                }
                if (!p.exhaustPile.contains(attackCard)) {
                    cards.add(attackCard);
                    ctr++;
                }
            }
            if (ctr >= this.amount) break;
        }
        for (AbstractCard card : cards) {
            if (p.hand.size() < Settings.MAX_HAND_SIZE) {
                if (p.discardPile.contains(card)) {
                    p.discardPile.moveToHand(card);
                } else if (p.drawPile.contains(card)) {
                    p.drawPile.moveToHand(card);
                }
            } else {
                p.createHandIsFullDialog();
                break;
            }
        }
        p.hand.refreshHandLayout();
        this.isDone = true;
    }
}

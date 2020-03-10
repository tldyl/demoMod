package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.List;

public class UnmovedAction extends AbstractGameAction {
    public static List<AbstractCard> cards = new ArrayList<>();
    private AbstractPlayer p = AbstractDungeon.player;
    private AbstractCard.CardType type;

    public UnmovedAction(int amount, AbstractCard.CardType type) {
        this.amount = amount;
        this.duration = Settings.ACTION_DUR_FAST;
        this.type = type;
    }

    public static void findCards(int amt, AbstractCard.CardType type) {
        if (AbstractDungeon.player == null) return;
        cards.clear();
        int ctr = 0;
        for (int i = AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1; i >= 0; i--) {
            if (AbstractDungeon.actionManager.cardsPlayedThisCombat.get(i).type == type) {
                AbstractCard attackCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(i);
                if (AbstractDungeon.actionManager.cardsPlayedThisCombat.size() > 1) {
                    for (int j=i-1;j>=0;j--) {
                        if (attackCard.uuid.equals(AbstractDungeon.actionManager.cardsPlayedThisCombat.get(j).uuid)) {
                            i = j;
                            attackCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(j);
                        } else {
                            if (AbstractDungeon.actionManager.cardsPlayedThisCombat.get(j).type == type) break;
                        }
                    }
                }
                if (!AbstractDungeon.player.exhaustPile.contains(attackCard) && !attackCard.purgeOnUse) {
                    cards.add(attackCard);
                    ctr++;
                }
            }
            if (ctr >= amt) break;
        }
    }

    @Override
    public void update() {
        findCards(amount, type);
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

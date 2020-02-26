package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

import java.util.ArrayList;

@SuppressWarnings({"WeakerAccess", "Duplicates"})
public class BetterGunCardInDrawPileToHandAction extends AbstractGameAction {
    public static final String[] TEXT;
    private AbstractPlayer player;
    private int numberOfCards;
    private boolean optional;
    private CardGroup gunCardsInDraw;

    public BetterGunCardInDrawPileToHandAction(int numberOfCards, boolean optional) {
        this.numberOfCards = numberOfCards;
        this.optional = optional;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
        this.gunCardsInDraw = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.player = AbstractDungeon.player;
    }

    public BetterGunCardInDrawPileToHandAction(int numberOfCards) {
        this(numberOfCards, false);
    }

    @Override
    public void update() {
        if (this.duration == this.startDuration) {
            if (!this.player.drawPile.isEmpty() && this.numberOfCards > 0) {
                for (AbstractCard c : this.player.drawPile.group) {
                    if (c instanceof AbstractGunCard) {
                        gunCardsInDraw.addToHand(c);
                    }
                }
                if (gunCardsInDraw.size() <= this.numberOfCards && !this.optional) {
                    ArrayList<AbstractCard> cardsToMove = new ArrayList<>(gunCardsInDraw.group);
                    for (AbstractCard c : cardsToMove) {
                        if (this.player.hand.size() < 10) {
                            this.player.drawPile.moveToHand(c, this.player.drawPile);
                        } else {
                            this.player.drawPile.moveToDiscardPile(c);
                            this.player.createHandIsFullDialog();
                        }
                    }

                    this.isDone = true;
                } else {
                    if (this.numberOfCards == 1) {
                        if (this.optional) {
                            AbstractDungeon.gridSelectScreen.open(gunCardsInDraw, this.numberOfCards, true, TEXT[0]);
                        } else {
                            AbstractDungeon.gridSelectScreen.open(gunCardsInDraw, this.numberOfCards, TEXT[0], false);
                        }
                    } else if (this.optional) {
                        AbstractDungeon.gridSelectScreen.open(gunCardsInDraw, this.numberOfCards, true, TEXT[1] + this.numberOfCards + TEXT[2]);
                    } else {
                        AbstractDungeon.gridSelectScreen.open(gunCardsInDraw, this.numberOfCards, TEXT[1] + this.numberOfCards + TEXT[2], false);
                    }

                    this.tickDuration();
                }
            } else {
                this.isDone = true;
            }
        } else {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    if (this.player.hand.size() < 10) {
                        this.player.drawPile.moveToHand(c, this.player.drawPile);
                    } else {
                        this.player.drawPile.moveToDiscardPile(c);
                        this.player.createHandIsFullDialog();
                    }
                }

                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.player.hand.refreshHandLayout();
            }
            this.tickDuration();
        }
    }

    static {
        TEXT = CardCrawlGame.languagePack.getUIString(DemoMod.makeID("BetterGunCardInDiscardPileToHandAction")).TEXT;
    }
}

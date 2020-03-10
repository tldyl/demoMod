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
import demoMod.relics.CrisisStone;

import java.util.ArrayList;

@SuppressWarnings({"WeakerAccess", "Duplicates"})
public class BetterGunCardInDiscardPileToHandAction extends AbstractGameAction {
    public static final String[] TEXT;
    private AbstractPlayer player;
    private int numberOfCards;
    private boolean optional;
    private CardGroup gunCardsInDiscard;

    public BetterGunCardInDiscardPileToHandAction(int numberOfCards, boolean optional) {
        this.player = AbstractDungeon.player;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
        this.numberOfCards = numberOfCards;
        this.optional = optional;
        this.gunCardsInDiscard = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    }

    public BetterGunCardInDiscardPileToHandAction(int numberOfCards) {
        this(numberOfCards, false);
    }

    @Override
    public void update() {
        if (this.duration == this.startDuration) {
            if (!this.player.discardPile.isEmpty() && this.numberOfCards > 0) {
                for (AbstractCard c : this.player.discardPile.group) {
                    if (c instanceof AbstractGunCard) {
                        gunCardsInDiscard.addToHand(c);
                    }
                }
                if (gunCardsInDiscard.size() <= this.numberOfCards && !this.optional) {
                    ArrayList<AbstractCard> cardsToMove = new ArrayList<>(gunCardsInDiscard.group);
                    for (AbstractCard c : cardsToMove) {
                        if (this.player.hand.size() < Settings.MAX_HAND_SIZE) {
                            this.player.discardPile.moveToHand(c);
                            AbstractGunCard gunCard = (AbstractGunCard)c;
                            gunCard.target = gunCard.defaultTarget;
                            gunCard.isReload = false;
                            if (this.player.hasRelic(DemoMod.makeID("CrisisStone"))) {
                                if (gunCard.capacity <= 0) {
                                    ((CrisisStone)this.player.getRelic(DemoMod.makeID("CrisisStone"))).onReload(1);
                                }
                            }
                            gunCard.reload();
                            gunCard.superFlash();
                        } else {
                            this.player.createHandIsFullDialog();
                        }
                    }
                    this.isDone = true;
                } else {
                    if (this.numberOfCards == 1) {
                        if (this.optional) {
                            AbstractDungeon.gridSelectScreen.open(gunCardsInDiscard, this.numberOfCards, true, TEXT[0]);
                        } else {
                            AbstractDungeon.gridSelectScreen.open(gunCardsInDiscard, this.numberOfCards, TEXT[0], false);
                        }
                    } else if (this.optional) {
                        AbstractDungeon.gridSelectScreen.open(gunCardsInDiscard, this.numberOfCards, true, TEXT[1] + this.numberOfCards + TEXT[2]);
                    } else {
                        AbstractDungeon.gridSelectScreen.open(gunCardsInDiscard, this.numberOfCards, TEXT[1] + this.numberOfCards + TEXT[2], false);
                    }

                    this.tickDuration();
                }
            } else {
                this.isDone = true;
            }
        } else {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    if (this.player.hand.size() < Settings.MAX_HAND_SIZE) {
                        this.player.discardPile.moveToHand(c);
                        AbstractGunCard gunCard = (AbstractGunCard)c;
                        gunCard.target = gunCard.defaultTarget;
                        gunCard.isReload = false;
                        if (this.player.hasRelic(DemoMod.makeID("CrisisStone"))) {
                            if (gunCard.capacity <= 0) {
                                ((CrisisStone)player.getRelic(DemoMod.makeID("CrisisStone"))).onReload(1);
                            }
                        }
                        gunCard.reload();
                        gunCard.superFlash();
                    }
                    c.lighten(false);
                    c.unhover();
                }
                for (AbstractCard c : this.player.discardPile.group) {
                    c.unhover();
                    c.target_x = (float)CardGroup.DISCARD_PILE_X;
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

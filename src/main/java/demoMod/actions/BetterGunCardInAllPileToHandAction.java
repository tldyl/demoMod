package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.DemoMod;
import demoMod.cards.ConditionalReflex;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.interfaces.PostReloadSubscriber;
import demoMod.relics.CrisisStone;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "Duplicates"})
public class BetterGunCardInAllPileToHandAction extends AbstractGameAction {
    public static final String[] TEXT;
    private AbstractPlayer player;
    private int numberOfCards;
    private boolean optional;
    private boolean needReload;
    private CardGroup gunCardsInDiscard;
    private CardGroup gunCardsInDraw;
    private CardGroup gunCardsInTotal;

    public BetterGunCardInAllPileToHandAction(int numberOfCards, boolean optional, boolean needReload) {
        this.player = AbstractDungeon.player;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
        this.numberOfCards = numberOfCards;
        this.optional = optional;
        this.needReload = needReload;
        this.gunCardsInDiscard = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.gunCardsInDraw = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.gunCardsInTotal = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    }

    public BetterGunCardInAllPileToHandAction(int numberOfCards) {
        this(numberOfCards, false, true);
    }

    @Override
    public void update() {
        if (this.duration == this.startDuration) {
            if (!this.player.discardPile.isEmpty() || !this.player.drawPile.isEmpty() && this.numberOfCards > 0) {
                for (AbstractCard c : this.player.discardPile.group) {
                    if (c instanceof AbstractGunCard) {
                        gunCardsInDiscard.addToHand(c);
                    }
                }
                for (AbstractCard c : this.player.drawPile.group) {
                    if (c instanceof AbstractGunCard) {
                        gunCardsInDraw.addToHand(c);
                    }
                }
                if (gunCardsInDraw.group.size() <= 0 && gunCardsInDiscard.size() <= 0) {
                    this.isDone = true;
                    return;
                }
                gunCardsInTotal.group.addAll(gunCardsInDiscard.group);
                gunCardsInTotal.group.addAll(gunCardsInDraw.group);
                if (gunCardsInTotal.size() <= this.numberOfCards && !this.optional) {
                    ArrayList<AbstractCard> cardsToMove = new ArrayList<>(gunCardsInTotal.group);
                    for (AbstractCard c : cardsToMove) {
                        if (this.player.hand.size() < Settings.MAX_HAND_SIZE) {
                            this.player.hand.addToHand(c);
                            if (this.player.discardPile.contains(c)) {
                                this.player.discardPile.removeCard(c);
                            } else {
                                this.player.drawPile.removeCard(c);
                            }
                            if (needReload) {
                                AbstractGunCard gunCard = (AbstractGunCard) c;
                                gunCard.target = gunCard.defaultTarget;
                                gunCard.isReload = false;
                                if (this.player.hasRelic(DemoMod.makeID("CrisisStone"))) {
                                    if (gunCard.capacity <= 0) {
                                        ((CrisisStone) this.player.getRelic(DemoMod.makeID("CrisisStone"))).onReload(1);
                                    }
                                }
                                gunCard.reload();
                                gunCard.superFlash();
                            }
                        } else {
                            this.player.createHandIsFullDialog();
                        }
                    }
                    this.isDone = true;
                } else {
                    if (this.numberOfCards == 1) {
                        if (this.optional) {
                            AbstractDungeon.gridSelectScreen.open(gunCardsInTotal, this.numberOfCards, true, TEXT[0]);
                        } else {
                            AbstractDungeon.gridSelectScreen.open(gunCardsInTotal, this.numberOfCards, TEXT[0], false);
                        }
                    } else if (this.optional) {
                        AbstractDungeon.gridSelectScreen.open(gunCardsInTotal, this.numberOfCards, true, TEXT[1] + this.numberOfCards + TEXT[2]);
                    } else {
                        AbstractDungeon.gridSelectScreen.open(gunCardsInTotal, this.numberOfCards, TEXT[1] + this.numberOfCards + TEXT[2], false);
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
                        this.player.hand.addToHand(c);
                        if (this.player.discardPile.contains(c)) {
                            this.player.discardPile.removeCard(c);
                        } else {
                            this.player.drawPile.removeCard(c);
                        }
                        if (needReload) {
                            AbstractGunCard gunCard = (AbstractGunCard) c;
                            gunCard.target = gunCard.defaultTarget;
                            gunCard.isReload = false;
                            if (this.player.hasRelic(DemoMod.makeID("CrisisStone"))) {
                                if (gunCard.capacity <= 0) {
                                    ((CrisisStone) this.player.getRelic(DemoMod.makeID("CrisisStone"))).onReload(1);
                                }
                            }

                            gunCard.reload();
                            gunCard.superFlash();
                        }
                    } else {
                        this.player.createHandIsFullDialog();
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

package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

public class SniperShotAction extends AbstractGameAction {
    public static final String[] TEXT;
    private AbstractPlayer p;
    private CardGroup gunCardsInDiscard;

    public SniperShotAction() {
        this.p = AbstractDungeon.player;
        this.gunCardsInDiscard = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (this.duration == this.startDuration) {
            if (!this.p.discardPile.isEmpty()) {
                for (AbstractCard c : this.p.discardPile.group) {
                    if (c instanceof AbstractGunCard) {
                        gunCardsInDiscard.addToHand(c);
                    }
                }
                if (gunCardsInDiscard.size() == 1) {
                    if (this.p.hand.size() < Settings.MAX_HAND_SIZE) {
                        this.p.hand.addToHand(gunCardsInDiscard.group.get(0));
                        if (!AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                            AbstractMonster m = AbstractDungeon.getRandomMonster();
                            if (gunCardsInDiscard.group.get(0).target == AbstractCard.CardTarget.ALL_ENEMY) {
                                gunCardsInDiscard.group.get(0).calculateCardDamage(null);
                            } else {
                                gunCardsInDiscard.group.get(0).calculateCardDamage(m);
                            }
                            gunCardsInDiscard.group.get(0).use(p, m);
                        }
                    }
                    this.isDone = true;
                } else if (gunCardsInDiscard.size() == 0) {
                    this.isDone = true;
                } else {
                    AbstractDungeon.gridSelectScreen.open(gunCardsInDiscard, 1, TEXT[0], false);
                }
                this.tickDuration();
            } else {
                this.isDone = true;
            }
        } else {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    if (this.p.hand.size() < Settings.MAX_HAND_SIZE) {
                        this.p.hand.addToHand(c);
                        this.p.discardPile.removeCard(c);
                        if (!AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                            AbstractMonster m = AbstractDungeon.getRandomMonster();
                            if (c.target == AbstractCard.CardTarget.ALL_ENEMY) {
                                c.calculateCardDamage(null);
                            } else {
                                c.calculateCardDamage(m);
                            }
                            c.use(p, m);
                        }
                        c.lighten(false);
                        c.unhover();
                    } else {
                        this.p.createHandIsFullDialog();
                    }
                }
                for (AbstractCard c : this.p.discardPile.group) {
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

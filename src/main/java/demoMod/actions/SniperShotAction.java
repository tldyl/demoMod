package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.monsters.LordOfTheJammed;

import java.util.Iterator;

@SuppressWarnings("Duplicates")
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
                        this.p.discardPile.moveToHand(gunCardsInDiscard.group.get(0));
                        if (!AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                            AbstractMonster m = AbstractDungeon.getRandomMonster(AbstractDungeon.getCurrRoom().monsters.getMonster(LordOfTheJammed.ID));
                            if (gunCardsInDiscard.group.get(0).target == AbstractCard.CardTarget.ALL_ENEMY) {
                                gunCardsInDiscard.group.get(0).calculateCardDamage(null);
                            } else {
                                gunCardsInDiscard.group.get(0).calculateCardDamage(m);
                            }

                            AbstractCard c = gunCardsInDiscard.group.get(0);
                            c.use(p, m);
                            addToBot(new UseCardAction(c, m) {
                                @Override
                                public void update() {
                                    if (duration == 0.15F) {
                                        Iterator var1 = AbstractDungeon.player.powers.iterator();

                                        while (var1.hasNext()) {
                                            AbstractPower p = (AbstractPower) var1.next();
                                            if (!c.dontTriggerOnUseCard) {
                                                p.onAfterUseCard(c, this);
                                            }
                                        }

                                        var1 = AbstractDungeon.getMonsters().monsters.iterator();

                                        while (var1.hasNext()) {
                                            AbstractMonster m = (AbstractMonster) var1.next();

                                            for (AbstractPower p : m.powers) {
                                                if (!c.dontTriggerOnUseCard) {
                                                    p.onAfterUseCard(c, this);
                                                }
                                            }
                                        }

                                        c.freeToPlayOnce = false;
                                        c.isInAutoplay = false;
                                        if (c.purgeOnUse) {
                                            this.addToTop(new ShowCardAndPoofAction(c));
                                            this.isDone = true;
                                            AbstractDungeon.player.cardInUse = null;
                                            return;
                                        }

                                        if (c.type == AbstractCard.CardType.POWER) {
                                            this.addToTop(new ShowCardAction(c));
                                            if (Settings.FAST_MODE) {
                                                this.addToTop(new WaitAction(0.1F));
                                            } else {
                                                this.addToTop(new WaitAction(0.7F));
                                            }

                                            AbstractDungeon.player.hand.empower(c);
                                            this.isDone = true;
                                            AbstractDungeon.player.hand.applyPowers();
                                            AbstractDungeon.player.hand.glowCheck();
                                            AbstractDungeon.player.cardInUse = null;
                                            return;
                                        }

                                        AbstractDungeon.player.cardInUse = null;
                                        boolean spoonProc = false;
                                        if (this.exhaustCard && AbstractDungeon.player.hasRelic("Strange Spoon") && c.type != AbstractCard.CardType.POWER) {
                                            spoonProc = AbstractDungeon.cardRandomRng.randomBoolean();
                                        }

                                        if (this.exhaustCard && !spoonProc) {
                                            AbstractDungeon.player.hand.moveToExhaustPile(c);
                                            CardCrawlGame.dungeon.checkForPactAchievement();
                                        } else {
                                            if (spoonProc) {
                                                AbstractDungeon.player.getRelic("Strange Spoon").flash();
                                            }
                                        }

                                        c.exhaustOnUseOnce = false;
                                        c.dontTriggerOnUseCard = false;
                                        this.addToBot(new HandCheckAction());
                                    }
                                    tickDuration();
                                }
                            });
                            //AbstractDungeon.actionManager.phase = GameActionManager.Phase.WAITING_ON_USER;
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
                        this.p.discardPile.moveToHand(c);
                        if (!AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                            AbstractMonster m = AbstractDungeon.getRandomMonster(AbstractDungeon.getCurrRoom().monsters.getMonster(LordOfTheJammed.ID));
                            if (c.target == AbstractCard.CardTarget.ALL_ENEMY) {
                                c.calculateCardDamage(null);
                            } else {
                                c.calculateCardDamage(m);
                            }
                            c.use(p, m);
                            addToBot(new UseCardAction(c, m) {
                                @Override
                                public void update() {
                                    if (duration == 0.15F) {
                                        Iterator var1 = AbstractDungeon.player.powers.iterator();

                                        while (var1.hasNext()) {
                                            AbstractPower p = (AbstractPower) var1.next();
                                            if (!c.dontTriggerOnUseCard) {
                                                p.onAfterUseCard(c, this);
                                            }
                                        }

                                        var1 = AbstractDungeon.getMonsters().monsters.iterator();

                                        while (var1.hasNext()) {
                                            AbstractMonster m = (AbstractMonster) var1.next();

                                            for (AbstractPower p : m.powers) {
                                                if (!c.dontTriggerOnUseCard) {
                                                    p.onAfterUseCard(c, this);
                                                }
                                            }
                                        }

                                        c.freeToPlayOnce = false;
                                        c.isInAutoplay = false;
                                        if (c.purgeOnUse) {
                                            this.addToTop(new ShowCardAndPoofAction(c));
                                            this.isDone = true;
                                            AbstractDungeon.player.cardInUse = null;
                                            return;
                                        }

                                        if (c.type == AbstractCard.CardType.POWER) {
                                            this.addToTop(new ShowCardAction(c));
                                            if (Settings.FAST_MODE) {
                                                this.addToTop(new WaitAction(0.1F));
                                            } else {
                                                this.addToTop(new WaitAction(0.7F));
                                            }

                                            AbstractDungeon.player.hand.empower(c);
                                            this.isDone = true;
                                            AbstractDungeon.player.hand.applyPowers();
                                            AbstractDungeon.player.hand.glowCheck();
                                            AbstractDungeon.player.cardInUse = null;
                                            return;
                                        }

                                        AbstractDungeon.player.cardInUse = null;
                                        boolean spoonProc = false;
                                        if (this.exhaustCard && AbstractDungeon.player.hasRelic("Strange Spoon") && c.type != AbstractCard.CardType.POWER) {
                                            spoonProc = AbstractDungeon.cardRandomRng.randomBoolean();
                                        }

                                        if (this.exhaustCard && !spoonProc) {
                                            AbstractDungeon.player.hand.moveToExhaustPile(c);
                                            CardCrawlGame.dungeon.checkForPactAchievement();
                                        } else {
                                            if (spoonProc) {
                                                AbstractDungeon.player.getRelic("Strange Spoon").flash();
                                            }
                                        }

                                        c.exhaustOnUseOnce = false;
                                        c.dontTriggerOnUseCard = false;
                                        this.addToBot(new HandCheckAction());
                                    }
                                    tickDuration();
                                }
                            });
                            c.lighten(false);
                            c.unhover();
                        }
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
                //AbstractDungeon.actionManager.phase = GameActionManager.Phase.WAITING_ON_USER;
            }
            this.tickDuration();
        }
    }

    static {
        TEXT = CardCrawlGame.languagePack.getUIString(DemoMod.makeID("BetterGunCardInDiscardPileToHandAction")).TEXT;
    }
}

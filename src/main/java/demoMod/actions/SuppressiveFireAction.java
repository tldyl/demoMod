package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.powers.SuppressiveFirePower;

import java.util.ArrayList;
import java.util.List;

public class SuppressiveFireAction extends AbstractGameAction {
    private boolean upgraded;
    private static final UIStrings uiStrings;
    private List<AbstractCard> otherCards = new ArrayList<>();
    private AbstractPlayer p;
    public static final String[] TEXT;

    public SuppressiveFireAction(boolean upgraded) {
        this.upgraded = upgraded;
        this.duration = 0.25F;
        this.p = AbstractDungeon.player;
    }

    @Override
    public void update() {
        if (!this.upgraded) {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                boolean hasGun = false;
                for (AbstractCard card : AbstractDungeon.player.hand.group) {
                    if (card instanceof AbstractGunCard) {
                        hasGun = true;
                        break;
                    }
                }
                if (!hasGun) {
                    this.isDone = true;
                    return;
                }
                for (AbstractCard card : AbstractDungeon.player.hand.group) {
                    if (!(card instanceof AbstractGunCard)) {
                        otherCards.add(card);
                    }
                }
                if (this.p.hand.group.size() - this.otherCards.size() == 1) { //如果手里只有一张牌是枪
                    for (AbstractCard c : this.p.hand.group) {
                        if (c instanceof AbstractGunCard) {
                            AbstractGunCard gunCard = (AbstractGunCard)c;
                            gunCard.isSemiAutomaticForTurn = true;
                            gunCard.superFlash();
                            this.addToBot(new ApplyPowerAction(p, p, new SuppressiveFirePower(gunCard)));
                            this.isDone = true;
                            return;
                        }
                    }
                }
                this.p.hand.group.removeAll(this.otherCards);
                if (this.p.hand.group.size() > 1) {
                    AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false, false, false, false);
                    this.tickDuration();
                    return;
                }

                if (this.p.hand.group.size() == 1) {
                    this.returnCards();
                    this.isDone = true;
                }
            }

            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                this.returnCards();
                for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                    AbstractGunCard gunCard = (AbstractGunCard)c;
                    gunCard.isSemiAutomaticForTurn = true;
                    this.p.hand.addToTop(gunCard);
                    gunCard.superFlash();
                    this.addToBot(new ApplyPowerAction(p, p, new SuppressiveFirePower(gunCard)));
                }
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
                this.isDone = true;
            }

            this.tickDuration();
        } else {
            for (AbstractCard card : AbstractDungeon.player.hand.group) {
                if (card instanceof AbstractGunCard) {
                    AbstractGunCard gunCard = (AbstractGunCard) card;
                    gunCard.isSemiAutomaticForTurn = true;
                }
            }
            this.addToBot(new ApplyPowerAction(p, p, new SuppressiveFirePower(true)));
            this.isDone = true;
        }
    }

    private void returnCards() {
        for (AbstractCard c : this.otherCards) {
            this.p.hand.addToTop(c);
        }
        this.p.hand.refreshHandLayout();
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(DemoMod.makeID("SuppressiveFireAction"));
        TEXT = uiStrings.TEXT;
    }
}

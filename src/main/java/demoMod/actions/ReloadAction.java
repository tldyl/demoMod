package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.DemoMod;
import demoMod.cards.ConditionalReflex;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.interfaces.PostReloadSubscriber;
import demoMod.powers.SkilledReloadPower;
import demoMod.relics.CrisisStone;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class ReloadAction extends AbstractGameAction {
    public static final String ID = DemoMod.makeID("ReloadAction");
    private static final UIStrings uiStrings;
    private AbstractPlayer p;
    private ArrayList<AbstractCard> cannotReload = new ArrayList<>();
    public static final String[] TEXT;

    public ReloadAction() {
        this(1);
    }

    public ReloadAction(int amount) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = 0.25F;
        this.p = AbstractDungeon.player;
        this.amount = amount;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            for (AbstractCard c : this.p.hand.group) {
                if (!this.isReloadable(c)) {
                    this.cannotReload.add(c);
                }
            }

            if (this.cannotReload.size() == this.p.hand.group.size()) { //如果手里没有牌能被填装
                this.isDone = true;
                return;
            }

            if (this.p.hand.group.size() - this.cannotReload.size() == 1) { //如果手里只有一张牌能被填装
                for (AbstractCard c : this.p.hand.group) {
                    if (this.isReloadable(c)) {
                        AbstractGunCard gunCard = (AbstractGunCard)c;
                        gunCard.target = gunCard.defaultTarget;
                        gunCard.isReload = false;
                        if (this.p.hasRelic(DemoMod.makeID("CrisisStone"))) {
                            if (gunCard.capacity <= 0) {
                                ((CrisisStone)p.getRelic(DemoMod.makeID("CrisisStone"))).onReload(1);
                            }
                        }
                        gunCard.reload();
                        gunCard.superFlash();
                        this.isDone = true;
                        return;
                    }
                }
            }

            this.p.hand.group.removeAll(this.cannotReload);
            if (this.p.hand.group.size() > 1) {
                AbstractDungeon.handCardSelectScreen.open(TEXT[0], this.amount, true, true, false, false);
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
                gunCard.target = gunCard.defaultTarget;
                gunCard.isReload = false;
                if (this.p.hasRelic(DemoMod.makeID("CrisisStone"))) {
                    if (gunCard.capacity <= 0) {
                        ((CrisisStone)p.getRelic(DemoMod.makeID("CrisisStone"))).onReload(1);
                    }
                }
                gunCard.reload();
                this.p.hand.addToTop(gunCard);
                gunCard.superFlash();
            }
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            this.isDone = true;
        }

        this.tickDuration();
    }

    private void returnCards() {
        for (AbstractCard c : this.cannotReload) {
            this.p.hand.addToTop(c);
        }
        this.p.hand.refreshHandLayout();
    }

    private boolean isReloadable(AbstractCard card) {
        return card instanceof AbstractGunCard;
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(DemoMod.makeID("ReloadAction"));
        TEXT = uiStrings.TEXT;
    }
}

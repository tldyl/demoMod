package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.sounds.DemoSoundMaster;

import java.util.ArrayList;

public class EjectAction extends AbstractGameAction {

    private static final UIStrings uiStrings;
    private AbstractPlayer p;
    private ArrayList<AbstractCard> cannotEject = new ArrayList<>();
    public static final String[] TEXT;

    public EjectAction(int amount) {
        this.p = AbstractDungeon.player;
        this.duration = 0.25F;
        this.amount = amount;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            for (AbstractCard c : this.p.hand.group) {
                if (!this.isEjectable(c)) {
                    this.cannotEject.add(c);
                }
            }

            if (this.cannotEject.size() == this.p.hand.group.size()) { //如果手里没有牌能被退弹
                this.isDone = true;
                return;
            }

            if (this.p.hand.group.size() - this.cannotEject.size() == 1) { //如果手里只有一张牌能被退弹
                for (AbstractCard c : this.p.hand.group) {
                    if (this.isEjectable(c)) {
                        AbstractGunCard gunCard = (AbstractGunCard)c;
                        gunCard.clearAmmo();
                        gunCard.superFlash();
                        DemoSoundMaster.playA("GUN_RELOAD_BIG_SHOTGUN", 0.0F);
                        this.isDone = true;
                        return;
                    }
                }
            }

            this.p.hand.group.removeAll(this.cannotEject);
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
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                AbstractGunCard gunCard = (AbstractGunCard)c;
                gunCard.clearAmmo();
                this.p.hand.addToTop(gunCard);
                gunCard.superFlash();
            }
            DemoSoundMaster.playA("GUN_RELOAD_BIG_SHOTGUN", 0.0F);
            this.returnCards();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            this.isDone = true;
        }

        this.tickDuration();
    }

    private void returnCards() {
        for (AbstractCard c : this.cannotEject) {
            this.p.hand.addToTop(c);
        }
        this.p.hand.refreshHandLayout();
    }

    private boolean isEjectable(AbstractCard card) {
        if (!(card instanceof AbstractGunCard)) {
            return false;
        } else {
            AbstractGunCard gunCard = (AbstractGunCard) card;
            return gunCard.capacity > 0;
        }
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(DemoMod.makeID("EjectAction"));
        TEXT = uiStrings.TEXT;
    }
}

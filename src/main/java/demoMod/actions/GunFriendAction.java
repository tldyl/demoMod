package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.powers.GunFriendPower;

import java.util.ArrayList;

public class GunFriendAction extends AbstractGameAction {
    public static final String[] TEXT;
    private AbstractPlayer player;

    private ArrayList<AbstractCard> nonGun = new ArrayList<>();

    public GunFriendAction() {
        this.player = AbstractDungeon.player;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            for (AbstractCard c : this.player.hand.group) {
                if (!this.isGun(c)) {
                    this.nonGun.add(c);
                }
            }

            if (this.nonGun.size() == this.player.hand.group.size()) {
                this.isDone = true;
                return;
            }

            if (this.player.hand.group.size() - this.nonGun.size() == 1) {
                for (AbstractCard c : this.player.hand.group) {
                    if (this.isGun(c)) {
                        AbstractGunCard gunCard = (AbstractGunCard)c;
                        addToBot(new ApplyPowerAction(player, player, new GunFriendPower(1, gunCard)));
                        this.isDone = true;
                        return;
                    }
                }
            }

            this.player.hand.group.removeAll(this.nonGun);
            if (this.player.hand.group.size() > 1) {
                AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false, false, false, false);
                this.tickDuration();
                return;
            }

            if (this.player.hand.group.size() == 1) {
                this.returnCards();
                this.isDone = true;
            }
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            this.returnCards();
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                AbstractGunCard gunCard = (AbstractGunCard)c;
                addToBot(new ApplyPowerAction(player, player, new GunFriendPower(1, gunCard)));
                this.player.hand.addToTop(gunCard);
                gunCard.superFlash();
            }
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            this.isDone = true;
        }

        this.tickDuration();
    }

    private boolean isGun(AbstractCard card) {
        return card instanceof AbstractGunCard;
    }

    private void returnCards() {
        for (AbstractCard c : this.nonGun) {
            this.player.hand.addToTop(c);
        }
        this.player.hand.refreshHandLayout();
    }

    static {
        TEXT = CardCrawlGame.languagePack.getUIString(DemoMod.makeID("GunFriendAction")).TEXT;
    }
}

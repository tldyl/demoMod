package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.SkipEnemiesTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;

public class SuperHotWatch extends CustomRelic {
    public static final String ID = DemoMod.makeID("SuperHotWatch");
    public static final String IMG_PATH = "relics/superHotWatch.png";

    private boolean activated = true;
    private static final int THRESHOLD = 2;
    private boolean playCardThisTurn = false;

    public SuperHotWatch() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.RARE, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atTurnStart() {
        this.playCardThisTurn = false;
        this.activated = true;
        if (this.counter < THRESHOLD) {
            this.beginLongPulse();
        } else {
            this.stopPulse();
        }
    }

    public void atBattleStart() {
        this.counter = 0;
    }

    @Override
    public void onVictory() {
        this.counter = -1;
        this.stopPulse();
    }

    @Override
    public void onPlayerEndTurn() {
        if (activated && this.counter < THRESHOLD) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new SkipEnemiesTurnAction());
            this.counter++;
        } else if (this.counter >= THRESHOLD) {
            this.counter = 0;
            this.beginLongPulse();
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (playCardThisTurn) {
            this.activated = false;
            this.counter = 0;
            this.stopPulse();
        } else {
            playCardThisTurn = true;
        }
    }
}

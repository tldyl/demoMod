package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;

public class Unsteady extends CustomRelic {
    public static final String ID = DemoMod.makeID("Unsteady");
    public static final String IMG_PATH = "relics/unsteady.png";

    public Unsteady() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        this.counter = 0;
    }

    @Override
    public void onVictory() {
        this.counter = -1;
        this.stopPulse();
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction action) {
        this.counter++;
        if (this.counter == 10) {
            this.counter = 0;
            this.flash();
            this.pulse = false;
            this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.addToBot(new ExhaustSpecificCardAction(AbstractDungeon.player.hand.getRandomCard(true), AbstractDungeon.player.hand, true));
        } else if (this.counter == 9) {
            this.beginPulse();
            this.pulse = true;
        }
    }
}

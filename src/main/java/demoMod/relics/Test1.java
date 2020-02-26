package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.interfaces.PostReloadSubscriber;

public class Test1 extends CustomRelic implements PostReloadSubscriber {
    public static final String ID = DemoMod.makeID("Test1");
    public static final String IMG_PATH = "relics/strengthOfFortune.png";

    private boolean triggered = false;

    public Test1() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.BOSS, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atTurnStart() {
        triggered = false;
    }

    @Override
    public void onPlayerEndTurn() {
        triggered = true;
    }

    @Override
    public void onReload() {
        if (!triggered) {
            triggered = true;
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
            this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        }
    }
}

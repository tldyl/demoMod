package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.powers.PlayerFlightPower;

public class WaxWing extends CustomRelic {
    public static final String ID = DemoMod.makeID("WaxWing");
    public static final String IMG_PATH = "relics/waxWing.png";

    public WaxWing() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.BOSS, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        this.flash();
        AbstractPlayer p = AbstractDungeon.player;
        this.addToTop(new RelicAboveCreatureAction(p, this));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new PlayerFlightPower(p, 1)));
    }
}

package demoMod.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.NoBlockPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.DemoMod;
import demoMod.powers.PlayerFlightPower;

public class JetPack extends AbstractClickRelic {
    public static final String ID = DemoMod.makeID("JetPack");
    public static final String IMG_PATH = "relics/jetPack.png";

    private boolean enabled = false;
    private boolean activated = true;

    public JetPack() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.UNCOMMON, LandingSound.HEAVY);
        this.beginLongPulse();
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        enabled = true;
    }

    @Override
    public void atTurnStart() {
        enabled = true;
    }

    @Override
    public void onPlayerEndTurn() {
        enabled = false;
    }

    @Override
    public void onVictory() {
        enabled = false;
        activated = true;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new JetPack();
    }

    @Override
    protected void onRightClick() {
        if (enabled && activated) {
            this.flash();
            this.stopPulse();
            activated = false;
            AbstractPlayer p = AbstractDungeon.player;
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new PlayerFlightPower(p, 1)));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new NoBlockPower(p, 2, false)));
        }
    }
}

package demoMod.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import demoMod.DemoMod;

public class RingOfEthereal extends AbstractClickRelic {

    public static final String ID = DemoMod.makeID("RingOfEthereal");
    public static final String IMG_PATH = "relics/ringOfEthereal.png";
    private int maxCharge = 4;
    private boolean enabled = false;

    public RingOfEthereal() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.RARE, LandingSound.CLINK);
        this.counter = maxCharge;
        this.beginLongPulse();
    }

    @Override
    public void onVictory() {
        if (this.counter < maxCharge) {
            this.counter++;
        } else {
            if (!this.pulse) this.beginLongPulse();
        }
    }

    @Override
    public void atBattleStart() {
        DemoMod.canSteal = false;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new RingOfEthereal();
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
    protected void onRightClick() {
        if (this.counter == maxCharge && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT
                && enabled) {
            this.flash();
            AbstractPlayer p = AbstractDungeon.player;

            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new IntangiblePlayerPower(p, 1)));
            this.stopPulse();
            this.counter = 0;
        }
        if (this.counter == maxCharge && AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
            this.flash();
            this.stopPulse();
            DemoMod.canSteal = true;
            this.counter = 0;
        }
    }
}

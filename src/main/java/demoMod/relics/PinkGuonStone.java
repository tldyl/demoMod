package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;

public class PinkGuonStone extends CustomRelic {
    public static final String ID = DemoMod.makeID("PinkGuonStone");
    public static final String IMG_PATH = "relics/pinkGuonStone.png";

    public PinkGuonStone() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.UNCOMMON, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onPlayerEndTurn() {
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, 2));
    }

    @Override
    public void onEquip() {
        this.flash();
        AbstractDungeon.player.increaseMaxHp(7, true);
    }
}

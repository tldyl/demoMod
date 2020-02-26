package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;

public class RedGuonStone extends CustomRelic {
    public static final String ID = DemoMod.makeID("RedGuonStone");
    public static final String IMG_PATH = "relics/redGuonStone.png";

    public RedGuonStone() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.COMMON, LandingSound.HEAVY);
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
}

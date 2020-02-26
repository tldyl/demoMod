package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.potions.BlankPotion;
import demoMod.interfaces.PostEnterNewActSubscriber;

public class WhiteGuonStone extends CustomRelic implements PostEnterNewActSubscriber {
    public static final String ID = DemoMod.makeID("WhiteGuonStone");
    public static final String IMG_PATH = "relics/whiteGuonStone.png";

    public WhiteGuonStone() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.obtainPotion(new BlankPotion());
    }

    @Override
    public void onPlayerEndTurn() {
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, 2));
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    @Override
    public void onEnterNewAct() {
        AbstractDungeon.player.obtainPotion(new BlankPotion());
    }
}

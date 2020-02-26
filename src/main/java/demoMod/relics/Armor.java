package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import demoMod.DemoMod;
import demoMod.actions.LoseRelicAction;
import demoMod.sounds.DemoSoundMaster;

public class Armor extends CustomRelic {
    public static final String ID = DemoMod.makeID("Armor");
    public static final String IMG_PATH = "relics/armor.png";

    public Armor() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.SPECIAL, LandingSound.CLINK);
        this.counter = 1;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public int onLoseHpLast(int damageAmount) {
        if (damageAmount > 0) {
            this.counter--;
            this.flash();
            DemoSoundMaster.playV("POTION_BLANK", 0.1F);
            if (this.counter <= 0) DemoMod.actionsQueue.add(new LoseRelicAction(this));
            return 0;
        }
        return damageAmount;
    }
}

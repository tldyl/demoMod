package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import demoMod.DemoMod;
import demoMod.interfaces.ConstantPrice;

public class GnawedKey extends CustomRelic implements ConstantPrice {
    public static final String ID = DemoMod.makeID("GnawedKey");
    public static final String IMG_PATH = "relics/gnawedKey.png";

    public GnawedKey() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public int getPrice() {
        return 115;
    }
}

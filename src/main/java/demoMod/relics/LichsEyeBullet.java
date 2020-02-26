package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import demoMod.DemoMod;
import demoMod.combo.ComboManager;
import demoMod.relics.interfaces.PostRemoveRelic;

public class LichsEyeBullet extends CustomRelic implements PostRemoveRelic {
    public static final String ID = DemoMod.makeID("LichsEyeBullet");
    public static final String IMG_PATH = "relics/lichsEyeBullet.png";
    public static final String OUTLINE_PATH = "relics/lichsEyeBulletOutline.png";

    public LichsEyeBullet() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                new Texture(DemoMod.getResourcePath(OUTLINE_PATH)),
                RelicTier.RARE, LandingSound.CLINK);
    }

    @Override
    public void onEquip() {
        ComboManager.detectCombo();
    }

    @Override
    public void onRemove() {
        ComboManager.detectCombo();
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }
}

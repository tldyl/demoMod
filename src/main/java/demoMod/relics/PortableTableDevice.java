package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.actions.ChooseOneTableTech;

public class PortableTableDevice extends CustomRelic {
    public static final String ID = DemoMod.makeID("PortableTableDevice");
    public static final String IMG_PATH = "relics/portableTableDevice.png";
    public static final String OUTLINE_PATH = "relics/portableTableDeviceOutline.png";

    public PortableTableDevice() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                new Texture(DemoMod.getResourcePath(OUTLINE_PATH)),
                RelicTier.SHOP, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        this.flash();
        this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        this.addToBot(new ChooseOneTableTech());
    }
}

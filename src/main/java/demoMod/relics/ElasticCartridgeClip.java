package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;

public class ElasticCartridgeClip extends CustomRelic {
    public static final String ID = DemoMod.makeID("ElasticCartridgeClip");
    public static final String IMG_PATH = "relics/elasticCartridgeClip.png";

    public ElasticCartridgeClip() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.BOSS, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        ++AbstractDungeon.player.energy.energyMaster;
    }

    @Override
    public void onUnequip() {
        --AbstractDungeon.player.energy.energyMaster;
    }
}

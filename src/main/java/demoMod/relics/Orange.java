package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.sounds.DemoSoundMaster;

public class Orange extends CustomRelic implements Combo {
    public static final String ID = DemoMod.makeID("Orange");
    public static final String IMG_PATH = "relics/orange.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/orange.png"));

    private boolean isRemoving = false;

    public Orange() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.RARE, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        ComboManager.detectComboInGame();
        DemoSoundMaster.playA("RELIC_ORANGE", 0.0F);
        AbstractDungeon.player.increaseMaxHp(12, true);
    }

    public void onUnequip() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {

    }

    @Override
    public void onComboDisabled(String comboId) {

    }

    @Override
    public boolean isRemoving() {
        return isRemoving;
    }

    @Override
    public Texture getComboPortrait() {
        return comboTexture;
    }

    static {
        ComboManager.addCombo(DemoMod.makeID("EnterTheFruitgeon"), Orange.class);
        ComboManager.addCombo(DemoMod.makeID("OrangerGuonStone"), Orange.class);
    }
}

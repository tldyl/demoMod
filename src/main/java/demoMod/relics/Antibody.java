package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;

public class Antibody extends CustomRelic implements Combo {
    public static final String ID = DemoMod.makeID("Antibody");
    public static final String IMG_PATH = "relics/antibody.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/antibody.png"));

    private static int chance = 50;

    private boolean isRemoving = false;

    public Antibody() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.RARE, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        ComboManager.detectComboInGame();
    }

    @Override
    public void onUnequip() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + chance + this.DESCRIPTIONS[1];
    }

    @Override
    public int onPlayerHeal(int healAmount) {
        int ran = AbstractDungeon.miscRng.random(99);
        if (ran < chance) {
            healAmount *= 2;
            this.flash();
        }
        return healAmount;
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
        ComboManager.addCombo(DemoMod.makeID("Antichamber"), Antibody.class);
        ComboManager.addCombo(DemoMod.makeID("PowerhouseOfTheCell"), Antibody.class);
    }
}

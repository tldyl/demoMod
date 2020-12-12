package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;

public class Plus1Bullets extends CustomRelic implements Combo {
    public static final String ID = DemoMod.makeID("Plus1Bullets");
    public static final String IMG_PATH = "relics/plus1Bullets.png";
    public static final String OUTLINE_IMG_PATH = "relics/plus1BulletsOutline.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/plus1Bullets.png"));

    private boolean isRemoving = false;

    public static boolean combos[] = new boolean[]{false, false, false, false, false, false, false, false};

    public Plus1Bullets() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                new Texture(DemoMod.getResourcePath(OUTLINE_IMG_PATH)),
                RelicTier.COMMON, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
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
    public float atDamageModify(float damage, AbstractCard c) {
        return c instanceof AbstractGunCard ? damage + 1.0F : damage;
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:BluerGuonStone":
                combos[0] = true;
                break;
            case "DemoMod:ClearerGuonStone":
                combos[1] = true;
                break;
            case "DemoMod:FriendToGunAndBullet":
                combos[2] = true;
                break;
            case "DemoMod:GreenerGuonStone":
                combos[3] = true;
                break;
            case "DemoMod:OrangerGuonStone":
                combos[4] = true;
                break;
            case "DemoMod:PinkerGuonStone":
                combos[5] = true;
                break;
            case "DemoMod:RedderGuonStone":
                combos[6] = true;
                break;
            case "DemoMod:WhiterGuonStone":
                combos[7] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:BluerGuonStone":
                combos[0] = false;
                break;
            case "DemoMod:ClearerGuonStone":
                combos[1] = false;
                break;
            case "DemoMod:FriendToGunAndBullet":
                combos[2] = false;
                break;
            case "DemoMod:GreenerGuonStone":
                combos[3] = false;
                break;
            case "DemoMod:OrangerGuonStone":
                combos[4] = false;
                break;
            case "DemoMod:PinkerGuonStone":
                combos[5] = false;
                break;
            case "DemoMod:RedderGuonStone":
                combos[6] = false;
                break;
            case "DemoMod:WhiterGuonStone":
                combos[7] = false;
                break;
        }
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
        ComboManager.addCombo(DemoMod.makeID("BluerGuonStone"), Plus1Bullets.class);
        ComboManager.addCombo(DemoMod.makeID("ClearerGuonStone:Plus1Bullets"), Plus1Bullets.class);
        ComboManager.addCombo(DemoMod.makeID("FriendToGunAndBullet"), Plus1Bullets.class);
        ComboManager.addCombo(DemoMod.makeID("GreenerGuonStone"), Plus1Bullets.class);
        ComboManager.addCombo(DemoMod.makeID("OrangerGuonStone:Plus1Bullets"), Plus1Bullets.class);
        ComboManager.addCombo(DemoMod.makeID("PinkerGuonStone"), Plus1Bullets.class);
        ComboManager.addCombo(DemoMod.makeID("RedderGuonStone"), Plus1Bullets.class);
        ComboManager.addCombo(DemoMod.makeID("WhiterGuonStone"), Plus1Bullets.class);
    }
}

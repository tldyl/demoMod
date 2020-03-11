package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.powers.StrengthOfCursePower;

public class SilverBullets extends CustomRelic implements Combo {
    public static final String ID = DemoMod.makeID("SilverBullets");
    public static final String IMG_PATH = "relics/silverBullets.png";
    public static final String OUTLINE_IMG_PATH = "relics/silverBulletsOutline.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/silverBullets.png"));
    private boolean isRemoving = false;
    private static boolean isCombo = false;

    public SilverBullets() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)), new Texture(DemoMod.getResourcePath(OUTLINE_IMG_PATH)),
                RelicTier.UNCOMMON, LandingSound.CLINK);
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
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        int tmp = (int) Math.floor(this.counter / 4.0);
        if (isCombo && tmp > 0) {
            this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, tmp)));
        }
    }

    @Override
    public void onMonsterDeath(AbstractMonster m) {
        if (isCombo) {
            if (m != null && m.hasPower(StrengthOfCursePower.POWER_ID)) {
                this.counter++;
            }
        }
    }

    static {
        ComboManager.addCombo(DemoMod.makeID("BlessingAndACurse"), SilverBullets.class);
    }

    @Override
    public String getItemId() {
        return ID;
    }

    private void enableCounter() {
        this.counter = 0;
    }

    private void disableCounter() {
        this.counter = -1;
    }

    @Override
    public void onComboActivated(String comboId) {
        if (!isCombo) ((SilverBullets)AbstractDungeon.player.getRelic(ID)).enableCounter();
        isCombo = true;
    }

    @Override
    public void onComboDisabled(String comboId) {
        isCombo = false;
        if (AbstractDungeon.player.getRelic(ID) == null) return;
        ((SilverBullets)AbstractDungeon.player.getRelic(ID)).disableCounter();
    }

    @Override
    public boolean isRemoving() {
        return isRemoving;
    }

    @Override
    public Texture getComboPortrait() {
        return comboTexture;
    }
}

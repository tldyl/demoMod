package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;

public class RedGuonStone extends CustomRelic implements Combo {
    public static final String ID = DemoMod.makeID("RedGuonStone");
    public static final String IMG_PATH = "relics/redGuonStone.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/redGuonStone.png"));
    private boolean isRemoving = false;
    private static boolean combo = false;

    public RedGuonStone() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.COMMON, LandingSound.HEAVY);
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
    public void onPlayerEndTurn() {
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, 2));
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount == 0) {
            if (combo) {
                this.flash();
                addToBot(new ApplyPowerAction(p, p, new VigorPower(p, 4)));
            }
        }
        return damageAmount;
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        combo = true;
    }

    @Override
    public void onComboDisabled(String comboId) {
        combo = false;
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
        ComboManager.addCombo(DemoMod.makeID("RedderGuonStone"), RedGuonStone.class);
    }
}

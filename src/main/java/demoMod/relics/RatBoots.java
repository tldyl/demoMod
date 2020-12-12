package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.interfaces.PostReloadSubscriber;

public class RatBoots extends CustomRelic implements PostReloadSubscriber,
                                                     Combo {
    public static final String ID = DemoMod.makeID("RatBoots");
    public static final String IMG_PATH = "relics/ratBoots.png";
    public static final String OUTLINE_IMG_PATH = "relics/ratBootsOutline.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/ratBoots.png"));

    private boolean isRemoving = false;

    public RatBoots() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                new Texture(DemoMod.getResourcePath(OUTLINE_IMG_PATH)),
                RelicTier.RARE, LandingSound.FLAT);
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
    public void atBattleStart() {
        AbstractPlayer p = AbstractDungeon.player;
        this.addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, 1)));
    }

    @Override
    public void onReload() {
        this.flash();
        AbstractPlayer p = AbstractDungeon.player;
        this.addToTop(new RelicAboveCreatureAction(p, this));
        this.addToBot(new GainBlockAction(p, 6));
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
        ComboManager.addCombo(DemoMod.makeID("ResourcefulIndeed"), RatBoots.class);
    }
}

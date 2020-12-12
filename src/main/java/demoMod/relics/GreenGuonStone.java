package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.relics.interfaces.PostBeforePlayerDeath;

public class GreenGuonStone extends CustomRelic implements PostBeforePlayerDeath, Combo {
    public static final String ID = DemoMod.makeID("GreenGuonStone");
    public static final String IMG_PATH = "relics/greenGuonStone.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/greenGuonStone.png"));
    private boolean isRemoving = false;
    private static boolean combo = false;

    public GreenGuonStone() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.RARE, LandingSound.SOLID);
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
    public void onPlayerEndTurn() {
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, 2));
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0) {
            if (damageAmount < p.currentHealth) {
                if (AbstractDungeon.miscRng.random(99) < 20) {
                    this.flash();
                    p.heal(6);
                    if (combo) {
                        p.gainGold(20);
                    }
                }
            }
        }
        return damageAmount;
    }

    @Override
    public void onNearDeath() {
        AbstractPlayer p = AbstractDungeon.player;
        if (AbstractDungeon.miscRng.random(99) < 50) {
            this.flash();
            p.heal(10);
            if (combo) {
                p.gainGold(20);
            }
        }
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
        ComboManager.addCombo(DemoMod.makeID("GreenerGuonStone"), GreenGuonStone.class);
    }
}

package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.powers.ControlPower;

public class PinkGuonStone extends CustomRelic implements Combo {
    public static final String ID = DemoMod.makeID("PinkGuonStone");
    public static final String IMG_PATH = "relics/pinkGuonStone.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/pinkGuonStone.png"));

    private boolean isRemoving = false;
    private static boolean combo = false;

    public PinkGuonStone() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.UNCOMMON, LandingSound.CLINK);
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
        if (combo) {
            AbstractPlayer p = AbstractDungeon.player;
            if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0
                    && !info.owner.hasPower(ControlPower.POWER_ID)
                    && info.owner instanceof AbstractMonster) {
                this.flash();
                addToBot(new ApplyPowerAction(info.owner, p, new ControlPower((AbstractMonster) info.owner)));
            }
        }
        return damageAmount;
    }

    @Override
    public void onEquip() {
        this.flash();
        AbstractDungeon.player.increaseMaxHp(7, true);
        ComboManager.detectComboInGame();
    }

    @Override
    public void onUnequip() {
        isRemoving = true;
        ComboManager.detectCombo();
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
        ComboManager.addCombo(DemoMod.makeID("PinkerGuonStone"), PinkGuonStone.class);
    }
}

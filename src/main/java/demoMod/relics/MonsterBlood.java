package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.monsters.Decoy;

public class MonsterBlood extends CustomRelic implements Combo {
    public static final String ID = DemoMod.makeID("MonsterBlood");
    public static final String IMG_PATH = "relics/monsterBlood.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/monsterBlood.png"));

    private boolean isRemoving = false;
    private boolean enabled = false;

    public MonsterBlood() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.COMMON, LandingSound.CLINK);
    }

    @Override
    public void onEquip() {
        ComboManager.detectComboInGame();
        AbstractDungeon.player.increaseMaxHp(7, true);
    }

    public void onUnequip() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public void atBattleStart() {
        this.enabled = true;
    }

    public void onVictory() {
        this.enabled = false;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (info.type == DamageInfo.DamageType.NORMAL && damageAmount > p.currentBlock && this.enabled) {
            this.flash();
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDeadOrEscaped() && !(m instanceof Decoy)) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new PoisonPower(m, p, 2)));
                }
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
        ComboManager.addCombo(DemoMod.makeID("NaturalSelection:MonsterBlood"), MonsterBlood.class);
        ComboManager.addCombo(DemoMod.makeID("DeadlyDistraction"), MonsterBlood.class);
    }
}

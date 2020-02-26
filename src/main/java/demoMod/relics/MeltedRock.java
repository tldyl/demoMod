package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.sounds.DemoSoundMaster;

public class MeltedRock extends CustomRelic implements Combo {
    public static final String ID = DemoMod.makeID("MeltedRock");
    public static final String IMG_PATH = "relics/meltedRock.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/meltedRock.png"));
    private static boolean combos[] = new boolean[]{false};

    private boolean isRemoving = false;

    public MeltedRock() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.UNCOMMON, LandingSound.HEAVY);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    private void setDescriptionAfterLoading() {
        this.description = this.DESCRIPTIONS[0];
        if (combos[0]) {
            this.description += this.DESCRIPTIONS[1];
        }
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void onEquip() {
        ComboManager.detectComboInGame();
    }

    @Override
    public void atBattleStart() {
        this.counter = 0;
    }

    @Override
    public void onVictory() {
        this.counter = -1;
    }

    @Override
    public void onMonsterDeath(AbstractMonster m) {
        this.counter++;
        if (this.counter < AbstractDungeon.getCurrRoom().monsters.monsters.size()) {
            this.flash();
            DemoSoundMaster.playV("RELIC_MELTED_ROCK", 0.1F);
            AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(AbstractDungeon.player, DamageInfo.createDamageMatrix(6, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE));
            if (combos[0]) {
                setDescriptionAfterLoading();
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, 8 * this.counter));
            }
        }
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
        combos[0] = true;
    }

    @Override
    public void onComboDisabled(String comboId) {
        combos[0] = false;
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
        ComboManager.addCombo(DemoMod.makeID("HumanShield"), MeltedRock.class);
    }
}

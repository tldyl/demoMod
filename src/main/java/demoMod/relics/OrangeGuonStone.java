package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.actions.OrangeGuonStoneDamageAction;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;

public class OrangeGuonStone extends CustomRelic implements Combo {
    public static final String ID = DemoMod.makeID("OrangeGuonStone");
    public static final String IMG_PATH = "relics/orangeGuonStone.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/orangeGuonStone.png"));
    private static boolean[] combos = new boolean[] {false, false};

    private boolean isRemoving = false;

    public OrangeGuonStone() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.UNCOMMON, LandingSound.SOLID);
    }

    private void setDescriptionAfterLoading() {
        this.description = this.DESCRIPTIONS[0];
        if (combos[0]) {
            this.description += this.DESCRIPTIONS[1];
        }
        if (combos[1]) {
            this.description += this.DESCRIPTIONS[2];
        }
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void onPlayerEndTurn() {
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, 2));
        int baseDamage = 5;
        if (combos[0]) baseDamage = 7;
        if (!combos[1]) {
            AbstractDungeon.actionManager.addToBottom(new DamageRandomEnemyAction(new DamageInfo(AbstractDungeon.player, baseDamage, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
        } else {
            AbstractMonster m = AbstractDungeon.getRandomMonster();
            if (m != null) AbstractDungeon.actionManager.addToBottom(new OrangeGuonStoneDamageAction(m, baseDamage));
        }
        setDescriptionAfterLoading();
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
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:EnterTheFruitgeon":
                combos[0] = true;
                break;
            case "DemoMod:OrangerGuonStone":
                combos[1] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:EnterTheFruitgeon":
                combos[0] = false;
                break;
            case "DemoMod:OrangerGuonStone":
                combos[1] = false;
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
        ComboManager.addCombo(DemoMod.makeID("EnterTheFruitgeon"), OrangeGuonStone.class);
        ComboManager.addCombo(DemoMod.makeID("OrangerGuonStone"), OrangeGuonStone.class);
        ComboManager.addCombo(DemoMod.makeID("OrangerGuonStone:Plus1Bullets"), OrangeGuonStone.class);
    }
}

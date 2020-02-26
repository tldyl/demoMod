package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.powers.SniperWoofPower;
import demoMod.sounds.DemoSoundMaster;

@SuppressWarnings("StringConcatenationInLoop")
public class Wolf extends CustomRelic implements Combo {
    public static final String ID = DemoMod.makeID("Wolf");
    public static final String IMG_PATH = "relics/wolf.png";
    public static final String OUTLINE_PATH = "relics/wolfOutline.png";
    private static boolean combos[] = new boolean[]{false, false, false};
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/wolf.png"));

    private boolean isRemoving = false;

    public Wolf() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)), new Texture(DemoMod.getResourcePath(OUTLINE_PATH)),
                RelicTier.BOSS, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    private void setDescriptionAfterLoading() {
        this.description = this.DESCRIPTIONS[0];
        for (int i=0;i<combos.length;i++) {
            if (combos[i]) {
                this.description += this.DESCRIPTIONS[i + 1];
            }
        }
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void atTurnStart() {
        if (combos[1]) {
            this.flash();
            AbstractPlayer p = AbstractDungeon.player;
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new SniperWoofPower()));
        }
    }

    @Override
    public void onPlayerEndTurn() {
        AbstractMonster m = AbstractDungeon.getRandomMonster();
        if (m != null) {
            this.flash();
            DemoSoundMaster.playV("RELIC_WOLF_BARK", 0.1F);
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new BiteEffect(m.hb.cX, m.hb.cY - 40.0F * Settings.scale, Color.SCARLET.cpy()), 0.3F));
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(m, 12, DamageInfo.DamageType.THORNS), AttackEffect.NONE));
            if (combos[0]) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, AbstractDungeon.player, new WeakPower(m, 1, false)));
            }
            setDescriptionAfterLoading();
        }
    }

    @Override
    public void playLandingSFX() {
        DemoSoundMaster.playV("RELIC_WOLF_LANDING_SOUND", 0.1F);
    }

    @Override
    public void obtain() {
        ComboManager.detectComboInGame();
        System.out.println("DemoMod:wolf obtained.");
        for (boolean combo : combos) {
            if (combo) {
                setDescriptionAfterLoading();
                break;
            }
        }
        for (int i = 0; i < AbstractDungeon.player.relics.size(); i++) {
            if (AbstractDungeon.player.relics.get(i).relicId.equals(DemoMod.makeID("Dog"))) {
                instantObtain(AbstractDungeon.player, i, true);
                break;
            }
        }
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

    public boolean canSpawn() {
        return AbstractDungeon.player.hasRelic(DemoMod.makeID("Dog"));
    }

    @Override
    public String getItemId() {
        return DemoMod.makeID("Wolf");
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:TheRedHood" :
                combos[0] = true;
                break;
            case "DemoMod:SniperWoof" :
                combos[1] = true;
                break;
            case "DemoMod:TeaForTwo" :
                combos[2] = true;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:TheRedHood" :
                combos[0] = false;
                break;
            case "DemoMod:SniperWoof" :
                combos[1] = false;
                break;
            case "DemoMod:TeaForTwo" :
                combos[2] = false;
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
        ComboManager.addCombo(DemoMod.makeID("TheRedHood"), Wolf.class);
        ComboManager.addCombo(DemoMod.makeID("SniperWoof"), Wolf.class);
        ComboManager.addCombo(DemoMod.makeID("TeaForTwo"), Wolf.class);
    }
}

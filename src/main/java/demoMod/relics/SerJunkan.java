package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.WhirlwindAction;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.ScreenOnFireEffect;
import demoMod.DemoMod;
import demoMod.actions.BetterAttackDamageRandomEnemyAction;
import demoMod.actions.LoseRelicAction;
import demoMod.actions.PlaySoundAction;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.monsters.Decoy;
import demoMod.powers.StrengthOfCursePower;
import demoMod.relics.interfaces.PostBeforePlayerDeath;

public class SerJunkan extends CustomRelic implements PostBeforePlayerDeath, Combo {
    public static final String ID = DemoMod.makeID("SerJunkan");
    public static final String IMG_PATH = "relics/serJunkan_0.png";
    private static Texture[] imgs = new Texture[9];
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/serJunkan.png"));
    private boolean isRemoving = false;

    public static int LEVEL = 0;

    public SerJunkan() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.UNCOMMON, LandingSound.FLAT);
        DemoMod.actionsQueue.add(new AbstractGameAction() {
            @Override
            public void update() {
                if (AbstractDungeon.player != null) {
                    onTrigger();
                    if (AbstractDungeon.player.hasRelic(GoldJunk.ID)) {
                        onTrigger(null);
                    }
                }
                isDone = true;
            }
        });
    }

    @Override
    public void onEquip() {
        updateLevel();
        if (AbstractDungeon.player.hasRelic(GoldJunk.ID)) {
            onTrigger(null);
        }
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
    public void onTrigger() {
        updateLevel();
    }

    @Override
    public void onTrigger(AbstractCreature target) { //捡到金垃圾时触发
        LEVEL = 8;
        this.img = imgs[LEVEL];
        this.counter = 0;
        setDescriptionAfterLoading();
    }

    private void updateLevel() {
        if (AbstractDungeon.player == null) return;
        if (AbstractDungeon.player.hasRelic(GoldJunk.ID)) return;
        LEVEL = 0;
        if (AbstractDungeon.player.hasRelic(Junk.ID) || AbstractDungeon.player.hasRelic("DemoExt:Lies")) {
            if (AbstractDungeon.player.hasRelic(Junk.ID)) {
                LEVEL = AbstractDungeon.player.getRelic(Junk.ID).counter;
            }
            if (AbstractDungeon.player.hasRelic("DemoExt:Lies")) {
                LEVEL++;
            }
        }
        if (LEVEL > 7) LEVEL = 7;
        this.img = imgs[LEVEL];
        setDescriptionAfterLoading();
    }

    private void setDescriptionAfterLoading() {
        this.description = this.DESCRIPTIONS[0] + this.DESCRIPTIONS[LEVEL + 1];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void atBattleStart() {
        if (LEVEL == 8 || LEVEL <= 2) {
            this.counter = 0;
        }
    }

    @Override
    public void atTurnStart() {
        if (LEVEL == 6) {
            this.counter++;
            if (this.counter > 3) {
                this.counter = 3;
                this.beginLongPulse();
            }
        }
    }

    @Override
    public int onLoseHpLast(int damageAmount) {
        if (LEVEL == 6) {
            if (this.counter >= 3 && damageAmount > 0) {
                this.flash();
                this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                this.addToTop(new PlaySoundAction("POTION_BLANK"));
                this.counter = 0;
                this.stopPulse();
                return 0;
            }
        }
        return damageAmount;
    }

    @Override
    public void onVictory() {
        if (LEVEL != 6) {
            this.counter = -1;
            this.stopPulse();
        }
    }

    @Override
    public void onPlayerEndTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        switch (LEVEL) {
            case 0:
                if (this.counter == 0) {
                    this.flash();
                    this.addToBot(new GainBlockAction(p, 4));
                    this.counter++;
                } else {
                    this.counter = 0;
                }
                break;
            case 1:
                if (this.counter == 0) {
                    this.flash();
                    this.addToBot(new GainBlockAction(p, 4));
                    this.counter++;
                } else {
                    AbstractMonster monster = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(AbstractDungeon.getCurrRoom().monsters.getMonster(Decoy.ID), true);
                    if (monster == null) break;
                    this.flash();
                    this.addToBot(new DamageAction(monster, new DamageInfo(p, 3, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                    this.counter = 0;
                }
                break;
            case 2:
                if (this.counter == 0) {
                    this.flash();
                    this.addToBot(new GainBlockAction(p, 5));
                    this.counter++;
                } else {
                    AbstractMonster monster = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(AbstractDungeon.getCurrRoom().monsters.getMonster(Decoy.ID), true);
                    if (monster == null) break;
                    this.flash();
                    this.addToBot(new DamageAction(monster, new DamageInfo(p, 5, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                    this.counter = 0;
                }
                break;
            case 3:
                AbstractMonster monster = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(AbstractDungeon.getCurrRoom().monsters.getMonster(Decoy.ID), true);
                if (monster == null) break;
                this.flash();
                this.addToBot(new GainBlockAction(p, 5));
                this.addToBot(new DamageAction(monster, new DamageInfo(p, 5, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
            case 4:
                monster = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(AbstractDungeon.getCurrRoom().monsters.getMonster(Decoy.ID), true);
                if (monster == null) break;
                this.flash();
                this.addToBot(new GainBlockAction(p, 5));
                this.addToBot(new DamageAction(monster, new DamageInfo(p, 9, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
            case 5:
                this.flash();
                this.addToBot(new GainBlockAction(p, 7));
                this.addToBot(new WhirlwindAction(p, DamageInfo.createDamageMatrix(6, true), DamageInfo.DamageType.THORNS, true, 2));
                break;
            case 6:
                int s = 0;
                if (p.hasPower(StrengthPower.POWER_ID)) {
                    s = p.getPower(StrengthPower.POWER_ID).amount;
                }
                if (s < 0) s = 0;
                this.flash();
                this.addToBot(new GainBlockAction(p, 7));
                this.addToBot(new WhirlwindAction(p, DamageInfo.createDamageMatrix(9 + s, true), DamageInfo.DamageType.THORNS, true, 2));
                break;
            case 7:
                s = 0;
                if (p.hasPower(StrengthPower.POWER_ID)) {
                    s = p.getPower(StrengthPower.POWER_ID).amount;
                }
                if (s < 0) s = 0;
                this.flash();
                for (int i=0;i<15;i++) {
                    this.addToBot(new BetterAttackDamageRandomEnemyAction(4 + s, AbstractGameAction.AttackEffect.FIRE, AbstractDungeon.getCurrRoom().monsters.getMonster(Decoy.ID)));
                }
                break;
            case 8:
                s = 0;
                if (p.hasPower(StrengthPower.POWER_ID)) {
                    s = p.getPower(StrengthPower.POWER_ID).amount;
                }
                if (s < 0) s = 0;
                this.flash();
                if (this.counter == 0) {
                    for (int i=0;i<15;i++) {
                        this.addToBot(new BetterAttackDamageRandomEnemyAction(2 + s, AbstractGameAction.AttackEffect.FIRE, AbstractDungeon.getCurrRoom().monsters.getMonster(Decoy.ID)));
                    }
                    for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                        if (m.hasPower(StrengthOfCursePower.POWER_ID)) {
                            StrengthOfCursePower power = (StrengthOfCursePower) m.getPower(StrengthOfCursePower.POWER_ID);
                            this.addToBot(new RemoveSpecificPowerAction(m, p, power));
                        }
                    }
                    this.counter++;
                } else if (this.counter == 1) {
                    AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(AbstractDungeon.getCurrRoom().monsters.getMonster(Decoy.ID), true);
                    if (m != null) {
                        this.addToBot(new LoseBlockAction(m, p, m.currentBlock));
                        this.addToBot(new DamageAction(m, new DamageInfo(p, 10 + s, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                        this.addToBot(new DamageAction(m, new DamageInfo(p, 10 + s, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                    }
                    this.counter++;
                } else {
                    this.addToBot(new VFXAction(p, new ScreenOnFireEffect(), 1.0F));
                    for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                        this.addToBot(new LoseBlockAction(m, p, m.currentBlock));
                    }
                    for (int i=0;i<7;i++) {
                        this.addToBot(new BetterAttackDamageRandomEnemyAction(8 + s, AbstractGameAction.AttackEffect.FIRE, AbstractDungeon.getCurrRoom().monsters.getMonster(Decoy.ID)));
                    }
                    this.counter = 0;
                }
                break;
        }
    }

    @Override
    public boolean canSpawn() {
        return Settings.isEndless || AbstractDungeon.floorNum <= 31;
    }

    static {
        for (int i=0;i<8;i++) {
            imgs[i] = new Texture(DemoMod.getResourcePath("relics/serJunkan_" + i + ".png"));
        }
        imgs[8] = new Texture(DemoMod.getResourcePath("relics/serJunkan_gold.png"));
    }

    @Override
    public void onNearDeath() {
        if (LEVEL == 6) {
            AbstractPlayer p = AbstractDungeon.player;
            this.flash();
            addToBot(new RelicAboveCreatureAction(p, this));
            p.heal(AbstractDungeon.player.maxHealth);
            DemoMod.actionsQueue.add(new WaitAction(2.0F));
            if (Settings.FAST_MODE) {
                for (int i=0;i<10;i++) {
                    DemoMod.actionsQueue.add(new WaitAction(2.0F));
                }
            }
            DemoMod.actionsQueue.add(new LoseRelicAction(this));
        }
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
        ComboManager.addCombo(DemoMod.makeID("TeaForTwo:SerJunkan"), SerJunkan.class);
    }
}

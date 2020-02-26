package demoMod.monsters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.AnimateHopAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import demoMod.DemoMod;

public class Mimic extends AbstractMonster {
    private static final String ID = DemoMod.makeID("Mimic");
    private static final MonsterStrings monsterStrings;
    private static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final int[] HP = new int[]{80, 120, 250};
    private static final int[] LV_ARMOR_MAX = new int[]{5, 10, 15};
    private static final int[] LV_ARMOR_GAIN = new int[]{4, 5, 5};
    private static final int[] LV_DOUBLESTRIKE_DMG = new int[]{2, 3, 4};
    private static final int[] LV_BIGSTRIKE_DMG = new int[]{9, 11, 13};
    private static final int[] LV_STR = new int[]{1, 1, 2};
    private static final int[] LV_DAZE = new int[]{2, 3, 4};
    private static final int[] LV_MAD = new int[]{2, 2, 3};
    private int lv;
    private int lid_armor_gain;
    private int lid_armor_max;
    private int lid_artifact;
    private int doubleStrike_dmg;
    private int bigHit_dmg;
    private int selfBuff_str;
    private int boo_dazed;
    private int mad_duration;
    private static final byte CLOSE_LID = 1;
    private static final byte DOUBLE_STRIKE = 2;
    private static final byte BLADE_MIMICRY = 3;
    private static final byte MAD_MIMICRY = 4;
    private static final byte BIG_HIT = 5;
    private static final byte BOO = 6;
    private int closeBlock;
    private boolean firstMove;
    private Mimic.MimicType mimType;

    public Mimic(MimicType mimType) {
        super(NAME, ID, 80, 0.0F, 0.0F, 200.0F, 200.0F, (String)null);
        int lv = 0;
        this.mimType = mimType;
        this.closeBlock = 0;
        switch(mimType) {
            case SMALL:
                this.setImage(ImageMaster.loadImage(DemoMod.getResourcePath("monsters/mimicSmall.png")), 295.0F, 153.0F);
                break;
            case MEDIUM:
                lv = 1;
                this.setImage(ImageMaster.loadImage(DemoMod.getResourcePath("monsters/mimicMedium.png")), 273.0F, 252.0F);
                break;
            case LARGE:
                lv = 2;
                this.setImage(ImageMaster.loadImage(DemoMod.getResourcePath("monsters/mimicLarge.png")), 453.0F, 317.0F);
        }

        if (AbstractDungeon.bossCount > 2) {
            lv = 2;
        }

        this.dialogX = 0.0F;
        this.dialogY = 0.0F;

        this.firstMove = true;
        this.lid_artifact = 1;
        this.lv = lv;
        if (AbstractDungeon.ascensionLevel >= 8) {
            this.setHp(HP[lv] - 2 + 15, HP[lv] - 2 + 15);
            if (this.lv > 0) {
                this.lid_artifact = 2;
            }

            this.closeBlock = this.lv * 2;
            if (AbstractDungeon.ascensionLevel >= 18) {
                this.closeBlock += 3;
            }
        } else {
            this.setHp(HP[lv] - 2, HP[lv] - 2);
        }

        if (AbstractDungeon.ascensionLevel >= 3) {
            this.doubleStrike_dmg = LV_DOUBLESTRIKE_DMG[lv] + 1;
            this.bigHit_dmg = LV_BIGSTRIKE_DMG[lv] + 2;
        } else {
            this.doubleStrike_dmg = LV_DOUBLESTRIKE_DMG[lv];
            this.bigHit_dmg = LV_BIGSTRIKE_DMG[lv];
        }

        this.lid_armor_gain = LV_ARMOR_GAIN[lv];
        this.lid_armor_max = LV_ARMOR_MAX[lv];
        this.mad_duration = LV_MAD[lv];
        this.boo_dazed = LV_DAZE[lv];
        this.selfBuff_str = LV_STR[lv];
        this.damage.add(new DamageInfo(this, this.doubleStrike_dmg));
        this.damage.add(new DamageInfo(this, this.bigHit_dmg));
    }

    private void setImage(Texture img, float hb_w, float hb_h) {
        this.img = img;
        this.hb_w = hb_w * Settings.scale;
        this.hb_h = hb_h * Settings.scale;
        this.hb = new Hitbox(this.hb_w, this.hb_h);
        this.healthHb = new Hitbox(this.hb_w, 72.0F * Settings.scale);
        this.refreshHitboxLocation();
        this.refreshIntentHbLocation();
    }

    @Override
    public void usePreBattleAction() {
        AbstractPower offGuard = new EntanglePower(AbstractDungeon.player);
        offGuard.name = DIALOG[0];
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, offGuard));
    }

    @Override
    public void takeTurn() {
        switch(this.nextMove) {
            case 1:
                if (this.firstMove && (this.mimType == Mimic.MimicType.SMALL || this.mimType == Mimic.MimicType.MEDIUM)) {
                    --this.lid_armor_gain;
                }

                if (this.hasPower("Plated Armor") && this.getPower("Plated Armor").amount + this.lid_armor_gain > this.lid_armor_max) {
                    if (this.getPower("Plated Armor").amount < this.lid_armor_max) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new PlatedArmorPower(this, this.lid_armor_max - this.getPower("Plated Armor").amount), this.lid_armor_max - this.getPower("Plated Armor").amount));
                    }
                } else {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new PlatedArmorPower(this, this.lid_armor_gain), this.lid_armor_gain));
                }

                if (this.firstMove && (this.mimType == Mimic.MimicType.SMALL || this.mimType == Mimic.MimicType.MEDIUM)) {
                    ++this.lid_armor_gain;
                }

                this.firstMove = false;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ArtifactPower(this, this.lid_artifact), this.lid_artifact));
                if (this.closeBlock > 0) {
                    AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.closeBlock));
                }

                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));

                for(int i = 0; i < 2; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new VFXAction(new BiteEffect(AbstractDungeon.player.hb.cX + MathUtils.random(-25.0F, 25.0F) * Settings.scale, AbstractDungeon.player.hb.cY + MathUtils.random(-25.0F, 25.0F) * Settings.scale, Color.GOLD.cpy()), 0.0F));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(0), AbstractGameAction.AttackEffect.NONE));
                }

                if (this.mimType == Mimic.MimicType.SMALL && AbstractDungeon.bossCount < 1) {
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false));
                } else {
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAndDeckAction(new Slimed()));
                }

                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.selfBuff_str), this.selfBuff_str));
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, this.mad_duration, true), this.mad_duration));
                if (AbstractDungeon.bossCount > 0) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, this.mad_duration, true), this.mad_duration));
                }
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                break;
            case 5:
                AbstractDungeon.actionManager.addToBottom(new AnimateHopAction(this));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.2F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
            case 6:
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Dazed(), this.boo_dazed));
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
        }
    }

    private void setMoveNow(byte nextTurn) {
        switch(nextTurn) {
            case 1:
                this.setMove(MOVES[0], nextTurn, this.closeBlock > 0 ? Intent.DEFEND_BUFF : Intent.BUFF);
                break;
            case 2:
                this.setMove(nextTurn, Intent.ATTACK_DEBUFF, (this.damage.get(0)).base, 2, true);
                break;
            case 3:
                this.setMove(MOVES[1], nextTurn, Intent.BUFF);
                break;
            case 4:
                this.setMove(MOVES[2], nextTurn, Intent.DEBUFF);
                break;
            case 5:
                this.setMove(nextTurn, Intent.ATTACK, (this.damage.get(1)).base);
                break;
            case 6:
                this.setMove(MOVES[3], nextTurn, Intent.STRONG_DEBUFF);
                break;
            default:
                this.setMove(nextTurn, Intent.NONE);
        }
    }

    @Override
    protected void getMove(int num) {
        if (this.firstMove) {
            this.setMoveNow((byte)1);
        } else if (this.lastMove((byte)1)) {
            this.setMoveNow((byte)2);
        } else if (!this.lastMove((byte)2)) {
            if (!this.lastMove((byte)3) && !this.lastMove((byte)4)) {
                if (this.lastMove((byte)5)) {
                    this.setMoveNow((byte)6);
                } else {
                    this.setMoveNow((byte)1);
                }
            } else {
                this.setMoveNow((byte)5);
            }
        } else {
            if (num <= 66 && (GameActionManager.turn <= 5 && (!this.hasPower("Strength") || this.getPower("Strength").amount >= 0) || num <= 33)) {
                this.setMoveNow((byte)4);
            } else {
                this.setMoveNow((byte)3);
            }
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(DemoMod.makeID("Mimic"));
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }

    public static enum MimicType {
        SMALL,
        MEDIUM,
        LARGE;

        private MimicType() {
        }
    }
}

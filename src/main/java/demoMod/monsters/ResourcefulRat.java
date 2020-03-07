package demoMod.monsters;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.interfaces.OnCardUseSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.combat.DaggerSprayEffect;
import demoMod.DemoMod;
import demoMod.cards.guns.Elimentaler;
import demoMod.cards.tempCards.RatTrap;
import demoMod.effects.BulletWaveEffect;
import demoMod.effects.RatJumpIntoEntryEffect;
import demoMod.patches.MonsterRoomPatch;
import demoMod.powers.*;
import demoMod.sounds.DemoSoundMaster;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourcefulRat extends AbstractMonster implements CustomSavable<Boolean>,
                                                               OnCardUseSubscriber {
    public static final String ID = DemoMod.makeID("ResourcefulRat");
    private static final MonsterStrings monsterStrings;
    public static String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final int HP_MAX = 148;
    private static final float HB_X = 0.0F;
    private static final float HB_Y = 10.0F;
    private static final float HB_W = 360.0F;
    private static final float HB_H = 240.0F;
    private int bodySlamDamage = 0;
    private static final String IMG_PATH = DemoMod.getResourcePath("monsters/resourcefulRat.png");
    public static boolean isBeaten = false;
    public static boolean isTrueBeaten = false;
    public static boolean phaseTwo = false;
    private static MovePackManager movePackManager;
    private boolean firstMove = true;

    private MovePack[] packs1;
    private MovePack[] packs2;

    private int tmpCtr = 0;

    public ResourcefulRat(float x, float y) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, IMG_PATH, x, y);
        this.type = EnemyType.BOSS;
        BaseMod.addSaveField("isBeaten", this);
        BaseMod.subscribe(this);
        initMovePack();
    }

    private void initMovePack() {
        packs1 = new MovePack[6];
        packs2 = new MovePack[9];
        packs1[0] = () -> {
            movePackManager.addMove(() -> setMove((byte) 1, Intent.DEFEND_BUFF));
            movePackManager.addMove(() -> setMove((byte) 1, Intent.DEFEND_BUFF));
            movePackManager.addMove(() -> setMove((byte) 1, Intent.DEFEND_BUFF));
            movePackManager.addMove(() -> setMove(MOVES[1], (byte) 2, Intent.ATTACK, bodySlamDamage));
            movePackManager.addMove(() -> {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new HighPressurePower()));
                setMove(MOVES[0], (byte) 3, Intent.ATTACK, 6, 2, true);
            });
        };
        packs1[1] = () -> movePackManager.addMove(() -> setMove((byte) 0, Intent.UNKNOWN));
        packs1[2] = () -> {
            movePackManager.addMove(() -> {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new HighPressurePower()));
                setMove(MOVES[0], (byte) 3, Intent.ATTACK_BUFF, 6, 2, true);
            });
            movePackManager.addMove(() -> {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new HighPressurePower()));
                setMove((byte) 3, Intent.ATTACK_BUFF, 6, 2, true);
            });
            movePackManager.addMove(() -> {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new HighPressurePower()));
                setMove((byte) 3, Intent.ATTACK_BUFF, 6, 2, true);
            });
        };
        packs1[3] = () -> {
            movePackManager.addMove(() -> setMove(MOVES[4], (byte) 4, Intent.ATTACK_DEBUFF, 14));
            movePackManager.addMove(() -> setMove((byte) 4, Intent.ATTACK_DEBUFF, 14));
            movePackManager.addMove(() -> setMove((byte) 4, Intent.ATTACK_DEBUFF, 14));
            movePackManager.addMove(() -> setMove((byte) 4, Intent.ATTACK_DEBUFF, 14));
            movePackManager.addMove(() -> {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new HighPressurePower()));
                setMove((byte) 3, Intent.ATTACK, 6, 2, true);
            });
        };
        packs1[4] = () -> movePackManager.addMove(() -> {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new HighPressurePower(true)));
            setMove(MOVES[3], (byte) 5, Intent.DEBUFF);
        });
        packs1[5] = () -> movePackManager.addMove(() -> setMove((byte) 6, Intent.ATTACK_DEBUFF, 3, 6, true));

        packs2[0] = () -> {
            movePackManager.addMove(() -> setMove((byte) 1, Intent.BUFF));
            movePackManager.addMove(() -> setMove((byte) 2, Intent.ATTACK, 15));
            movePackManager.addMove(() -> setMove((byte) 2, Intent.ATTACK, 15));
        };

        packs2[1] = () -> movePackManager.addMove(() -> setMove((byte) 3, Intent.ATTACK_DEBUFF, 6));

        packs2[2] = () -> movePackManager.addMove(() -> setMove((byte) 4, Intent.ATTACK_DEBUFF, 6, 4, true));

        packs2[3] = () -> movePackManager.addMove(() -> setMove((byte) 5, Intent.ATTACK, 9, 3, true));

        packs2[4] = () -> movePackManager.addMove(() -> setMove((byte) 9, Intent.UNKNOWN));

        packs2[5] = () -> movePackManager.addMove(() -> setMove(MOVES[5], (byte) 6, Intent.DEBUFF));

        packs2[6] = () -> movePackManager.addMove(() -> setMove((byte) 7, Intent.BUFF));

        packs2[7] = () -> {
            this.tmpCtr = 20;
            movePackManager.addMove(() -> setMove((byte) 8, Intent.ATTACK, 3, 10, true));
        };

        packs2[8] = () -> movePackManager.addMove(() -> setMove((byte) 10, Intent.DEFEND));
    }

    @Override
    public void usePreBattleAction() {
        this.damage = new ArrayList<>();
        this.damage.add(new DamageInfo(this, 6)); //匕首雨
        this.damage.add(new DamageInfo(this, bodySlamDamage)); //全身撞击
        this.damage.add(new DamageInfo(this, 14)); //甩大绳
        this.damage.add(new DamageInfo(this, 3)); //一圈奶酪
        this.damage.add(new DamageInfo(this, 15));
        this.damage.add(new DamageInfo(this, 9));
        this.damage.add(new DamageInfo(this, 3));
        CardCrawlGame.music.silenceTempBgmInstantly();
        AbstractDungeon.scene.fadeOutAmbiance();
        MonsterRoomPatch.entered = true;
        this.powers = new ArrayList<>();
        movePackManager.clearRemainingMoves();
        if (isBeaten) {
            AbstractDungeon.getCurrRoom().rewardAllowed = true;
            this.maxHealth = 351;
            this.currentHealth = this.maxHealth;
            MonsterRoomPatch.PatchRender.enabled = false;
            phaseTwo = true;
            this.img = new Texture(DemoMod.getResourcePath("monsters/resourcefulRat/idle/idle_scale_1.3.png"));
        } else {
            AbstractDungeon.getCurrRoom().rewardAllowed = false;
            AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_RESOURCEFUL_RAT_1");
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new AfterImageMonsterPower(this, 2)));
            AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[AbstractDungeon.miscRng.random(0, 1)], 0.5F, 4.0F));
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (m != this) {
                    this.addToBot(new StunMonsterAction(m, this));
                }
            }
            AbstractDungeon.actionManager.addToBottom(new PressEndTurnButtonAction());
            MonsterRoomPatch.PatchRender.enabled = true;
            phaseTwo = false;
        }
    }

    public void addBlock(int amount) {
        super.addBlock(amount);
        if (amount > 0) {
            if (!isBeaten && this.nextMove == 2) {
                bodySlamDamage = this.currentBlock;
                setMove((byte) 2, Intent.ATTACK, bodySlamDamage);
                this.damage.get(1).base = bodySlamDamage;
                this.createIntent();
            }
        }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (!isBeaten && this.nextMove == 2) {
            bodySlamDamage = this.currentBlock;
            setMove((byte) 2, Intent.ATTACK, bodySlamDamage);
            this.damage.get(1).base = bodySlamDamage;
            this.createIntent();
        }
    }

    @Override
    public void takeTurn() {
        if (!isBeaten) {
            switch (this.nextMove) {
                case 0:
                    int amount = AbstractDungeon.player.drawPile.size() / 5;
                    if (amount < 1) amount = 1;
                    AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, 30));
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new RatTrap(), amount, true, true));
                    break;
                case 1:
                    AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, 30));
                    if (!this.hasPower(BarricadePower.POWER_ID)) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BarricadePower(this)));
                    }
                    break;
                case 2:
                    AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, BarricadePower.POWER_ID));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                    break;
                case 3:
                    this.addToBot(new VFXAction(new DaggerSprayEffect(true), 0.0F));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.NONE));
                    this.addToBot(new VFXAction(new DaggerSprayEffect(true), 0.0F));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.NONE));
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, 1)));
                    break;
                case 4:
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                    int dmg = this.damage.get(2).output;
                    for (AbstractPower power : AbstractDungeon.player.powers) {
                        dmg = power.onAttacked(this.damage.get(2), dmg);
                    }
                    if (dmg > AbstractDungeon.player.currentBlock) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 2, true)));
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 2, true)));
                    }
                    break;
                case 5:
                    if (HighPressurePower.isEndByThis) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new PoisonPower(AbstractDungeon.player, this, 4)));
                        HighPressurePower.isEndByThis = false;
                    }
                    break;
                case 6:
                    int block = AbstractDungeon.player.currentBlock;
                    dmg = this.damage.get(3).output;
                    int totalDamage = 0;
                    for (int i=0;i<6;i++) {
                        AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(3), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                        for (AbstractPower power : AbstractDungeon.player.powers) {
                            dmg = power.onAttacked(this.damage.get(3), dmg);
                        }
                        totalDamage += dmg;
                    }
                    if (totalDamage > block * 1.5) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new SlowPower(AbstractDungeon.player, 0)));
                    }
                    break;
            }
        } else {
            switch (this.nextMove) {
                case 1:
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new HomingMissilePower(this, 3)));
                    break;
                case 2:
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(4), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                    break;
                case 3:
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.FIRE));
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 2, true)));
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Burn(), 1, true, true));
                    break;
                case 4:
                    int block = AbstractDungeon.player.currentBlock;
                    for (int i=0;i<4;i++) {
                        AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.FIRE));
                        block -= this.damage.get(0).output;
                        if (block < 0) {
                            int dmg = this.damage.get(0).output;
                            for (AbstractPower power : AbstractDungeon.player.powers) {
                                dmg = power.onAttacked(this.damage.get(0), dmg);
                            }
                            if (dmg > 0) {
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 1, true)));
                                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Burn(), 1));
                            }
                        }
                    }
                    break;
                case 5:
                    this.addToBot(new VFXAction(this, new BulletWaveEffect(this.hb.cX, this.hb.cY, Settings.WIDTH * 0.12), 0.5F, true));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(5), AbstractGameAction.AttackEffect.FIRE));
                    this.addToBot(new VFXAction(this, new BulletWaveEffect(this.hb.cX, this.hb.cY, Settings.WIDTH * 0.12), 0.5F, true));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(5), AbstractGameAction.AttackEffect.FIRE));
                    this.addToBot(new VFXAction(this, new BulletWaveEffect(this.hb.cX, this.hb.cY, Settings.WIDTH * 0.12), 0.5F, true));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(5), AbstractGameAction.AttackEffect.FIRE));
                    break;
                case 6:
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BulletSprayPower(this, 3, 5)));
                    break;
                case 7:
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, 2)));
                    break;
                case 8:
                    for (int i=0;i<(int)Math.ceil(this.tmpCtr / 2.0);i++) {
                        AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(6), AbstractGameAction.AttackEffect.FIRE));
                    }
                    break;
                case 9:
                    AbstractMonster mouser = new Mouser(MathUtils.random(-300.0F, 180.0F), MathUtils.random(0.0F, 380.0F), 3);
                    AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(mouser, false));
                    mouser.usePreBattleAction();
                    break;
                case 10:
                    AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, 999));
                    break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private static int calculateMousers(int amount) {
        if (amount <= 0) return 1;
        return amount * calculateMousers(amount - 1) + 1;
    }

    @Override
    protected void getMove(int aiRng) {
        bodySlamDamage = this.currentBlock;
        if (firstMove && !phaseTwo) {
            firstMove = false;
            packs1[1].execute();
            packs1[0].execute();
            movePackManager.isDone = false;
        }
        try {
            Field field = AbstractMonster.class.getDeclaredField("move");
            field.setAccessible(true);
            if (field.get(this) == null) {
                setMove((byte) 0, Intent.UNKNOWN);
                packs1[0].execute();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (movePackManager.isDone) {
            if (!isBeaten) {
                int ran = aiRng % packs1.length;
                if (movePackManager.lastMovePack == 2) ran = 0;
                packs1[ran].execute();
                movePackManager.lastMovePack = ran;
            } else {
                int ran = aiRng % packs2.length;
                if (ran == 5 && this.hasPower(BulletSprayPower.POWER_ID)) {
                    ran = 3;
                }
                int ctr = 0;
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (m instanceof Mouser && !m.isDeadOrEscaped()) {
                        if (m.hasPower(AlertModePower.POWER_ID)) {
                            ctr += calculateMousers(m.getPower(AlertModePower.POWER_ID).amount);
                        } else {
                            ctr++;
                        }
                    }
                }
                if (ran == 8) {
                    if (ctr <= 5) {
                        ran = 0;
                    }
                } else if (ctr > 3) {
                    ran = 8;
                }
                if (this.hasPower(HomingMissilePower.POWER_ID) && ran == 0) {
                    ran = 8;
                }
                packs2[ran].execute();
                movePackManager.lastMovePack = ran;
            }
        }
        movePackManager.getMove().execute();
    }

    public void render(SpriteBatch sb) {
        if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.INCOMPLETE || !phaseTwo) {
            super.render(sb);
        }
    }

    public void die() {
        super.die();
        CardCrawlGame.music.silenceTempBgmInstantly();
        CardCrawlGame.music.playTempBgmInstantly("BOSS_BEATEN");
        this.powers = new ArrayList<>();
        if (isBeaten) {
            isTrueBeaten = true;
            AbstractDungeon.getCurrRoom().rewards = new ArrayList<>();
            Set<AbstractCard> cardSet = new HashSet<>();
            cardSet.add(new Elimentaler());
            RewardItem reward = new RewardItem();
            reward.cards = new ArrayList<>(cardSet);
            AbstractDungeon.getCurrRoom().addCardReward(reward);
            MonsterRoomPatch.PatchRender.isEntryOpen = false;
            MonsterRoomPatch.PatchUpdate.hb_enabled = true;
            BaseMod.unsubscribe(this);
        } else {
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
            MonsterRoomPatch.PatchRender.isEntryOpen = true;
            DemoSoundMaster.playA("ENTRY_OPEN", 0.0F);
            AbstractDungeon.effectList.add(new RatJumpIntoEntryEffect(this));
            this.firstMove = true;
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                relic.onVictory();
            }
            for (AbstractPower power : AbstractDungeon.player.powers) {
                power.onVictory();
            }
        }
        isBeaten = true;
        movePackManager.clearRemainingMoves();
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m != this && !m.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToBottom(new SuicideAction(m, false));
            }
        }
        if (isTrueBeaten) StatsScreen.incrementBossSlain();
        if (GameActionManager.turn <= 1) {
            UnlockTracker.unlockAchievement("YOU_ARE_NOTHING");
        }

        if (GameActionManager.damageReceivedThisCombat - GameActionManager.hpLossThisCombat <= 0 && isTrueBeaten) {
            UnlockTracker.unlockAchievement("PERFECT");
            ++CardCrawlGame.perfect;
        }
        movePackManager.lastMovePack = -1;
    }

    public void movePosition(float x, float y) {
        float dialogOffsetX = this.dialogX - this.drawX;
        float dialogOffsetY = this.dialogY - this.drawY;
        this.drawX = x;
        this.drawY = y;
        this.dialogX = this.drawX + dialogOffsetX;
        this.dialogY = this.drawY + dialogOffsetY;
        this.animX = 0.0F;
        this.animY = 0.0F;
        this.refreshHitboxLocation();
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
        movePackManager = new MovePackManager();
    }

    @Override
    public Boolean onSave() {
        return isBeaten;
    }

    @Override
    public void onLoad(Boolean aBoolean) {
        if (aBoolean != null) {
            isBeaten = aBoolean;
        }
    }

    @Override
    public void receiveCardUsed(AbstractCard card) {
        if (phaseTwo && this.nextMove == 8) {
            this.tmpCtr--;
            if (this.tmpCtr < 0) this.tmpCtr = 0;
            setMove((byte) 8, Intent.ATTACK, 3, (int)Math.ceil(this.tmpCtr / 2.0), true);
            this.createIntent();
        }
    }

    private static class MovePackManager {
        public boolean isDone = true;
        int lastMovePack = -1;
        private List<MovePack> pack;

        MovePackManager() {
            pack = new ArrayList<>();
        }

        public MovePack getMove() {
            MovePack movePack = pack.get(0);
            pack.remove(0);
            if (pack.size() == 0) this.isDone = true;
            return movePack;
        }

        void addMove(MovePack movePack) {
            pack.add(movePack);
            this.isDone = false;
        }

        void clearRemainingMoves() {
            pack.clear();
            this.isDone = true;
        }
    }

    private interface MovePack {
        void execute();
    }
}

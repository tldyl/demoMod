package demoMod.monsters;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MalleablePower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;
import demoMod.effects.BlessEffect;
import demoMod.powers.BlessPower;

import java.util.ArrayList;
import java.util.List;

public class Gunsinger extends AbstractMonster {

    public static final String ID = DemoMod.makeID("Gunsinger");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final int HP_MAX = 15;
    private static final float HB_X = -8.0F;
    private static final float HB_Y = 10.0F;
    private static final float HB_W = 128.0F;
    private static final float HB_H = 128.0F;
    private static final int ATTACK_DMG = 6;
    private static final String IMG_PATH = DemoMod.getResourcePath("monsters/gunsinger/idle/init.png");

    private int phase = 0; //0 - 空闲 1 - 施法 2 - 攻击
    private int lastPhase = -1;

    private int idx[] = new int[] {0, 0, 0};
    private int uBound = 0;
    private int frameCounter = 0;

    private AbstractGameEffect effect;

    private static int maxCount = 12 * DemoMod.MAX_FPS / 60; //每过多少帧给怪物换一帧
    private static List<List<Texture>> frames = new ArrayList<>();

    private boolean isFirstMove = true;

    static {
        frames.add(new ArrayList<>());
        for (int i=1;i<=4;i++) {
            frames.get(0).add(new Texture(DemoMod.getResourcePath("monsters/gunsinger/idle/" + i + ".png")));
        }
        frames.add(new ArrayList<>());
        for (int i=1;i<=3;i++) {
            frames.get(1).add(new Texture(DemoMod.getResourcePath("monsters/gunsinger/blessing/" + i + ".png")));
        }
        frames.add(new ArrayList<>());
        for (int i=1;i<=4;i++) {
            frames.get(2).add(new Texture(DemoMod.getResourcePath("monsters/gunsinger/attack/" + i + ".png")));
        }
    }

    public Gunsinger(float x, float y) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, IMG_PATH, x, y);
        if (AbstractDungeon.ascensionLevel >= 7) {
            setHp(18, 22);
        } else {
            setHp(13, 17);
        }
        this.damage.add(new DamageInfo(this, ATTACK_DMG));
        this.effect = new BlessEffect(this.drawX, this.drawY + 16);
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MalleablePower(this, 16)));
    }

    @Override
    public void useStaggerAnimation() { //受伤时触发
        super.useStaggerAnimation();
        this.phase = 0;
        this.setMove((byte)2, Intent.UNKNOWN);
        this.createIntent();
        if (!this.effect.isDone) {
            ((BlessEffect)effect).stop();
        }
        AbstractDungeon.actionManager.addToBottom(new TextAboveCreatureAction(this, TextAboveCreatureAction.TextType.INTERRUPTED));
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped() && m.hasPower(DemoMod.makeID("BlessPower"))) {
                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(m, m, BlessPower.POWER_ID));
            }
        }
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                this.phase = 1;
                boolean hasBlessed = false;
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDeadOrEscaped() && m.hasPower(DemoMod.makeID("BlessPower"))) {
                        hasBlessed = true;
                        break;
                    }
                }
                if (!hasBlessed) {
                    AbstractMonster m = null;
                    for (int i=0;i<10;i++) {
                        m = AbstractDungeon.getRandomMonster();
                        if (m != this) break;
                    }
                    if (m != null && m != this) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new BlessPower(m)));
                    }
                }
                if (this.effect.isDone) {
                    ((BlessEffect)effect).start();
                    AbstractDungeon.effectsQueue.add(this.effect);
                }
                getMove(0);
                break;
            case 2:
                setMove(MOVES[0], (byte)1, Intent.BUFF);
                this.createIntent();
                takeTurn();
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
        }
    }

    @Override
    protected void getMove(int i) {
        if (isFirstMove) {
            setMove(MOVES[0], (byte)1, Intent.BUFF);
            isFirstMove = false;
            return;
        }
        boolean isAllDead = true;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped() && m != this && !(m instanceof LordOfTheJammed) && !(m instanceof Decoy)) {
                isAllDead = false;
                break;
            }
        }
        if (isAllDead) {
            this.phase = 2;
            maxCount = 6 * DemoMod.MAX_FPS / 60;
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, MalleablePower.POWER_ID));
            setMove((byte)3, Intent.ATTACK, this.damage.get(0).base);
        } else {
            setMove(MOVES[0], (byte)1, Intent.BUFF);
            this.createIntent();
        }
    }

    @Override
    public void update() {
        super.update();
        if (!this.isDead && !this.escaped) {
            frameCounter++;
            if (frameCounter > maxCount) frameCounter = 0;
            if (frameCounter == 0) {
                if (lastPhase != phase) {
                    uBound = frames.get(phase).size() - 1;
                    lastPhase = phase;
                }
                idx[phase]++;
                if (idx[phase] > uBound) {
                    idx[phase] = 0;
                    this.flipHorizontal = !this.flipHorizontal;
                }
                this.img = frames.get(phase).get(idx[phase]);
            }
        }
    }

    @Override
    public void die() {
        super.die();
    }

    @Override
    public void dispose() {

    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}

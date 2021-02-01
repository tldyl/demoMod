package demoMod.monsters;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.interfaces.PostOnMonsterDeathSubscriber;
import demoMod.patches.MonsterPatch;
import demoMod.powers.JammedPower;

public class LordOfTheJammed extends AbstractMonster implements PostOnMonsterDeathSubscriber {
    public static final String ID = DemoMod.makeID("LordOfTheJammed");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final int HP_MAX = Integer.MAX_VALUE;
    private static final float HB_X = -8.0F;
    private static final float HB_Y = 10.0F;
    private static final float HB_W = 230.0F;
    private static final float HB_H = 240.0F;
    private static final int ATTACK_DMG = 8;
    private static final String IMG_PATH = DemoMod.getResourcePath("monsters/lordOfTheJammed.png");

    private boolean draggingMode = false;
    private float dragFadeOut = 0.0F;
    private int fadeOutX;
    private int fadeOutY;

    public LordOfTheJammed(float x, float y) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, IMG_PATH, x, y);
        this.damage.add(new DamageInfo(this, ATTACK_DMG));
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new JammedPower(this)));
        MonsterPatch.DiePatch.subscribe(this);
    }

    @Override
    public void increaseMaxHp(int amount, boolean showEffect) {
    }

    @Override
    public void heal(int healAmount, boolean showEffect) {
    }

    @Override
    public void addBlock(int amount) {
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                addToBot(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                if (!this.hasPower(JammedPower.POWER_ID)) {
                    addToBot(new ApplyPowerAction(this, this, new JammedPower(this)));
                }
                break;
            case 2:
                addToBot(new SuicideAction(this, false));
                return;
        }
        rollMove();
    }

    @Override
    protected void getMove(int aiRng) {
        boolean isAllEnemyDead = true;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped() && !(m instanceof Decoy) && m != this) {
                isAllEnemyDead = false;
                break;
            }
        }
        if (isAllEnemyDead) {
            setMove((byte) 2, Intent.UNKNOWN);
            this.createIntent();
            this.takeTurn();
        } else {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, (this.damage.get(0)).base);
        }
    }

    @Override
    public void update() {
        if (this.hb != null) {
            if (this.hb.hovered && InputHelper.isMouseDown && !draggingMode) {
                draggingMode = true;
                dragFadeOut = 0.7F;
            }
            if (draggingMode && !InputHelper.isMouseDown) {
                draggingMode = false;
                fadeOutX = (int)((InputHelper.mX - this.drawX) / 2 + this.drawX);
                fadeOutY = (int)((InputHelper.mY - this.drawY - this.hb_h / 2.0F) / 2 + this.drawY);
            }
            if (draggingMode) {
                this.drawX += this.calculateVelocity((int)this.drawX, InputHelper.mX);
                this.drawY += this.calculateVelocity((int)this.drawY, (int)(InputHelper.mY - this.hb_h / 2.0F));
                this.hb.move(this.drawX + this.hb_x + this.animX, this.drawY + this.hb_y + this.hb_h / 2.0F);
                this.healthHb.move(this.hb.cX, this.hb.cY - this.hb_h / 2.0F - this.healthHb.height / 2.0F);
                this.intentHb.move(this.hb.cX + this.intentOffsetX, this.hb.cY + this.hb_h / 2.0F + 32.0F * Settings.scale);
            } else if (dragFadeOut > 0) {
                this.drawX += this.calculateVelocity((int)this.drawX, fadeOutX);
                this.drawY += this.calculateVelocity((int)this.drawY, fadeOutY);
                this.hb.move(this.drawX + this.hb_x + this.animX, this.drawY + this.hb_y + this.hb_h / 2.0F);
                this.healthHb.move(this.hb.cX, this.hb.cY - this.hb_h / 2.0F - this.healthHb.height / 2.0F);
                this.intentHb.move(this.hb.cX + this.intentOffsetX, this.hb.cY + this.hb_h / 2.0F + 32.0F * Settings.scale);
                this.dragFadeOut -= Gdx.graphics.getDeltaTime();
            }
        }
        super.update();
    }

    private int calculateVelocity(int src, int target) {
        return (target - src) / 10;
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }

    @Override
    public void onMonsterDeath(AbstractMonster m) {
        getMove(0);
    }

    @Override
    public void die(boolean triggerRelics) {
        MonsterPatch.DiePatch.unsubscribe(this);
        super.die(triggerRelics);
    }

    @Override
    public void escape() {

    }
}

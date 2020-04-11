package demoMod.monsters;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
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
    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new SuicideAction(this, false));
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
}

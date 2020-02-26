package demoMod.monsters;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;
import demoMod.DemoMod;
import demoMod.patches.ChangeTargetPatch;
import demoMod.relics.DecoyRelic;

public class Decoy extends AbstractMonster {
    public static final String ID = DemoMod.makeID("Decoy");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final int HP_MAX = 20;
    private static final float HB_X = -8.0F;
    private static final float HB_Y = 10.0F;
    private static final float HB_W = 115.0F;
    private static final float HB_H = 120.0F;
    private static final String IMG_PATH = DemoMod.getResourcePath("monsters/decoy.png");

    public Decoy(float x, float y, int maxHp) {
        super(NAME, ID, maxHp, HB_X, HB_Y, HB_W, HB_H, IMG_PATH, x, y);
    }

    public Decoy(float x, float y) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, IMG_PATH, x, y);
    }

    @Override
    public void takeTurn() {
        boolean isAllEnemyDead = true;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped() && !(m instanceof LordOfTheJammed) && m != this) {
                isAllEnemyDead = false;
                break;
            }
        }
        if (isAllEnemyDead) {
            AbstractDungeon.actionManager.addToBottom(new SuicideAction(this, false));
        }
    }

    protected void getMove(int i) {
        this.setMove((byte)1 , Intent.UNKNOWN);
        ChangeTargetPatch.source.clear();
        ChangeTargetPatch.source.addAll(AbstractDungeon.getCurrRoom().monsters.monsters);
        ChangeTargetPatch.target = this;
    }

    public void die() {
        ChangeTargetPatch.target = null;
        ChangeTargetPatch.source.clear();
        if (DecoyRelic.combos[0]) {
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (m != this && !m.isDeadOrEscaped()) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new PoisonPower(m, this, 10)));
                }
            }
        }
        super.die(true);
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}

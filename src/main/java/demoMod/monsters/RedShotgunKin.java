package demoMod.monsters;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.actions.PlaySoundAction;
import demoMod.powers.SelfExplodePower;
import demoMod.sounds.DemoSoundMaster;

import static demoMod.utils.Utils.calcMaxHpMultiplier;

public class RedShotgunKin extends AbstractMonster {
    public static final String ID = DemoMod.makeID("RedShotgunKin");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final int HP_MAX = 15;
    private static final float HB_X = -8.0F;
    private static final float HB_Y = 10.0F;
    private static final float HB_W = 120.0F;
    private static final float HB_H = 180.0F;
    private static final int ATTACK_DMG = 3;
    private static final String IMG_PATH = DemoMod.getResourcePath("monsters/redShotgunKin.png");

    private boolean firstMove = true;

    public RedShotgunKin(float x, float y) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, IMG_PATH, x, y);
        if (AbstractDungeon.ascensionLevel >= 7) {
            setHp(35 * (int)calcMaxHpMultiplier(), 40 * (int)calcMaxHpMultiplier());
        } else {
            setHp(28 * (int)calcMaxHpMultiplier(), 32 * (int)calcMaxHpMultiplier());
        }
        this.damage.add(new DamageInfo(this, ATTACK_DMG));
    }

    @Override
    public void usePreBattleAction() {
        if (AbstractDungeon.monsterRng.random(0, 99) < 30) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new SelfExplodePower(this, 6)));
        }
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new PlaySoundAction("GUN_FIRE_SHOTGUN"));
                for (int i=0;i<5;i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                }
                setMove(MOVES[0], (byte)2, Intent.UNKNOWN);
                break;
            case 2:
                DemoSoundMaster.playV("GUN_RELOAD_SHOTGUN", 0.1F);
                setMove((byte)1, Intent.ATTACK, this.damage.get(0).base, 5, true);
                break;
        }
    }

    @Override
    protected void getMove(int aiRng) {
        if (firstMove) {
            setMove((byte)1, Intent.ATTACK, this.damage.get(0).base, 5, true);
            firstMove = false;
        }
    }

    public void die() {
        super.die();
        DemoSoundMaster.playV("MONSTER_SHOTGUN_KIN_DEATH", 0.1F);
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}

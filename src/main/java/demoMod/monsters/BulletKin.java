package demoMod.monsters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.sounds.DemoSoundMaster;

import static demoMod.utils.Utils.calcMaxHpMultiplier;

public class BulletKin extends AbstractMonster {
    public static final String ID = DemoMod.makeID("BulletKin");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final int HP_MAX = 15;
    private static final float HB_X = -8.0F;
    private static final float HB_Y = 10.0F;
    private static final float HB_W = 120.0F;
    private static final float HB_H = 120.0F;
    private static final int ATTACK_DMG = 6;
    private static final String IMG_PATH = DemoMod.getResourcePath("monsters/bulletKin.png");

    private boolean wantSpawn = false;
    private boolean firstMove = true;

    private float x;
    private float y;

    public BulletKin(float x, float y) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, IMG_PATH, x, y);

        if (AbstractDungeon.ascensionLevel >= 7) {
            setHp(13 * (int)calcMaxHpMultiplier(), 17 * (int)calcMaxHpMultiplier());
        } else {
            setHp(16 * (int)calcMaxHpMultiplier(), 20 * (int)calcMaxHpMultiplier());
        }
        this.damage.add(new DamageInfo(this, ATTACK_DMG));
        this.x = x;
        this.y = y;
    }

    public BulletKin(float x, float y, boolean wantSpawn) {
        this(x, y);
        this.wantSpawn = wantSpawn;
    }

    @Override
    public void takeTurn() {
        int ctr = 0;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped() && m instanceof BulletKin) ctr++;
        }
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                if (ctr <= 6) {
                    setMove((byte) 2, Intent.UNKNOWN);
                }
                break;
            case 2:
                AbstractMonster kin = new BulletKin(MathUtils.random(-300.0F, 180.0F), MathUtils.random(0.0F, 380.0F));
                ((BulletKin) kin).wantSpawn = ctr < 8 && AbstractDungeon.monsterRng.randomBoolean();
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(kin, false));
                setMove(MOVES[0], (byte)3, Intent.DEFEND);
                break;
            case 3:
                addToBot(new GainBlockAction(this, 6 * (int)calcMaxHpMultiplier()));
                setMove((byte)1, Intent.ATTACK, this.damage.get(0).base);
                break;
        }
    }

    @Override
    protected void getMove(int aiRng) {
        if (firstMove) {
            if (wantSpawn) {
                setMove((byte)2, Intent.UNKNOWN);
            } else {
                if (AbstractDungeon.miscRng.randomBoolean()) {
                    setMove((byte) 1, Intent.ATTACK, this.damage.get(0).base);
                } else {
                    setMove(MOVES[0], (byte)3, Intent.DEFEND);
                }
            }
            firstMove = false;
        }
    }

    public void die() {
        super.die();
        this.img.dispose();
        this.img = new Texture(DemoMod.getResourcePath("monsters/bulletKin_die.png"));
        DemoSoundMaster.playV("MONSTER_BULLET_KIN_DEATH", 0.1F);
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}

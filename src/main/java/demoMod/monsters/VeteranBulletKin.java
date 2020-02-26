package demoMod.monsters;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.tempCards.Flaw;
import demoMod.sounds.DemoSoundMaster;

public class VeteranBulletKin extends AbstractMonster {
    public static final String ID = DemoMod.makeID("VeteranBulletKin");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final int HP_MAX = 15;
    private static final float HB_X = -8.0F;
    private static final float HB_Y = 10.0F;
    private static final float HB_W = 120.0F;
    private static final float HB_H = 120.0F;
    private static final int ATTACK_DMG = 9;
    private static final String IMG_PATH = DemoMod.getResourcePath("monsters/veteranBulletKin.png");

    private boolean firstMove = true;

    public VeteranBulletKin(float x, float y) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, IMG_PATH, x, y);
        if (AbstractDungeon.ascensionLevel >= 7) {
            setHp(18, 22);
        } else {
            setHp(13, 17);
        }
        this.damage.add(new DamageInfo(this, ATTACK_DMG));
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Flaw(), 1, true, true));
                setMove(MOVES[1], (byte)2, Intent.ATTACK_DEBUFF, this.damage.get(0).base);
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Flaw(), 1, true, true));
                setMove(MOVES[0], (byte)1, Intent.DEBUFF);
                break;
        }
    }

    @Override
    protected void getMove(int i) {
        if (firstMove) {
            setMove(MOVES[0], (byte)1, Intent.DEBUFF);
            firstMove = false;
        }
    }

    public void die() {
        super.die();
        DemoSoundMaster.playV("MONSTER_BULLET_KIN_DEATH", 0.1F);
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}

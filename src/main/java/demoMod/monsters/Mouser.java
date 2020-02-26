package demoMod.monsters;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import demoMod.DemoMod;
import demoMod.powers.AlertModePower;

public class Mouser extends AbstractMonster {

    public static final String ID = DemoMod.makeID("Mouser");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final int HP_MAX = 5;
    private static final float HB_X = -8.0F;
    private static final float HB_Y = 10.0F;
    private static final float HB_W = 120.0F;
    private static final float HB_H = 120.0F;
    private static final int ATTACK_DMG = 3;
    private static final String IMG_PATH = DemoMod.getResourcePath("monsters/mouser.png");
    private int spawnAmount;

    public Mouser(float x, float y, int spawnAmount) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, IMG_PATH, x, y);
        this.damage.add(new DamageInfo(this, ATTACK_DMG));
        this.spawnAmount = spawnAmount;
    }

    public void usePreBattleAction() {
        setMove((byte)1, Intent.ATTACK, this.damage.get(0).base);
        this.createIntent();
        if (this.spawnAmount > 0) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new AlertModePower(this, this.spawnAmount)));
        }
    }

    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        AbstractDungeon.actionManager.addToBottom(new VFXAction(new BiteEffect(p.hb.cX, p.hb.cY - 40.0F * Settings.scale, Color.SCARLET.cpy()), 0.2F));
        AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.NONE));
        setMove((byte)1, Intent.ATTACK, this.damage.get(0).base);
    }

    @Override
    protected void getMove(int i) {
        setMove((byte)1, Intent.ATTACK, this.damage.get(0).base);
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}

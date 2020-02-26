package demoMod.actions;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class EatMonsterAction extends AbstractGameAction {
    private AbstractMonster target;
    private AbstractPlayer p;
    private float x;
    private float y;
    private float orgX;
    private float orgY;
    private float dst;
    private boolean back = false;
    private int ctr = 0;

    public EatMonsterAction(AbstractMonster target) {
        this.target = target;
        this.duration = 0.1F;
        this.p = AbstractDungeon.player;
        this.x = p.drawX;
        this.y = p.drawY;
        this.orgX = this.x;
        this.orgY = this.y;
        this.dst = (this.target.hb.cX - this.x) / 1.5F;
    }

    @Override
    public void update() {
        if (!back) {
            this.x += Gdx.graphics.getDeltaTime() * this.dst;
            ctr++;
        } else {
            this.x -= Gdx.graphics.getDeltaTime() * this.dst;
            ctr--;
        }
        if (back && ctr <= 0) {
            this.isDone = true;
            this.x = this.orgX;
            this.y = this.orgY;
            p.flipHorizontal = !p.flipHorizontal;
        }
        p.movePosition(this.x, this.y);
        if (!back && Math.abs(this.x - this.target.hb.cX) <= 10) {
            back = true;
            p.flipHorizontal = !p.flipHorizontal;
            if (this.target.type != AbstractMonster.EnemyType.BOSS) {
                this.target.currentBlock = 0;
                this.target.gold = 0;
                this.target.currentHealth = 0;
                this.target.halfDead = true;
                this.target.healthBarUpdatedEvent();
                this.target.damage(new DamageInfo(null, 0, DamageInfo.DamageType.HP_LOSS));
            } else {
                this.target.damage(new DamageInfo(this.p, 40, DamageInfo.DamageType.THORNS));
            }
        }
    }
}

package demoMod.actions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;
import demoMod.cards.guns.BoxingGlove;

public class BoxingGloveAction extends AbstractGameAction {
    private boolean isSuperPunch;
    private BoxingGlove card;

    public BoxingGloveAction(BoxingGlove card, AbstractMonster target, int damageAmount, boolean isSuperPunch) {
        this.target = target;
        this.amount = damageAmount;
        this.duration = 0.1F;
        this.isSuperPunch = isSuperPunch;
        this.card = card;
    }

    @Override
    public void update() {
        if (this.duration == 0.1F && this.target != null) {
            if (!isSuperPunch) {
                AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.BLUNT_HEAVY));
            } else {
                AbstractDungeon.effectList.add(new WeightyImpactEffect(this.target.hb.cX, this.target.hb.cY, Color.GOLD.cpy()));
            }
            this.target.damage(new DamageInfo(AbstractDungeon.player, this.amount));
            if ((((AbstractMonster)this.target).isDying || this.target.currentHealth <= 0) && this.card.stars < 3) {
                this.card.stars += 1;
                for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                    if (card.uuid.equals(this.card.uuid)) {
                        BoxingGlove glove = (BoxingGlove) card;
                        glove.stars = this.card.stars;
                        break;
                    }
                }
            }
        }
        this.tickDuration();
    }
}

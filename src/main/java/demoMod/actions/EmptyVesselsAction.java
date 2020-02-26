package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashPowerEffect;
import demoMod.sounds.DemoSoundMaster;

import java.util.ArrayList;
import java.util.List;

public class EmptyVesselsAction extends AbstractGameAction {
    private List<AbstractGameEffect> flashAtkImgEffectList = new ArrayList<>();
    private int damageAmount;

    public EmptyVesselsAction(int damageAmount) {
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped()) {
                flashAtkImgEffectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AttackEffect.POISON, true));
            }
        }
        this.damageAmount = damageAmount;
        this.actionType = ActionType.DAMAGE;
        this.duration = 0.3F;
    }

    @Override
    public void update() {
        if (this.duration == 0.3F) {
            AbstractDungeon.effectList.addAll(flashAtkImgEffectList);
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDeadOrEscaped()) {
                    AbstractDungeon.effectList.add(new FlashPowerEffect(new PoisonPower(m, m, 0)));
                }
            }
            this.tickDuration();
        } else {
            if (flashAtkImgEffectList.get(0).duration <= 0.5F) {
                int[] damageMatrix = new int[AbstractDungeon.getCurrRoom().monsters.monsters.size()];
                for (int i=0;i<damageMatrix.length;i++) {
                    damageMatrix[i] = this.damageAmount;
                }
                AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(AbstractDungeon.player, damageMatrix, DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE));
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED, false);
                DemoSoundMaster.playA("COMBO_EMPTY_VESSELS", 0.0F);
                this.isDone = true;
            }
        }
    }
}

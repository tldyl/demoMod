package demoMod.actions;

import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.effects.DarkMarkerBlastEffect;
import demoMod.sounds.DemoSoundMaster;

public class DarkMarkerBlastAction extends AbstractGameAction {
    private AbstractPlayer player = AbstractDungeon.player;
    private AbstractCard card;
    private int energyOnUse;

    public DarkMarkerBlastAction(AbstractCard card, int energyOnUse) {
        this.duration = 1.0F;
        this.card = card;
        this.energyOnUse = energyOnUse;
    }

    @Override
    public void update() {
        if (this.duration == 1.0F) {
            DemoSoundMaster.playA("GUN_FIRE_DARK_MARKER_2", 0.0F);
            DemoMod.effectsQueue.add(new DarkMarkerBlastEffect());
        }
        if (this.duration <= 0.05F) {
            for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!monster.isDeadOrEscaped()) {
                    card.calculateCardDamage(monster);
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(monster, new DamageInfo(player, card.damage * energyOnUse, card.damageTypeForTurn)));
                }
            }
            for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!monster.isDeadOrEscaped()) {
                    card.calculateCardDamage(monster);
                    AbstractDungeon.actionManager.addToBottom(new StunMonsterAction(monster, player));
                }
            }
            this.isDone = true;
        }
        this.tickDuration();
    }
}

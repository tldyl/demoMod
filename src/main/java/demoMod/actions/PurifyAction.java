package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.PurificationShrine;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import demoMod.sounds.DemoSoundMaster;

public class PurifyAction extends AbstractGameAction {
    private final DamageInfo info;
    private AbstractCard srcCard;

    public PurifyAction(AbstractMonster m, DamageInfo info, AbstractCard srcCard) {
        this.info = info;
        this.srcCard = srcCard;
        this.setValues(m, info);
        this.actionType = ActionType.DAMAGE;
        this.duration = 0.1F;
    }

    @Override
    public void update() {
        if (this.duration == 0.1F && this.target != null) {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.FIRE));
            this.target.damage(this.info);
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
            if ((((AbstractMonster)this.target).isDying || this.target.currentHealth <= 0) && CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).size() >= 1
                    && !this.target.hasPower(MinionPower.POWER_ID)) {
                AbstractCard targetCard = null;
                for (AbstractCard card : CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).group) {
                    if (card.uuid == srcCard.uuid) {
                        targetCard = card;
                        break;
                    }
                }
                if (targetCard != null) {
                    AbstractDungeon.player.masterDeck.removeCard(targetCard);
                }
                AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, PurificationShrine.OPTIONS[2], false, false, false, true);
            } else {
                this.isDone = true;
            }
            this.tickDuration();
        } else if (this.target == null) {
            this.isDone = true;
        }
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            CardCrawlGame.sound.play("CARD_EXHAUST");
            DemoSoundMaster.playA("PURIFY", 0.0F);
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(AbstractDungeon.gridSelectScreen.selectedCards.get(0), (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
            AbstractDungeon.player.masterDeck.removeCard(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.isDone = true;
        }
    }
}

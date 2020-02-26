package demoMod.cards.interfaces;

import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.cards.managers.ChargeManager;
import demoMod.cards.optionCards.ChooseCharge;
import demoMod.cards.optionCards.ChooseNonCharge;

import java.util.ArrayList;

/**
 * 可以蓄力的枪需要实现此接口
 */
public interface ChargeCard {
    /**
     * 当玩家选择蓄力打出时
     * @param p 玩家
     * @param m 目标敌人
     */
    void onCharge(AbstractPlayer p, AbstractMonster m);

    /**
     * 当玩家选择不蓄力打出时
     * @param p 玩家
     * @param m 目标敌人
     */
    void onNonCharge(AbstractPlayer p, AbstractMonster m);

    default void select(AbstractMonster m) {
        ChargeManager.register(this, m);
        ArrayList<AbstractCard> chargeChoices = new ArrayList<>();
        chargeChoices.add(new ChooseCharge());
        chargeChoices.add(new ChooseNonCharge());
        AbstractDungeon.actionManager.addToBottom(new ChooseOneAction(chargeChoices));
    }
}

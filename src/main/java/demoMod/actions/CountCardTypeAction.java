package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.powers.HighPressurePower;

import java.util.ArrayList;
import java.util.List;

public class CountCardTypeAction extends AbstractGameAction {
    private HighPressurePower power;

    public CountCardTypeAction(HighPressurePower power) {
        this.power = power;
    }

    @Override
    public void update() {
        HighPressurePower.isEndByThis = false;
        List<AbstractCard.CardType> types = new ArrayList<>();
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            boolean dup = false;
            for (AbstractCard.CardType type : types) {
                if (type == card.type) {
                    dup = true;
                    break;
                }
            }
            if (!dup && card.type != AbstractCard.CardType.CURSE && card.type != AbstractCard.CardType.STATUS) {
                types.add(card.type);
            }
        }
        if (types.size() > 0) power.typeForTurn = types.get(AbstractDungeon.miscRng.random(types.size() - 1));
        power.updateDescription();
        this.isDone = true;
    }
}

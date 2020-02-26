package demoMod.cards.managers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.cards.interfaces.ChargeCard;

public class ChargeManager {
    private static ChargeCard c;
    private static AbstractMonster creature;

    public static void register(ChargeCard card, AbstractMonster target) {
        c = card;
        creature = target;
    }

    public static ChargeCard getChargeCard() {
        ChargeCard card = c;
        c = null;
        return card;
    }

    public static AbstractMonster getTarget() {
        AbstractMonster target = creature;
        creature = null;
        return target;
    }
}

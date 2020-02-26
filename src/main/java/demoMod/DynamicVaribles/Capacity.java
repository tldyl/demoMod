package demoMod.DynamicVaribles;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import demoMod.cards.guns.AbstractGunCard;

public class Capacity extends DynamicVariable {
    @Override
    public String key() {
        return "DemoModCapacity";
    }

    @Override
    public boolean isModified(AbstractCard card) {
        if (card instanceof AbstractGunCard) {
            AbstractGunCard gunCard = (AbstractGunCard) card;
            return gunCard.isMaxCapacityModified;
        }
        return false;
    }

    @Override
    public int value(AbstractCard card) {
        if (card instanceof AbstractGunCard) {
            AbstractGunCard gunCard = (AbstractGunCard) card;
            return gunCard.capacity;
        }
        return 0;
    }

    @Override
    public int baseValue(AbstractCard card) {
        if (card instanceof AbstractGunCard) {
            AbstractGunCard gunCard = (AbstractGunCard) card;
            return gunCard.capacity;
        }
        return 0;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return isModified(card);
    }
}

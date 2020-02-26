package demoMod.DynamicVaribles;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import demoMod.cards.guns.AbstractGunCard;

public class ExtraDamage extends DynamicVariable {
    @Override
    public String key() {
        return "DemoModExtraDamage";
    }

    @Override
    public boolean isModified(AbstractCard abstractCard) {
        return false;
    }

    @Override
    public int value(AbstractCard c) {
        if (c instanceof AbstractGunCard) {
            AbstractGunCard gunCard = (AbstractGunCard) c;
            return gunCard.extraDamage;
        }
        return 0;
    }

    @Override
    public int baseValue(AbstractCard c) {
        if (c instanceof AbstractGunCard) {
            AbstractGunCard gunCard = (AbstractGunCard) c;
            return gunCard.extraDamage;
        }
        return 0;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return card.upgraded;
    }
}

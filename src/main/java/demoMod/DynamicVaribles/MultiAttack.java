package demoMod.DynamicVaribles;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import demoMod.cards.interfaces.MultiAttackCard;

public class MultiAttack extends DynamicVariable {
    @Override
    public String key() {
        return "DemoModMulti";
    }

    @Override
    public boolean isModified(AbstractCard c) {
        if (c instanceof MultiAttackCard) {
            return ((MultiAttackCard)c).isMultiModified();
        }
        return false;
    }

    @Override
    public int value(AbstractCard c) {
        if (c instanceof MultiAttackCard) {
            return ((MultiAttackCard)c).getMulti();
        }
        return 1;
    }

    @Override
    public int baseValue(AbstractCard c) {
        if (c instanceof MultiAttackCard) {
            return ((MultiAttackCard)c).getMulti();
        }
        return 1;
    }

    @Override
    public boolean upgraded(AbstractCard c) {
        return c.upgraded;
    }
}

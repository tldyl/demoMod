package demoMod.interfaces;

import com.megacrit.cardcrawl.cards.DamageInfo;

public interface PreAttackDecreaseBlock {
    int onAttackBeforeDecreaseBlock(DamageInfo info, int damageAmount);
}

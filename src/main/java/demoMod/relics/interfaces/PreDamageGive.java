package demoMod.relics.interfaces;

import com.megacrit.cardcrawl.cards.DamageInfo;

public interface PreDamageGive {
    float atDamageGive(float damage, DamageInfo.DamageType type);
}

package demoMod.interfaces;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface PostOnMonsterDeathSubscriber {
    void onMonsterDeath(AbstractMonster m);
}

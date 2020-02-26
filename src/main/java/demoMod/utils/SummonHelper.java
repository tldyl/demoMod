package demoMod.utils;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class SummonHelper {
    public static void summonMinion(AbstractMonster monster) {
        boolean canNotLose = AbstractDungeon.getCurrRoom().cannotLose;
        MinionHelper.addMinion(AbstractDungeon.player, monster);
        AbstractDungeon.getCurrRoom().cannotLose = canNotLose;
    }
}

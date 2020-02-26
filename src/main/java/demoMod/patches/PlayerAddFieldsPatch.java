package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

@SuppressWarnings("unchecked")
@SpirePatch(
        clz = AbstractPlayer.class,
        method = "<class>"
)
public class PlayerAddFieldsPatch {
    private static Integer maxMinions = 2147483647;
    private static MonsterGroup minions = new MonsterGroup(new AbstractMonster[0]);
    public static SpireField<Integer> f_maxMinions = new SpireField(() -> maxMinions);
    public static SpireField<MonsterGroup> f_minions = new SpireField(() -> minions);

    public PlayerAddFieldsPatch() {
    }
}

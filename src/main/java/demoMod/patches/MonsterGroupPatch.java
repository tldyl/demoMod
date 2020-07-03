package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.ending.CorruptHeart;
import demoMod.actions.CheckInvincibleAction;
import demoMod.characters.HuntressCharacter;
import demoMod.monsters.LordOfTheJammed;
import demoMod.powers.StrengthOfCursePower;
import demoMod.rewards.GlassGuonStone;
import demoMod.sounds.DemoSoundMaster;

@SuppressWarnings("unused")
public class MonsterGroupPatch {

    @SpirePatch(
            clz = MonsterGroup.class,
            method = "usePreBattleAction"
    )
    public static class PatchUsePreBattleAction {
        public static SpireReturn Postfix(MonsterGroup monsterGroup) {
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                double chance = HuntressCharacter.curse * 5;
                if (chance > 50) chance = 50;
                for (AbstractMonster m : monsterGroup.monsters) {
                    if (!(m instanceof LordOfTheJammed)) {
                        int ran = AbstractDungeon.miscRng.random(99);
                        if (ran < chance) {
                            System.out.println("DemoMod:Cursed a monster.");
                            DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new StrengthOfCursePower(m)));
                            AbstractDungeon.actionManager.addToBottom(new CheckInvincibleAction(m, (float)(0.05 * HuntressCharacter.curse)));
                            AbstractDungeon.getCurrRoom().addGoldToRewards((int)(HuntressCharacter.curse));
                        }
                    }
                }
            }
            int ran = AbstractDungeon.treasureRng.random(99);
            if (ran < 40) {
                AbstractDungeon.getCurrRoom().rewards.add(new GlassGuonStone());
            }
            return SpireReturn.Continue();
        }
    }
}

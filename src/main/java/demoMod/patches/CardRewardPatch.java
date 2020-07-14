package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.cards.Spice;

/**
 * @author Temple9
 * @since 2019-9-13
 *
 * 在击败怪物后的卡牌奖励里按香料替换概率将卡牌随机替换为香料
 */
@SuppressWarnings("unused")
@SpirePatch(
        clz=AbstractDungeon.class,
        method="getRewardCards"
)
public class CardRewardPatch {
    public CardRewardPatch() {

    }

    @SpireInsertPatch(rloc=54, localvars={"card"}) //将以下这段代码注入到getRewardCards方法的第54行后
    public static void Insert(@ByRef(type="cards.AbstractCard") Object[] _card) { //传入这个方法的本地变量card的地址
        try {
            java.util.Random random = new java.util.Random();
            double ran = random.nextDouble();
            if (ran < Spice.dropChance) {
                _card[0] = new demoMod.cards.Spice();
                if (AbstractDungeon.player.hasRelic("Toxic Egg 2")) {
                    ((AbstractCard)_card[0]).upgrade();
                }
            }
        } catch (SecurityException|IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}

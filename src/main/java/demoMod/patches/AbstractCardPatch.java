package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

@SuppressWarnings("unused")
public class AbstractCardPatch {
    @SpirePatch(
            clz = AbstractCard.class,
            method = "cardPlayable"
    )
    public static class PatchCardPlayable {
        public static SpireReturn<Boolean> Prefix(AbstractCard c, AbstractMonster m) {
            if (AbstractDungeon.getMonsters() == null) {
                return SpireReturn.Return(false);
            }
            return SpireReturn.Continue();
        }
    }
}

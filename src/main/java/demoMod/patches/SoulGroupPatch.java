package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.relics.interfaces.PostCardAddedToDrawPileSubscriber;

@SuppressWarnings("unused")
public class SoulGroupPatch {
    @SpirePatch(
            clz = SoulGroup.class,
            method = "onToDeck",
            paramtypez = {
                    AbstractCard.class,
                    boolean.class,
                    boolean.class
            }
    )
    public static class PatchOnToDeck {
        public static void Postfix(SoulGroup soulGroup, AbstractCard card, boolean randomSpot, boolean visualOnly) {
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof PostCardAddedToDrawPileSubscriber) {
                    ((PostCardAddedToDrawPileSubscriber)relic).onCardAddedToDrawPile(card);
                }
            }
        }
    }
}

package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import demoMod.DemoMod;
import demoMod.characters.HuntressCharacter;

@SuppressWarnings("unused")
public class ShopRoomPatch {
    @SpirePatch(
            clz = ShopRoom.class,
            method = "onPlayerEntry"
    )
    public static class PatchOnPlayerEntry {
        @SpireInsertPatch(rloc = 4)
        public static SpireReturn Insert1(ShopRoom shopRoom) {
            DemoMod.canSteal = false;
            if (DemoMod.afterSteal) return SpireReturn.Return(null);
            return SpireReturn.Continue();
        }

        @SpireInsertPatch(rloc = 2)
        public static SpireReturn Insert2(ShopRoom shopRoom) {
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                CardCrawlGame.music.silenceTempBgmInstantly();
                shopRoom.playBGM("GUNGEON_SHOP");
            }
            return SpireReturn.Continue();
        }
    }
}

package demoMod.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import demoMod.relics.Junk;
import demoMod.relics.SerJunkan;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class CombatRewardScreenPatch {
    @SpirePatch(
            clz = CombatRewardScreen.class,
            method = "open",
            paramtypez = {}
    )
    public static class PatchOpen1 {
        public static void Postfix(CombatRewardScreen rewardScreen) {
            if (AbstractDungeon.player.hasRelic(SerJunkan.ID) && !(AbstractDungeon.getCurrRoom() instanceof TreasureRoomBoss)) {
                ArrayList<RewardItem> rewardItems = new ArrayList<>();
                for (RewardItem item : rewardScreen.rewards) {
                    rewardItems.add(item);
                    if (item.type == RewardItem.RewardType.RELIC && item.relicLink == null) {
                        RewardItem junk = new RewardItem(new Junk()) {
                            public void render(SpriteBatch sb) {
                                super.render(sb);
                                if (this.hb.hovered && this.relicLink != null) {
                                    TipHelper.renderGenericTip(360.0F * Settings.scale, (float)InputHelper.mY + 50.0F * Settings.scale, TEXT[7], TEXT[8] + FontHelper.colorString(this.relicLink.relic.name + TEXT[9], "y"));
                                }
                                try {
                                    Method method = RewardItem.class.getDeclaredMethod("renderRelicLink", SpriteBatch.class);
                                    method.setAccessible(true);
                                    method.invoke(this, sb);
                                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        rewardItems.add(junk);
                        item.relicLink = junk;
                        junk.relicLink = item;
                    }
                }
                rewardScreen.rewards.clear(); //给奖励重新排序
                rewardScreen.rewards.addAll(rewardItems);
                rewardScreen.positionRewards();
            }
        }
    }

    @SpirePatch(
            clz = CombatRewardScreen.class,
            method = "open",
            paramtypez = {
                    String.class
            }
    )
    public static class PatchOpen2 {
        public static void Postfix(CombatRewardScreen rewardScreen, String label) {
            PatchOpen1.Postfix(rewardScreen);
        }
    }
}

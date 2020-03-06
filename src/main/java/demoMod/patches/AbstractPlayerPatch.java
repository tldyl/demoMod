package demoMod.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import demoMod.DemoMod;
import demoMod.powers.PacManPower;
import demoMod.relics.ResourcefulSack;
import demoMod.relics.interfaces.PostBeforePlayerDeath;
import demoMod.relics.interfaces.PostRemoveRelic;
import demoMod.sounds.DemoSoundMaster;

import java.util.*;

import static demoMod.patches.AbstractPlayerPatch.PatchRender.ratAnim;
import static demoMod.patches.AbstractPlayerPatch.PatchUpdate.*;

@SuppressWarnings("unused")
public class AbstractPlayerPatch {
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "loseRelic"
    )
    public static class PatchLoseRelic {
        @SpireInsertPatch(rloc = 22, localvars = {"toRemove"})
        public static void Insert(AbstractPlayer player, String relicId, @ByRef(type = "relics.AbstractRelic") Object[] _relic) {
            if (_relic != null && _relic[0] != null) {
                AbstractRelic relic = (AbstractRelic) _relic[0];
                if (relic instanceof PostRemoveRelic) {
                    ((PostRemoveRelic) relic).onRemove();
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "obtainPotion",
            paramtypez = {
                    AbstractPotion.class
            }
    )
    public static class PatchObtainPotion {
        public static SpireReturn<Boolean> Prefix(AbstractPlayer player, AbstractPotion potionToObtain) {
            if (player.hasRelic("Sozu")) {
                player.getRelic("Sozu").flash();
                return SpireReturn.Return(false);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "damage"
    )
    public static class PatchDamage {
        @SpireInsertPatch(rloc = 127)
        public static SpireReturn Insert(AbstractPlayer p, DamageInfo info) {
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof PostBeforePlayerDeath) {
                    if (!((PostBeforePlayerDeath)relic).isUsedUp()) {
                        ((PostBeforePlayerDeath) relic).onNearDeath();
                        break;
                    }
                }
            }
            if (p.currentHealth > 0) return SpireReturn.Return(null);
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "update"
    )
    public static class PatchUpdate {

        public static Map<String, Integer> idx = new HashMap<>();
        static int idx_rat = 0;
        public static String animStatus = "intro";
        public static boolean enabled = false;
        private static int frame_ctr = 0;
        private static int frame_ctr_pac = (int)(2.0 * DemoMod.MAX_FPS / 60.0);
        static int frame_delay = (int)(8.0 * DemoMod.MAX_FPS / 60.0);

        public static void Prefix(AbstractPlayer p) {
            frame_ctr++;
            if (p.hasPower(PacManPower.POWER_ID)) {
                enabled = true;
                idx.put(animStatus, idx.get(animStatus) + (frame_ctr >= frame_ctr_pac || idx.get(animStatus) == -1 ? 1 : 0));
                if (animStatus.equals("intro")) {
                    if (idx.get(animStatus) >= 12) {
                        idx.put(animStatus, -1);
                        animStatus = "loop";
                        idx.put(animStatus, 0);
                        DemoSoundMaster.playL("CHEESE_LOOP");
                    }
                }
                if (animStatus.equals("loop")) {
                    if (idx.get(animStatus) >= 13) {
                        idx.put(animStatus, 0);
                    }
                }
            } else {
                if (enabled) {
                    if (!animStatus.equals("outro")) {
                        animStatus = "outro";
                        DemoSoundMaster.stopL("CHEESE_LOOP");
                        DemoSoundMaster.playA("CHEESE_OUTRO", 0.0F);
                    }
                    idx.put(animStatus, idx.get(animStatus) + (frame_ctr >= frame_ctr_pac || idx.get(animStatus) == -1 ? 1 : 0));
                    if (idx.get(animStatus) >= 29) {
                        idx.put(animStatus, -1);
                        animStatus = "intro";
                        enabled = false;
                    }
                }
            }
            idx_rat++;
            if (idx_rat >= frame_delay * ratAnim.length) idx_rat = 0;
            if (frame_ctr >= frame_ctr_pac) frame_ctr = 0;
        }

        static {
            idx.put("intro", -1);
            idx.put("loop", -1);
            idx.put("outro", -1);
        }
    }

    @SuppressWarnings("Duplicates")
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "render"
    )
    public static class PatchRender {

        static Map<String, Texture[]> pacManAnim;
        static Texture[] ratAnim;

        public static SpireReturn Prefix(AbstractPlayer p, SpriteBatch sb) {
            //这里判断是否使用了吃了一口的奶酪，如果使用了就改变角色外观
            if (p.hasPower(PacManPower.POWER_ID) || enabled) {
                p.stance.render(sb);
                if ((AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT || AbstractDungeon.getCurrRoom() instanceof MonsterRoom) && !p.isDead) {
                    p.renderHealth(sb);
                    if (!p.orbs.isEmpty()) {
                        for (AbstractOrb o : p.orbs) {
                            o.render(sb);
                        }
                    }
                }
                sb.setColor(Color.WHITE);
                sb.draw(pacManAnim.get(animStatus)[idx.get(animStatus)], p.drawX - (float)128 * Settings.scale / 2.0F + p.animX, p.drawY, (float)128 * Settings.scale, (float)128 * Settings.scale, 0, 0, 128, 128, p.flipHorizontal, p.flipVertical);
                p.hb.render(sb);
                p.healthHb.render(sb);
                return SpireReturn.Return(null);
            }
            if (ResourcefulSack.isComboActivated && !p.isDead && !(AbstractDungeon.getCurrRoom() instanceof RestRoom)) {
                p.stance.render(sb);
                if ((AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT || AbstractDungeon.getCurrRoom() instanceof MonsterRoom) && !p.isDead) {
                    p.renderHealth(sb);
                    if (!p.orbs.isEmpty()) {
                        for (AbstractOrb o : p.orbs) {
                            o.render(sb);
                        }
                    }
                }
                sb.setColor(Color.WHITE);
                sb.draw(ratAnim[idx_rat / frame_delay], p.drawX - (float)78 * Settings.scale / 2.0F + p.animX, p.drawY, (float)78 * Settings.scale, (float)66 * Settings.scale, 0, 0, 78, 66, p.flipHorizontal, p.flipVertical);
                p.hb.render(sb);
                p.healthHb.render(sb);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        static {
            pacManAnim = new HashMap<>();
            pacManAnim.put("intro", new Texture[12]);
            pacManAnim.put("loop", new Texture[13]);
            pacManAnim.put("outro", new Texture[29]);
            for (int i=100000;i<100012;i++) {
                String n = Integer.toString(i).substring(1);
                pacManAnim.get("intro")[i - 100000] = new Texture("DemoImages/effects/partiallyEatenCheese/intro/intro_" + n + ".png");
            }
            for (int i=100000;i<100013;i++) {
                String n = Integer.toString(i).substring(1);
                pacManAnim.get("loop")[i - 100000] = new Texture("DemoImages/effects/partiallyEatenCheese/loop/loop_" + n + ".png");
            }
            for (int i=100000;i<100029;i++) {
                String n = Integer.toString(i).substring(1);
                pacManAnim.get("outro")[i - 100000] = new Texture("DemoImages/effects/partiallyEatenCheese/outro/outro_" + n + ".png");
            }

            ratAnim = new Texture[4];
            for (int i=0;i<ratAnim.length;i++) {
                ratAnim[i] = new Texture(DemoMod.getResourcePath("char/char_rat_" + (i + 1) + ".png"));
            }
        }
    }
}

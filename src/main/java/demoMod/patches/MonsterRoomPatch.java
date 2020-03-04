package demoMod.patches;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import demoMod.DemoMod;
import demoMod.characters.HuntressCharacter;
import demoMod.dungeons.Maze;
import demoMod.effects.PlayerJumpIntoEntryEffect;
import demoMod.effects.ResourcefulRatPhaseTwoIntro;
import demoMod.monsters.LordOfTheJammed;
import demoMod.monsters.ResourcefulRat;

import static demoMod.patches.MonsterRoomPatch.PatchRender.enabled;
import static demoMod.patches.MonsterRoomPatch.PatchRender.isEntryOpen;

@SuppressWarnings("unused")
public class MonsterRoomPatch {

    public static String mazeTempMusicSoundKey = "ACT_MAZE_COMBAT";
    public static boolean entered = false;

    @SpirePatch(
            clz = MonsterRoom.class,
            method = "onPlayerEntry"
    )
    public static class PatchOnPlayerEntry {
        @SpireInsertPatch(rloc = 3)
        public static void Insert(MonsterRoom room) {
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                if (HuntressCharacter.curse >= 10) {
                    room.monsters.add(new LordOfTheJammed( - 430.0F, room.monsters.monsters.get(0).animY));
                }
            }
        }

        public static void Prefix(MonsterRoom room) {
            if (AbstractDungeon.id.equals(Maze.ID)) {
                if (mazeTempMusicSoundKey != null) {
                    CardCrawlGame.music.dispose();
                    CardCrawlGame.music.changeBGM(mazeTempMusicSoundKey);
                    entered = true;
                }
                mazeTempMusicSoundKey = null;
            }
        }
    }

    @SpirePatch(
            clz = MonsterRoomBoss.class,
            method = "onPlayerEntry"
    )
    public static class PatchOnPlayerEntryBossRoom {
        @SpireInsertPatch(rloc = 3)
        public static void Insert(MonsterRoom room) {
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                if (HuntressCharacter.curse >= 10) {
                    room.monsters.add(new LordOfTheJammed( - 430.0F, room.monsters.monsters.get(0).animY));
                }
            }
            if (AbstractDungeon.id.equals(Maze.ID) && ResourcefulRat.isBeaten) {
                room.phase = AbstractRoom.RoomPhase.INCOMPLETE;
                CardCrawlGame.music.silenceTempBgmInstantly();
                AbstractDungeon.effectList.add(new ResourcefulRatPhaseTwoIntro(AbstractDungeon.getCurrRoom().monsters.getMonster(ResourcefulRat.ID)));
                AbstractDungeon.bossList.add(ResourcefulRat.ID);
            }
        }
    }

    @SpirePatch(
            clz = MonsterRoomElite.class,
            method = "onPlayerEntry"
    )
    public static class PatchOnPlayerEntryEliteRoom {
        @SpireInsertPatch(rloc = 3)
        public static void Insert(MonsterRoom room) {
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                if (HuntressCharacter.curse >= 10) {
                    room.monsters.add(new LordOfTheJammed( - 430.0F, room.monsters.monsters.get(0).animY + 180.0F));
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractRoom.class,
            method = "update"
    )
    public static class PatchUpdate {
        private static Hitbox hb;
        public static boolean hb_enabled = true;

        public static void Prefix(AbstractRoom room) {
            if (enabled && isEntryOpen && hb_enabled) {
                hb.update();
                if (hb.hovered && (InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed()) && !AbstractDungeon.isScreenUp) {
                    InputHelper.justClickedLeft = false;
                    hb_enabled = false;
                    AbstractDungeon.effectList.add(new PlayerJumpIntoEntryEffect(false));
                }
            }
        }

        static {
            hb = new Hitbox(300, 100);
            hb.move(Settings.WIDTH * 0.5F, Settings.HEIGHT * 0.3F);
        }
    }

    @SpirePatch(
            clz = AbstractRoom.class,
            method = "render"
    )
    public static class PatchRender {
        private static Texture entry_close = new Texture(DemoMod.getResourcePath("effects/entry_phase_2.png"));
        private static Texture entry_open = new Texture(DemoMod.getResourcePath("effects/entry_phase_2_open.png"));
        public static boolean isEntryOpen = false;
        public static boolean enabled = false;

        public static void Prefix(AbstractRoom room, SpriteBatch sb) {
            if (enabled) {
                sb.setColor(1, 1, 1, 1);
                if (isEntryOpen) {
                    sb.draw(entry_open, Settings.WIDTH * 0.5F - 150, Settings.HEIGHT * 0.3F - 50);
                } else {
                    sb.draw(entry_close, Settings.WIDTH * 0.5F - 150, Settings.HEIGHT * 0.3F - 50);
                }
            }
        }
    }
}

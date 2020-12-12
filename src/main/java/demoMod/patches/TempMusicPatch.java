package demoMod.patches;

import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.audio.TempMusic;

@SuppressWarnings("unused")
public class TempMusicPatch {
    @SpirePatch(
            clz = TempMusic.class,
            method = "getSong"
    )
    public static class PatchGetSong {
        @SpireInsertPatch(rloc = 0)
        public static SpireReturn<Music> Insert(TempMusic tempMusic, String key) {
            if (key.equals("GUNGEON_SHOP")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/gungeon_shop.mp3"));
            }
            if (key.equals("BOSS_RESOURCEFUL_RAT_1")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/boss_resourceful_rat_1.mp3"));
            }
            if (key.equals("BOSS_RESOURCEFUL_RAT_2")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/boss_resourceful_rat_2.mp3"));
            }
            if (key.equals("BOSS_BEATEN")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/boss_beaten.mp3"));
            }
            if (key.equals("ACT_MAZE_INTRO")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/act_maze_intro.mp3"));
            }
            if (key.equals("ACT_MAZE_COMBAT")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/act_maze_combat.mp3"));
            }
            if (key.equals("BOSS_DRAGUN_1")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/boss_dragun_1.mp3"));
            }
            if (key.equals("BOSS_DRAGUN_2")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/boss_dragun_2.mp3"));
            }
            if (key.equals("EVENT_WINCHESTERS_GAME")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/event_winchesters_game.ogg"));
            }
            if (key.equals("EVENT_WINCHESTERS_GAME_PLAYING")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/event_winchesters_game_playing.ogg"));
            }
            return SpireReturn.Continue();
        }
    }
}

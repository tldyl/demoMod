package demoMod.patches;

import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.audio.MainMusic;

@SuppressWarnings("unused")
public class MainMusicPatch {
    @SpirePatch(
            clz = MainMusic.class,
            method = "getSong"
    )
    public static class PatchGetSong {
        public static SpireReturn<Music> Prefix(MainMusic mainMusic, String key) {
            System.out.println("DemoMod:Sound key:" + key);
            if (key.equals("DemoMod:Maze")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/act_maze_intro.mp3"));
            }
            if (key.equals("ACT_MAZE_COMBAT")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/act_maze_combat.mp3"));
            }
            return SpireReturn.Continue();
        }
    }
}

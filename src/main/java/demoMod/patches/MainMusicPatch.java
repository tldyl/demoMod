package demoMod.patches;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
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
            if (key.equals("DemoMod:Maze")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/act_maze_intro.mp3"));
            }
            if (key.equals("ACT_MAZE_COMBAT")) {
                return SpireReturn.Return(MainMusic.newMusic("DemoAudio/music/act_maze_combat.mp3"));
            }
            if (key.equals("DemoExt:Forge")) {
                return SpireReturn.Return(MainMusic.newMusic(String.format("DemoAudio/music/act_forge_%d.mp3", MathUtils.random(1) + 1)));
            }
            return SpireReturn.Continue();
        }
    }
}

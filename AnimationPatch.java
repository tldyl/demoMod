package animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class AnimationPatch {

    public AnimationPatch() {
    }

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.characters.AbstractPlayer",
            method = "update"
    )
    public static class UpdatePatch {
        public UpdatePatch() {
        }

        public static void Postfix(AbstractPlayer player) {
            for (AbstractAnimation animation : AbstractAnimation.animations) {
                if (animation != null) {
                    animation.update();
                }
            }
        }
    }

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.characters.AbstractPlayer",
            method = "render"
    )
    public static class RenderPatch {
        public RenderPatch() {
        }

        public static void Prefix(AbstractPlayer player, SpriteBatch sb) {
            for (AbstractAnimation animation : AbstractAnimation.animations) {
                if (animation != null) {
                    animation.render(sb);
                }
            }
        }
    }

}

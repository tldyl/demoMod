package animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import utils.Invoker;

/**
 * Created by Keeper on 2019/3/23.
 */
public class GifCardPatch {

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderPortrait", paramtypez = {SpriteBatch.class})
    public static class renderPortraitPatch {

        public static void Postfix(SingleCardViewPopup singleCardViewPopup, SpriteBatch sb) {
            AbstractCard card = Invoker.getField(singleCardViewPopup, "card");
            if (card instanceof AbstractGIFCard && ((AbstractGIFCard)card).textureImg.endsWith("gif")) {
                ((AbstractGIFCard) card).gifAnimation.render(sb, (float) Settings.WIDTH / 2.0F - 250.0F, (float)Settings.HEIGHT / 2.0F - 190.0F + 136.0F * Settings.scale, 250.0F, 190.0F, 500.0F, 380.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 500, 380, false, false);
            }
        }
    }
}

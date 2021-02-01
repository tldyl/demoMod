package demoMod.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import demoMod.cards.guns.AbstractGunCard;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class SingleCardViewPopupPatch {
    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "renderCardTypeText"
    )
    public static class RenderCardTypeText {
        public static SpireReturn<Void> Prefix(SingleCardViewPopup singleCardViewPopup, SpriteBatch sb) {
            AbstractCard card = null;
            try {
                Field field = SingleCardViewPopup.class.getDeclaredField("card");
                field.setAccessible(true);
                card = (AbstractCard) field.get(singleCardViewPopup);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (card instanceof AbstractGunCard) {
                FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, AbstractGunCard.GUN_CARD_TYPE_NAME.TEXT[0], (float)Settings.WIDTH / 2.0F + 3.0F * Settings.scale, (float)Settings.HEIGHT / 2.0F - 40.0F * Settings.scale, AbstractGunCard.CARD_TYPE_COLOR);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}

package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import demoMod.DemoMod;
import demoMod.cards.BlackBlade;
import demoMod.cards.WhiteBlade;
import demoMod.cards.guns.FinishedGun;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
@SpirePatch(
        clz = ShowCardAndObtainEffect.class,
        method = "update"
)
public class ShowCardAndObtainEffectPatch {
    public ShowCardAndObtainEffectPatch() {
    }

    public static void Postfix(ShowCardAndObtainEffect obj) {
        if (obj.duration <= 0.0F) {
            try {
                Field cardField = ShowCardAndObtainEffect.class.getDeclaredField("card");
                cardField.setAccessible(true);
                AbstractCard card = (AbstractCard)cardField.get(obj);
                if (card instanceof PostAddedToMasterDeckSubscriber) {
                    ((PostAddedToMasterDeckSubscriber)card).onAddedToMasterDeck();
                }
                DemoMod.onMasterDeckChange();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }
    }
}

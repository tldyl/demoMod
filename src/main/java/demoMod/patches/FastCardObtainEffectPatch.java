package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;
import demoMod.DemoMod;
import demoMod.cards.BlackBlade;
import demoMod.cards.WhiteBlade;
import demoMod.cards.guns.FinishedGun;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
@SpirePatch(
        clz = FastCardObtainEffect.class,
        method = "update"
)
public class FastCardObtainEffectPatch {

    public FastCardObtainEffectPatch() {
    }

    public static void Postfix(FastCardObtainEffect obj) {
        try {
            Field cardField = FastCardObtainEffect.class.getDeclaredField("card");
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

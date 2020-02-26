package demoMod.utils;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import java.lang.reflect.Field;

public class CustomPotionTexture {
    public static void setPotionTexture(AbstractPotion potion,
                                        String containerImg,
                                        String liquidImg,
                                        String hybridImg,
                                        String spotsImg,
                                        String outlineImg) {
        try {
            Field imgField = AbstractPotion.class.getDeclaredField("containerImg");
            imgField.setAccessible(true);
            imgField.set(potion, new Texture(containerImg));
            imgField = AbstractPotion.class.getDeclaredField("liquidImg");
            imgField.setAccessible(true);
            imgField.set(potion, new Texture(liquidImg));
            imgField = AbstractPotion.class.getDeclaredField("hybridImg");
            imgField.setAccessible(true);
            imgField.set(potion, new Texture(hybridImg));
            imgField = AbstractPotion.class.getDeclaredField("spotsImg");
            imgField.setAccessible(true);
            imgField.set(potion, new Texture(spotsImg));
            imgField = AbstractPotion.class.getDeclaredField("outlineImg");
            imgField.setAccessible(true);
            imgField.set(potion, new Texture(outlineImg));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setPotionTexture(AbstractPotion potion, String texture) { //texture指贴图路径
        setPotionTexture(potion, texture, texture, texture, texture, texture);
    }
}

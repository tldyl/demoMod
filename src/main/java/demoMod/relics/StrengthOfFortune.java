package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import demoMod.DemoMod;

public class StrengthOfFortune extends CustomRelic {
    public static final String ID = DemoMod.makeID("StrengthOfFortune");
    public static final String IMG_PATH = "relics/strengthOfFortune.png";

    public StrengthOfFortune() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public float atDamageModify(float damage, AbstractCard card) {
        return 3.0F * damage;
    }
}

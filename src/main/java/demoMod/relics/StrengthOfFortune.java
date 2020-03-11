package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import demoMod.DemoMod;
import demoMod.relics.interfaces.PreDamageGive;

public class StrengthOfFortune extends CustomRelic implements PreDamageGive {
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
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && type == DamageInfo.DamageType.NORMAL) {
            this.flash();
            return 3.0F * damage;
        }
        return damage;
    }
}

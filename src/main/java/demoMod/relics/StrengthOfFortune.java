package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
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
    public int onAttackToChangeDamage(DamageInfo info, int damageAmount) {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS) {
            this.flash();
            return 3 * damageAmount;
        }
        return damageAmount;
    }
}

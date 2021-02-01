package demoMod.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.sounds.DemoSoundMaster;

public class MeatBun extends CustomRelic implements CustomSavable<Boolean> {
    public static final String ID = DemoMod.makeID("MeatBun");
    public static final String IMG_PATH = "relics/meatBun.png";

    public MeatBun() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.SHOP, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        if (!this.usedUp) {
            this.beginLongPulse();
        }
    }

    @Override
    public void onVictory() {
        this.stopPulse();
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.heal(14);
        DemoSoundMaster.playA("RELIC_MEAT_BUN", 0.0F);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (damageAmount > 0 && !this.usedUp) {
            this.flash();
            this.usedUp();
            this.stopPulse();
        }
        return damageAmount;
    }

    @Override
    public float atDamageModify(float damage, AbstractCard c) {
        return this.usedUp ? damage : 2.0F * damage;
    }

    @Override
    public Boolean onSave() {
        return this.usedUp;
    }

    @Override
    public void onLoad(Boolean b) {
        if (b != null) {
            if (b) {
                this.usedUp();
            }
        } else {
            this.usedUp = false;
        }
    }
}

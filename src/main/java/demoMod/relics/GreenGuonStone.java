package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.relics.interfaces.PostBeforePlayerDeath;

public class GreenGuonStone extends CustomRelic implements PostBeforePlayerDeath {
    public static final String ID = DemoMod.makeID("GreenGuonStone");
    public static final String IMG_PATH = "relics/greenGuonStone.png";

    public GreenGuonStone() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.RARE, LandingSound.SOLID);
    }

    @Override
    public void onPlayerEndTurn() {
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, 2));
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0) {
            if (damageAmount < p.currentHealth) {
                if (AbstractDungeon.miscRng.random(99) < 20) {
                    this.flash();
                    p.heal(6);
                }
            }
        }
        return damageAmount;
    }

    @Override
    public void onNearDeath() {
        AbstractPlayer p = AbstractDungeon.player;
        if (AbstractDungeon.miscRng.random(99) < 50) {
            this.flash();
            p.heal(10);
        }
    }
}

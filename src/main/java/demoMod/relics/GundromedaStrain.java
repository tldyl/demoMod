package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;

public class GundromedaStrain extends CustomRelic {
    public static final String ID = DemoMod.makeID("GundromedaStrain");
    public static final String IMG_PATH = "relics/gundromedaStrain.png";

    public GundromedaStrain() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.RARE, LandingSound.FLAT);
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + 25 + this.DESCRIPTIONS[1];
    }

    @Override
    public void atBattleStart() {
        this.flash();
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (AbstractDungeon.player.hasRelic("PreservedInsect") && AbstractDungeon.getCurrRoom().eliteTrigger) {
                if (m.currentHealth > m.maxHealth * 0.5625) {
                    m.currentHealth = (int) (m.maxHealth * 0.5625);
                }
            } else {
                if (m.currentHealth > m.maxHealth * 0.75) {
                    m.currentHealth = (int) (m.maxHealth * 0.75);
                }
            }
            m.healthBarUpdatedEvent();
            this.addToTop(new RelicAboveCreatureAction(m, this));
        }
    }
}

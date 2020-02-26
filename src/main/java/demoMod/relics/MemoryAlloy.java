package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;

public class MemoryAlloy extends CustomRelic {
    public static final String ID = "DemoMod:MemoryAlloy";
    public static final String IMG_PATH = "relics/memoryAlloy.png";
    public static final String OUTLINE_IMG_PATH = "relics/memoryAlloyOutline.png";
    public static boolean activated = true;

    private static final int threshold = 30;

    public MemoryAlloy() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)), new Texture(DemoMod.getResourcePath(OUTLINE_IMG_PATH)),
                RelicTier.UNCOMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        activated = true;
        this.counter = 0;
    }

    @Override
    public void atTurnStart() {
        if (this.counter >= threshold) {
            this.flash();
            this.pulse = false;
            AbstractDungeon.actionManager.addToBottom(new HealAction(AbstractDungeon.player, AbstractDungeon.player, this.counter));
            this.counter = -1;
            activated = false;
        } else if (this.counter != -1) {
            this.counter = 0;
        }
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (activated) {
            this.counter += damageAmount;
            if (this.counter >= threshold) {
                this.pulse = true;
            }
        }
        return damageAmount;
    }

    @Override
    public void onVictory() {
        this.counter = -1;
    }
}

package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.actions.LoseRelicAction;

public class GlassGuonStoneRelic extends CustomRelic {
    public static final String ID = DemoMod.makeID("GlassGuonStoneRelic");
    public static final String IMG_PATH = "relics/glassGuonStoneRelic.png";
    private boolean isOnBattle = false;

    public GlassGuonStoneRelic() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.SPECIAL, LandingSound.CLINK);
        this.counter = 1;
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + 1 + this.DESCRIPTIONS[1];
    }

    public void atBattleStart() {
        this.isOnBattle = true;
    }

    public void onVictory() {
        this.isOnBattle = false;
    }

    @Override
    public void onPlayerEndTurn() {
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, this.counter));
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (isOnBattle && info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0) {
            this.addToBot(new LoseRelicAction(this));
        }
        return damageAmount;
    }
}

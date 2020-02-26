package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.powers.BlueGuonStonePower;

public class BlueGuonStone extends CustomRelic {
    public static final String ID = DemoMod.makeID("BlueGuonStone");
    public static final String IMG_PATH = "relics/blueGuonStone.png";

    private boolean activated = false;

    public BlueGuonStone() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.UNCOMMON, LandingSound.CLINK);
    }

    @Override
    public void atBattleStart() {
        activated = true;
    }

    @Override
    public void onVictory() {
        activated = false;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onPlayerEndTurn() {
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, 2));
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (activated && info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new BlueGuonStonePower(3)));
            activated = false;
        }
        return damageAmount;
    }
}

package demoMod.rewards;

import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.patches.CustomRewardPatch;
import demoMod.relics.GlassGuonStoneRelic;

public class GlassGuonStone extends CustomReward {
    private static Texture glassGuonStone = new Texture(DemoMod.getResourcePath("ui/panel/glassGuonStone.png"));
    private static String name;

    public GlassGuonStone() {
        super(glassGuonStone, name, CustomRewardPatch.GUON_STONE);
    }

    @Override
    public boolean claimReward() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.hasRelic(DemoMod.makeID("GlassGuonStoneRelic"))) {
            p.getRelic(DemoMod.makeID("GlassGuonStoneRelic")).counter++;
            p.getRelic(DemoMod.makeID("GlassGuonStoneRelic")).playLandingSFX();
        } else {
            new GlassGuonStoneRelic().instantObtain();
        }
        this.isDone = true;
        return true;
    }

    static {
        name = CardCrawlGame.languagePack.getUIString(DemoMod.makeID("GuonStone")).TEXT[0];
    }
}

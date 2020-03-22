package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;

public class GoldJunk extends CustomRelic {
    public static final String ID = DemoMod.makeID("GoldJunk");
    public static final String IMG_PATH = "relics/goldJunk.png";

    public GoldJunk() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        if (AbstractDungeon.player.hasRelic(SerJunkan.ID)) {
            AbstractDungeon.player.getRelic(SerJunkan.ID).onTrigger(null);
        }
        CardCrawlGame.sound.play("GOLD_GAIN");
        AbstractDungeon.player.gainGold(500);
    }

    @Override
    public void onUnequip() {
        if (AbstractDungeon.player.hasRelic(SerJunkan.ID)) {
            SerJunkan.LEVEL = 0;
            AbstractDungeon.player.getRelic(SerJunkan.ID).onTrigger();
        }
    }
}

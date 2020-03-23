package demoMod.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import demoMod.DemoMod;
import demoMod.sounds.DemoSoundMaster;

public class WeirdEgg extends AbstractClickRelic {
    public static final String ID = DemoMod.makeID("WeirdEgg");
    public static final String IMG_PATH = "relics/weirdEgg.png";

    public WeirdEgg() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.COMMON, LandingSound.SOLID);
    }

    @Override
    public void onEquip() {
        this.counter = 1;
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        this.counter++;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new WeirdEgg();
    }

    @Override
    protected void onRightClick() {
        DemoSoundMaster.playA("RELIC_WEIRD_EGG", 0.0F);
        if (this.counter < 15) {
            AbstractRelic relic = AbstractDungeon.returnRandomRelic(RelicTier.COMMON);
            relic.instantObtain(AbstractDungeon.player, getIndex(), true);
        } else if (this.counter < 30) {
            AbstractRelic relic = AbstractDungeon.returnRandomRelic(RelicTier.UNCOMMON);
            relic.instantObtain(AbstractDungeon.player, getIndex(), true);
        } else {
            AbstractRelic relic = AbstractDungeon.returnRandomRelic(RelicTier.RARE);
            relic.instantObtain(AbstractDungeon.player, getIndex(), true);
        }
    }

    private int getIndex() {
        int i;
        for (i = 0;i<AbstractDungeon.player.relics.size();i++) {
            if (AbstractDungeon.player.relics.get(i).relicId.equals(WeirdEgg.ID)) {
                break;
            }
        }
        return i;
    }
}

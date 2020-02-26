package demoMod.relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import demoMod.DemoMod;
import demoMod.rewards.GlassGuonStone;
import demoMod.rooms.MimicRoom;
import demoMod.sounds.DemoSoundMaster;

public class Dog extends AbstractClickRelic {
    public static final String ID = DemoMod.makeID("Dog");
    public static final String IMG_PATH = "relics/dog.png";
    public static final double chance = 0.2F;
    private float timeCounter = 0.5F;

    private boolean isMimicRoom = false;

    public Dog() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.STARTER, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onVictory() {
        double ran = AbstractDungeon.treasureRng.random(1.0F);
        if (ran <= chance) {
            this.flash();
            DemoSoundMaster.playA("RELIC_DOG_BARK", 0F);
            ran = AbstractDungeon.treasureRng.random(1.0F);
            if (ran < 0.3F) {
                int gold = 5 + AbstractDungeon.treasureRng.random(45);
                AbstractDungeon.getCurrRoom().addGoldToRewards(gold);
            } else if (ran < 0.6F) {
                AbstractDungeon.getCurrRoom().rewards.add(new GlassGuonStone());
            } else if (ran < 0.8F) {
                RewardItem cardReward = new RewardItem();
                cardReward.cards = AbstractDungeon.getRewardCards();
                AbstractDungeon.getCurrRoom().addCardReward(cardReward);
            } else if (ran < 0.9F) {
                AbstractDungeon.getCurrRoom().addPotionToRewards();
            } else {
                AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier()));
            }
        }
    }

    public void onEnterRoom(AbstractRoom room) {
        isMimicRoom = room instanceof MimicRoom;
    }

    public void atBattleStart() {
        isMimicRoom = false;
    }

    public void update() {
        super.update();
        if (isMimicRoom && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.INCOMPLETE) {
            timeCounter += Gdx.graphics.getDeltaTime();
            if (timeCounter >= 0.5F) {
                timeCounter = 0;
                this.flash();
                DemoSoundMaster.playV("RELIC_DOG_BARK", 0.1F);
            }
        }
    }

    @Override
    public void playLandingSFX() {
        DemoSoundMaster.playV("RELIC_DOG_BARK", 0.1F);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Dog();
    }

    @Override
    protected void onRightClick() {
        this.flash();
        DemoSoundMaster.playV("RELIC_DOG_BARK", 0.1F);
    }
}

package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import demoMod.DemoMod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SevenLeafClover extends CustomRelic {
    public static final String ID = "DemoMod:SevenLeafClover";
    public static final String IMG_PATH = "relics/sevenLeafClover.png";
    public static final String OUTLINE_IMG_PATH = "relics/sevenLeafCloverOutline.png";

    public SevenLeafClover() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)), new Texture(DemoMod.getResourcePath(OUTLINE_IMG_PATH)),
                RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void justEnteredRoom(AbstractRoom room) {
        if ((room instanceof TreasureRoom)) {
            this.flash();
            this.pulse = true;
        } else {
            this.pulse = false;
        }
    }

    @Override
    public void onChestOpen(boolean bossChest) {
        if (!bossChest) {
            this.flash();
            RewardItem reward = new RewardItem();
            Set<AbstractCard> cardSet = new HashSet<>();
            while (cardSet.size() < 3) {
                cardSet.add(AbstractDungeon.getCard(AbstractCard.CardRarity.RARE));
            }
            reward.cards = new ArrayList<>(cardSet);
            AbstractDungeon.getCurrRoom().addCardReward(reward);
            double ran = AbstractDungeon.treasureRng.random(1.0F) * AbstractDungeon.rareRelicPool.size();
            AbstractDungeon.getCurrRoom().addRelicToRewards(RelicLibrary.getRelic(AbstractDungeon.rareRelicPool.get((int)Math.floor(ran))));
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new SevenLeafClover();
    }

    @Override
    public boolean canSpawn() {
        return Settings.isEndless || AbstractDungeon.floorNum <= 34;
    }
}

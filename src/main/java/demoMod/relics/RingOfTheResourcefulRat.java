package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.rewards.RewardItem;
import demoMod.DemoMod;

import java.util.*;

public class RingOfTheResourcefulRat extends CustomRelic {
    public static final String ID = DemoMod.makeID("RingOfTheResourcefulRat");
    public static final String IMG_PATH = "relics/ringOfTheResourcefulRat.png";
    private List<RewardItem> itemList = new ArrayList<>();

    public RingOfTheResourcefulRat() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.COMMON, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + this.DESCRIPTIONS[1] + this.DESCRIPTIONS[2] + this.DESCRIPTIONS[3] + this.DESCRIPTIONS[4];
    }

    public void onTrigger(ItemType type, Object item) {
        switch (type) {
            case GOLD:
                break;
            case CARD:
                AbstractCard card = (AbstractCard) item;
                AbstractCard.CardRarity rarity = card.rarity;
                if (rarity == AbstractCard.CardRarity.BASIC || rarity == AbstractCard.CardRarity.SPECIAL) {
                    rarity = AbstractCard.CardRarity.COMMON;
                }
                Set<AbstractCard> tmpSet = new HashSet<>();
                int numOfCards = 3;
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    numOfCards = relic.changeNumberOfCardsInReward(numOfCards);
                }
                if (ModHelper.isModEnabled("Binary")) {
                    --numOfCards;
                }
                while (tmpSet.size() < numOfCards) {
                    if (!AbstractDungeon.player.hasRelic(PrismaticShard.ID)) {
                        tmpSet.add(AbstractDungeon.getCard(rarity));
                    } else {
                        AbstractCard.CardType cardType;
                        List<AbstractCard.CardType> cardTypeList = new ArrayList<>();
                        cardTypeList.add(AbstractCard.CardType.ATTACK);
                        cardTypeList.add(AbstractCard.CardType.SKILL);
                        cardTypeList.add(AbstractCard.CardType.POWER);
                        Collections.shuffle(cardTypeList, AbstractDungeon.cardRng.random);
                        cardType = cardTypeList.get(0);
                        tmpSet.add(CardLibrary.getAnyColorCard(cardType, rarity));
                    }
                }
                RewardItem reward = new RewardItem();
                Set<AbstractCard> cardSet = new HashSet<>();
                for (AbstractCard card1 : tmpSet) {
                    AbstractCard card2 = card1.makeCopy();
                    if (card2.canUpgrade()) {
                        card2.upgrade();
                    }
                    cardSet.add(card2);
                }
                reward.cards = new ArrayList<>(cardSet);
                if (!reward.cards.isEmpty()) {
                    itemList.add(reward);
                }
                break;
            case POTION:
                AbstractPotion potion = (AbstractPotion) item;
                AbstractPotion another = PotionHelper.getRandomPotion();
                while (another.rarity != potion.rarity) {
                    another = PotionHelper.getRandomPotion();
                }
                itemList.add(new RewardItem(another));
                break;
            case RELIC:
                AbstractRelic relic = (AbstractRelic) item;
                if (relic.tier != RelicTier.SPECIAL && relic.tier != RelicTier.STARTER) {
                    itemList.add(new RewardItem(AbstractDungeon.returnRandomRelic(relic.tier)));
                } else {
                    switch (relic.tier) {
                        case SPECIAL:
                            itemList.add(new RewardItem(RelicLibrary.specialList.get(AbstractDungeon.relicRng.random(RelicLibrary.specialList.size() - 1)).makeCopy()));
                            break;
                        case STARTER:
                            itemList.add(new RewardItem(RelicLibrary.starterList.get(AbstractDungeon.relicRng.random(RelicLibrary.starterList.size() - 1)).makeCopy()));
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void onVictory() {
        if (itemList.size() > 0) this.flash();
        for (RewardItem item : itemList) {
            AbstractDungeon.getCurrRoom().rewards.add(item);
        }
        itemList.clear();
    }

    @Override
    public boolean canSpawn() {
        return DemoMod.enableResourcefulRatThief;
    }

    public enum ItemType {
        GOLD,
        CARD,
        POTION,
        RELIC
    }
}

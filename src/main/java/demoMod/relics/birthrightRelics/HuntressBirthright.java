package demoMod.relics.birthrightRelics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

import java.util.ArrayList;
import java.util.List;

public class HuntressBirthright extends CustomRelic {
    public static final String ID = DemoMod.makeID("HuntressBirthright");

    public HuntressBirthright() {
        super(ID, "", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        AbstractDungeon.getCurrRoom().rewards = new ArrayList<>();
        List<AbstractCard> gunCards = new ArrayList<>();
        for (AbstractCard card : CardLibrary.getAllCards()) {
            if (card instanceof AbstractGunCard) {
                gunCards.add(card);
            }
        }
        List<AbstractCard> tmp = new ArrayList<>(gunCards);
        for (int i=0;i<8;i++) {
            if (AbstractDungeon.cardRng.randomBoolean()) {
                List<AbstractCard> rewardCard = new ArrayList<>();
                float roll = AbstractDungeon.cardRng.random(1.0F);
                for (int j=0;j<3;j++) {
                    AbstractCard c = tmp.get(AbstractDungeon.cardRng.random(0, tmp.size() - 1));
                    if (roll < 0.15F) {
                        while (c.rarity != AbstractCard.CardRarity.RARE) {
                            c = tmp.get(AbstractDungeon.cardRng.random(0, tmp.size() - 1));
                        }
                    } else if (roll < 0.69F) {
                        while (c.rarity != AbstractCard.CardRarity.UNCOMMON) {
                            c = tmp.get(AbstractDungeon.cardRng.random(0, tmp.size() - 1));
                        }
                    } else {
                        while (c.rarity != AbstractCard.CardRarity.COMMON) {
                            c = tmp.get(AbstractDungeon.cardRng.random(0, tmp.size() - 1));
                        }
                    }
                    tmp.remove(c);
                    rewardCard.add(c.makeCopy());
                }
                tmp = new ArrayList<>(gunCards);
                RewardItem rewardItem = new RewardItem();
                rewardItem.cards = new ArrayList<>(rewardCard);
                AbstractDungeon.getCurrRoom().addCardReward(rewardItem);
            } else {
                float roll = AbstractDungeon.relicRng.random(1.0F);
                AbstractRelic relic;
                if (roll < 0.15F) {
                    relic = AbstractDungeon.returnRandomRelic(RelicTier.RARE);
                } else if (roll < 0.69F) {
                    relic = AbstractDungeon.returnRandomRelic(RelicTier.UNCOMMON);
                } else {
                    relic = AbstractDungeon.returnRandomRelic(RelicTier.COMMON);
                }
                AbstractDungeon.getCurrRoom().addRelicToRewards(relic);
            }
        }
        AbstractDungeon.combatRewardScreen.open();
    }
}

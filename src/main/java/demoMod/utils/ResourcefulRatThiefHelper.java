package demoMod.utils;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.dto.ResourcefulRatThiefData;

import java.util.ArrayList;

public class ResourcefulRatThiefHelper implements CustomSavable<ResourcefulRatThiefData> {
    private ResourcefulRatThiefData data;
    private static ResourcefulRatThiefHelper inst = null;

    private ResourcefulRatThiefHelper() {
        this.data = new ResourcefulRatThiefData();
        data.cards = new ArrayList<>();
        data.cardMisc = new ArrayList<>();
        data.cardSaves = new ArrayList<>();
        data.potions = new ArrayList<>();
        data.relics = new ArrayList<>();
        data.relicCounters = new ArrayList<>();
        data.relicSaves = new ArrayList<>();
        BaseMod.addSaveField("ResourcefulRatThiefHelper", this);
    }

    public static ResourcefulRatThiefHelper getInstance() {
        if (inst == null) {
            inst = new ResourcefulRatThiefHelper();
        }
        return inst;
    }

    public static void reset() {
        inst = null;
    }

    public void addCard(AbstractCard card) {
        data.cards.add(card.cardID);
        data.cardMisc.add(card.misc);
        if (card instanceof CustomSavable) {
            data.cardSaves.add(((CustomSavable)card).onSaveRaw());
        } else {
            data.cardSaves.add(null);
        }
    }

    public void addGold(int goldAmt) {
        data.gold += goldAmt;
    }

    public void addPotion(AbstractPotion potion) {
        data.potions.add(potion.ID);
    }

    public void addRelic(AbstractRelic relic) {
        data.relics.add(relic.relicId);
        data.relicCounters.add(relic.counter);
        if (relic instanceof CustomSavable) {
            data.relicSaves.add(((CustomSavable)relic).onSaveRaw());
        } else {
            data.relicSaves.add(null);
        }
    }

    public ResourcefulRatThiefData getData() {
        return data;
    }

    @Override
    public ResourcefulRatThiefData onSave() {
        return data;
    }

    @Override
    public void onLoad(ResourcefulRatThiefData data) {
        if (data != null) {
            this.data = data;
        }
    }
}

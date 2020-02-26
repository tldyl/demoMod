package demoMod.relics.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

@SuppressWarnings("UnnecessaryInterfaceModifier")
public interface PostCardAddedToDrawPileSubscriber {
    public void onCardAddedToDrawPile(AbstractCard card);
}

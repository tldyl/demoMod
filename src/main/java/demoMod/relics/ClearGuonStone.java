package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.relics.interfaces.PostCardAddedToDrawPileSubscriber;

public class ClearGuonStone extends CustomRelic implements PostCardAddedToDrawPileSubscriber,
                                                           Combo {
    public static final String ID = DemoMod.makeID("ClearGuonStone");
    public static final String IMG_PATH = "relics/clearGuonStone.png";
    private static boolean isCombo = false;

    private boolean isRemoving = false;

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/clearGuonStone.png"));

    public ClearGuonStone() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.COMMON, LandingSound.SOLID);
    }

    @Override
    public void onEquip() {
        ComboManager.detectComboInGame();
    }

    @Override
    public void onUnequip() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public void onPlayerEndTurn() {
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, 2));
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onCardAddedToDrawPile(AbstractCard card) {
        if (isCombo) {
            if (card.type == AbstractCard.CardType.STATUS) {
                AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(card, AbstractDungeon.player.drawPile, true));
                AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(card, AbstractDungeon.player.hand, true));
            }
        } else {
            if (card instanceof Slimed || card instanceof Burn) {
                AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(card, AbstractDungeon.player.drawPile, true));
                AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(card, AbstractDungeon.player.hand, true));
            }
        }
    }

    @Override
    public void atTurnStartPostDraw() {
        for (AbstractCard card : AbstractDungeon.player.drawPile.group) {
            onCardAddedToDrawPile(card);
        }
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            onCardAddedToDrawPile(card);
        }
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        isCombo = true;
    }

    @Override
    public void onComboDisabled(String comboId) {
        isCombo = false;
    }

    @Override
    public boolean isRemoving() {
        return isRemoving;
    }

    @Override
    public Texture getComboPortrait() {
        return comboTexture;
    }

    static {
        ComboManager.addCombo(DemoMod.makeID("ClearerGuonStone:Bottle"), ClearGuonStone.class);
        ComboManager.addCombo(DemoMod.makeID("ClearerGuonStone:Plus1Bullets"), ClearGuonStone.class);
    }
}

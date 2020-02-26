package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;

public class Ammonomicon extends CustomRelic {
    public static final String ID = DemoMod.makeID("Ammonomicon");
    public static final String IMG_PATH = "relics/ammonomicon.png";

    public Ammonomicon() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        this.flash();
        AbstractCard card = DemoMod.getRandomBulletCard();
        card.costForTurn = 0;
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card));
    }
}

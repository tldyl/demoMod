package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;

import java.util.ArrayList;
import java.util.List;

public class SlingerPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("SlingerPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public SlingerPower(int amount) {
        this.ID = POWER_ID;
        this.owner = AbstractDungeon.player;
        this.name = NAME;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/Slinger84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/Slinger32.png")), 0, 0, 32, 32);
        this.amount = amount;
        this.updateDescription();
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void onUseCard(AbstractCard c, UseCardAction action) {
        if (c instanceof AbstractGunCard) {
            this.flash();
            if (AbstractDungeon.player.hand.size() < Settings.MAX_HAND_SIZE) {
                for (int i = 0; i < Math.min(this.amount, Settings.MAX_HAND_SIZE); i++) {
                    List<AbstractCard> skillsInDraw = new ArrayList<>();
                    List<AbstractCard> skillsInDiscard = new ArrayList<>();
                    for (AbstractCard card : AbstractDungeon.player.drawPile.group) {
                        if (card.type == AbstractCard.CardType.SKILL) {
                            skillsInDraw.add(card);
                        }
                    }
                    for (AbstractCard card : AbstractDungeon.player.discardPile.group) {
                        if (card.type == AbstractCard.CardType.SKILL) {
                            skillsInDiscard.add(card);
                        }
                    }
                    skillsInDiscard.addAll(skillsInDraw);
                    if (skillsInDiscard.size() > 0) {
                        AbstractCard card = getRandomCard(skillsInDiscard);
                        if (AbstractDungeon.player.drawPile.contains(card)) {
                            AbstractDungeon.player.drawPile.moveToHand(card);
                        } else {
                            AbstractDungeon.player.discardPile.moveToHand(card);
                        }
                    }
                }
            } else {
                AbstractDungeon.player.createHandIsFullDialog();
            }
        }
    }

    private AbstractCard getRandomCard(List<AbstractCard> cards) {
        return cards.get(AbstractDungeon.cardRng.random(cards.size() - 1));
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(DemoMod.makeID("SlingerPower"));
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

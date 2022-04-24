package demoMod.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.combat.PowerDebuffEffect;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;
import demoMod.DemoMod;
import demoMod.powers.PreparedPower;
import demoMod.relics.GnawedKey;
import demoMod.relics.RingOfTheResourcefulRat;
import demoMod.utils.ResourcefulRatThiefHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressWarnings("Duplicates")
public class ResourcefulRatThief extends AbstractMonster {
    public static final String ID = DemoMod.makeID("ResourcefulRatThief");
    private static final MonsterStrings monsterStrings;
    public static String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final int HP_MAX = 148;
    private static final float HB_X = 0.0F;
    private static final float HB_Y = 10.0F;
    private static final float HB_W = 90.0F;
    private static final float HB_H = 60.0F;
    private static final String IMG_PATH = DemoMod.getResourcePath("monsters/resourcefulRat.png");

    private boolean draggingMode = false;
    private float dragFadeOut = 0.0F;
    private int fadeOutX;
    private int fadeOutY;

    public ResourcefulRatThief(float offsetX, float offsetY) {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, IMG_PATH, offsetX, offsetY);
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new PreparedPower(this)));
    }

    @Override
    public void takeTurn() {
        int rng = AbstractDungeon.monsterRng.random(99);
        AbstractPlayer p = AbstractDungeon.player;
        RingOfTheResourcefulRat ring = (RingOfTheResourcefulRat) p.getRelic(RingOfTheResourcefulRat.ID);
        if (rng < 25) {
            int goldAmount = Math.min(30, p.gold);
            p.gold -= goldAmount;
            if (goldAmount > 0) {
                CardCrawlGame.sound.play("GOLD_JINGLE");
                for (AbstractRelic relic : p.relics) {
                    relic.onLoseGold();
                }
                for(int i = 0; i < goldAmount; i++) {
                    AbstractDungeon.effectList.add(new GainPennyEffect(this, p.hb.cX, p.hb.cY, this.hb.cX, this.hb.cY, false));
                }
                ResourcefulRatThiefHelper.getInstance().addGold(goldAmount);
                if (ring != null) {
                    ring.onTrigger(RingOfTheResourcefulRat.ItemType.GOLD, null);
                }
            }
        } else if (rng < 50 && p.masterDeck.size() > 0) {
            ArrayList<AbstractCard> cards = CardGroup.getGroupWithoutBottledCards(p.masterDeck).group;
            List<AbstractCard> toRemove = new ArrayList<>();
            for (AbstractCard card1 : cards) {
                if (card1.type == AbstractCard.CardType.CURSE || card1.type == AbstractCard.CardType.STATUS) {
                    toRemove.add(card1);
                }
            }
            cards.removeAll(toRemove);
            if (cards.size() > 0) {
                AbstractCard card = cards.get(AbstractDungeon.miscRng.random(cards.size() - 1));
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(), MathUtils.random(0.4F, 0.6F) * (float) Settings.WIDTH, MathUtils.random(0.4F, 0.6F) * (float) Settings.HEIGHT));
                p.masterDeck.removeCard(card);
                ResourcefulRatThiefHelper.getInstance().addCard(card);
                if (ring != null) {
                    ring.onTrigger(RingOfTheResourcefulRat.ItemType.CARD, card);
                }
            }
        } else if (rng < 75 && p.hasAnyPotions()) {
            AbstractPotion potion = p.getRandomPotion();
            AbstractDungeon.effectList.add(new PowerDebuffEffect(p.hb.cX - p.animX, p.hb.cY + p.hb.height / 2.0F, DIALOG[0] + potion.name));
            p.removePotion(potion);
            ResourcefulRatThiefHelper.getInstance().addPotion(potion);
            if (ring != null) {
                ring.onTrigger(RingOfTheResourcefulRat.ItemType.POTION, potion);
            }
        } else {
            if (p.relics.size() > 1) {
                ArrayList<AbstractRelic> relics = new ArrayList<>(p.relics);
                if (ring != null) {
                    relics.remove(ring);
                }
                Collections.shuffle(relics, new Random(AbstractDungeon.miscRng.randomLong()));
                if (relics.get(0).relicId.equals(GnawedKey.ID)) {
                    relics.remove(0);
                }
                if (relics.size() > 0) {
                    p.loseRelic(relics.get(0).relicId);
                    AbstractDungeon.effectList.add(new PowerDebuffEffect(p.hb.cX - p.animX, p.hb.cY + p.hb.height / 2.0F, DIALOG[0] + relics.get(0).name));
                    ResourcefulRatThiefHelper.getInstance().addRelic(relics.get(0));
                    if (ring != null) {
                        ring.onTrigger(RingOfTheResourcefulRat.ItemType.RELIC, relics.get(0));
                    }
                }
            }
        }
        addToBot(new WaitAction(0.3F));
        addToBot(new VFXAction(new SmokeBombEffect(this.hb.cX, this.hb.cY)));
        addToBot(new EscapeAction(this));
    }

    @Override
    protected void getMove(int i) {
        setMove((byte) 0, Intent.STRONG_DEBUFF);
    }

    @Override
    public void update() {
        if (this.hb != null) {
            if (this.hb.hovered && InputHelper.isMouseDown && !draggingMode) {
                draggingMode = true;
                dragFadeOut = 0.7F;
            }
            if (draggingMode && !InputHelper.isMouseDown) {
                draggingMode = false;
                fadeOutX = (int)((InputHelper.mX - this.drawX) / 2 + this.drawX);
                fadeOutY = (int)((InputHelper.mY - this.drawY - this.hb_h / 2.0F) / 2 + this.drawY);
            }
            if (draggingMode) {
                this.drawX += this.calculateVelocity((int)this.drawX, InputHelper.mX);
                this.drawY += this.calculateVelocity((int)this.drawY, (int)(InputHelper.mY - this.hb_h / 2.0F));
                this.hb.move(this.drawX + this.hb_x + this.animX, this.drawY + this.hb_y + this.hb_h / 2.0F);
                this.healthHb.move(this.hb.cX, this.hb.cY - this.hb_h / 2.0F - this.healthHb.height / 2.0F);
                this.intentHb.move(this.hb.cX + this.intentOffsetX, this.hb.cY + this.hb_h / 2.0F + 32.0F * Settings.scale);
            } else if (dragFadeOut > 0) {
                this.drawX += this.calculateVelocity((int)this.drawX, fadeOutX);
                this.drawY += this.calculateVelocity((int)this.drawY, fadeOutY);
                this.hb.move(this.drawX + this.hb_x + this.animX, this.drawY + this.hb_y + this.hb_h / 2.0F);
                this.healthHb.move(this.hb.cX, this.hb.cY - this.hb_h / 2.0F - this.healthHb.height / 2.0F);
                this.intentHb.move(this.hb.cX + this.intentOffsetX, this.hb.cY + this.hb_h / 2.0F + 32.0F * Settings.scale);
                this.dragFadeOut -= Gdx.graphics.getDeltaTime();
            }
        }
        super.update();
    }

    private int calculateVelocity(int src, int target) {
        return (target - src) / 10;
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}

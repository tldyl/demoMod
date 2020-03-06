package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import demoMod.DemoMod;
import demoMod.actions.BoxingGloveAction;
import demoMod.cards.interfaces.ChargeCard;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.characters.HuntressCharacter;
import demoMod.dto.BoxingGloveData;
import demoMod.dto.GunCardSaveData;
import demoMod.sounds.DemoSoundMaster;

import java.lang.reflect.Type;

@SuppressWarnings("Duplicates")
public class BoxingGlove extends AbstractGunCard implements ChargeCard, PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("BoxingGlove");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/boxingGlove.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    public int stars = 0;
    private boolean isAdded = false;
    private TextureRegion star = new TextureRegion(new Texture(DemoMod.getResourcePath("effects/boxingGloveStar.png")));

    private static final int COST = 1;

    public BoxingGlove() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 9;
        this.capacity = 6;
        this.maxCapacity = 6;
        this.reloadSoundKey = "";
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        if (this.stars >= 1 && EnergyPanel.getCurrentEnergy() >= this.costForTurn + 1) {
            select(m);
        } else {
            onNonCharge(p, m);
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(3);
        }
    }

    @SpireOverride
    protected void renderPortrait(SpriteBatch sb) {
        SpireSuper.call(sb);
        float drawX = this.current_x - 25.5F;
        float drawY = this.current_y + 55.5F;
        for (int i=1;i<=this.stars;i++) {
            sb.draw(star, drawX, drawY, 9.0F, 9.0F, 18.0F, 18.0F, this.drawScale * Settings.scale, this.drawScale * Settings.scale, this.angle);
            drawX += 21 * this.drawScale;
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }

    @Override
    public void onCharge(AbstractPlayer p, AbstractMonster m) {
        int baseDamage = this.baseDamage;
        this.baseDamage *= Math.pow(2, this.stars);
        this.calculateCardDamage(m);
        this.addToBot(new BoxingGloveAction(this, m, this.damage, true));
        this.baseDamage = baseDamage;
        this.stars = 0;
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card instanceof BoxingGlove) {
                BoxingGlove glove = (BoxingGlove) card;
                glove.stars = 0;
            }
        }
        EnergyPanel.setEnergy(EnergyPanel.getCurrentEnergy() - 1);
    }

    @Override
    public void onNonCharge(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new BoxingGloveAction(this, m, this.damage, false));
    }

    @Override
    public void onAddedToMasterDeck() {
        if (!isAdded) {
            DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                HuntressCharacter.curse += 1;
            }
            isAdded = true;
        }
    }

    @Override
    public void onRemoveFromMasterDeck() {
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse -= 1;
        }
    }

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard card = super.makeStatEquivalentCopy();
        BoxingGlove glove = (BoxingGlove) card;
        glove.stars = this.stars;
        return glove;
    }

    @Override
    public GunCardSaveData onSave() {
        BoxingGloveData data = new BoxingGloveData();
        data.capacity = this.capacity;
        data.maxCapacity = this.maxCapacity;
        data.stars = this.stars;
        return data;
    }

    @Override
    public void onLoad(GunCardSaveData saveData) {
        if (saveData != null) {
            BoxingGloveData data = (BoxingGloveData) saveData;
            this.capacity = data.capacity;
            this.maxCapacity = data.maxCapacity;
            this.stars = data.stars;
            if (this.capacity == 0) this.target = CardTarget.NONE;
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<BoxingGloveData>(){}.getType();
    }
}

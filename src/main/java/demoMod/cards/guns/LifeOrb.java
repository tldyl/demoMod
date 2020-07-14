package demoMod.cards.guns;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.actions.EmptyVesselsAction;
import demoMod.actions.LifeOrbAction;
import demoMod.cards.interfaces.MultiAttackCard;
import demoMod.dto.GeneralGunCardData;
import demoMod.dto.GunCardSaveData;
import demoMod.dto.LifeOrbData;

import java.lang.reflect.Type;

public class LifeOrb extends AbstractGunCard implements MultiAttackCard, CustomSavable<GunCardSaveData> {
    public static final String ID = DemoMod.makeID("LifeOrb");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/lifeOrb.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;

    public int reloadDamage = 0;
    private int multi = 6;

    public LifeOrb() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 1;
        this.capacity = 3;
        this.maxCapacity = 3;
        this.reloadSoundKey = "";
    }

    @Override
    public void reload() {
        super.reload();
        autoReload(AbstractDungeon.player, null);
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
        if (this.reloadDamage > 0) {
            AbstractDungeon.actionManager.addToBottom(new EmptyVesselsAction(this.reloadDamage));
            this.reloadDamage = 0;
            this.portrait = new TextureAtlas.AtlasRegion(new Texture(DemoMod.getResourcePath("cards/lifeOrb.png")), 0, 0, 250, 190);
            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                if (card.uuid.equals(this.uuid)) {
                    LifeOrb lifeOrb = (LifeOrb)card;
                    lifeOrb.reloadDamage = 0;
                    lifeOrb.portrait = this.portrait;
                    break;
                }
            }
        }
    }

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard card = this.makeCopy();

        for (int i = 0; i < this.timesUpgraded; ++i) {
            card.upgrade();
        }

        card.name = this.name;
        card.target = this.target;
        card.upgraded = this.upgraded;
        card.timesUpgraded = this.timesUpgraded;
        card.baseDamage = this.baseDamage;
        card.baseBlock = this.baseBlock;
        card.baseMagicNumber = this.baseMagicNumber;
        card.cost = this.cost;
        card.costForTurn = this.costForTurn;
        card.isCostModified = this.isCostModified;
        card.isCostModifiedForTurn = this.isCostModifiedForTurn;
        card.inBottleLightning = this.inBottleLightning;
        card.inBottleFlame = this.inBottleFlame;
        card.inBottleTornado = this.inBottleTornado;
        card.isSeen = this.isSeen;
        card.isLocked = this.isLocked;
        card.misc = this.misc;
        card.freeToPlayOnce = this.freeToPlayOnce;
        LifeOrb lifeOrb = (LifeOrb)card;
        lifeOrb.portrait = this.portrait;
        lifeOrb.reloadDamage = this.reloadDamage;
        if (this.reloadDamage > 30) lifeOrb.reloadDamage = 30;

        return card;
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new LifeOrbAction(this.damage, this.multi, this, m));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(1);
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }

    @Override
    public int getMulti() {
        return multi;
    }

    @Override
    public boolean isMultiModified() {
        return false;
    }

    @Override
    public GunCardSaveData onSave() {
        GeneralGunCardData data = new LifeOrbData();
        ((LifeOrbData)data).reloadDamage = this.reloadDamage;
        data.capacity = this.capacity;
        data.maxCapacity = this.maxCapacity;
        return data;
    }

    @Override
    public void onLoad(GunCardSaveData i) {
        super.onLoad(i);
        if (i != null) {
            this.reloadDamage = ((LifeOrbData)i).reloadDamage;
            if (this.reloadDamage > 0) {
                this.portrait = new TextureAtlas.AtlasRegion(new Texture(DemoMod.getResourcePath("cards/lifeOrb_saved.png")), 0, 0, 250, 190);
            }
        } else {
            this.reloadDamage = 0;
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<LifeOrbData>(){}.getType();
    }
}

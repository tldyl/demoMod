package demoMod.cards.guns;

import basemod.abstracts.CustomCard;
import basemod.abstracts.CustomSavable;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.DemoMod;
import demoMod.cards.ConditionalReflex;
import demoMod.dto.GeneralGunCardData;
import demoMod.dto.GunCardSaveData;
import demoMod.interfaces.PostReloadSubscriber;
import demoMod.monsters.Decoy;
import demoMod.powers.ChanceBulletsPower;
import demoMod.powers.GunslingerPower;
import demoMod.powers.SlingerPower;
import demoMod.relics.CrisisStone;
import demoMod.relics.ElasticCartridgeClip;
import demoMod.sounds.DemoSoundMaster;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractGunCard extends CustomCard implements CustomSavable<GunCardSaveData> {
    public static final CardType type = CardType.ATTACK;
    public int capacity = 0;
    public int extraDamage = 0;
    public int maxCapacity = 0;
    public boolean isReload = false;
    public boolean canFullReload = false;
    private static boolean redirected = false;
    public CardTarget defaultTarget;
    public String reloadSoundKey = "GUN_RELOAD";
    public boolean isMaxCapacityModified = false;
    public boolean isSemiAutomatic = false;
    public boolean isSemiAutomaticForTurn = false;

    public AbstractGunCard(String id, String name, String img, int cost, String rawDescription, CardColor color, CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
        this.defaultTarget = target;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!redirected && this.capacity > 0 && p.hasPower(DemoMod.makeID("ChanceBulletsPower"))) {
            List<AbstractCard> cards = new ArrayList<>();
            for (AbstractCard c : p.drawPile.group) {
                if (c instanceof AbstractGunCard) cards.add(c);
            }
            for (AbstractCard c : p.hand.group) {
                if (c instanceof AbstractGunCard) cards.add(c);
            }
            for (AbstractCard c : p.discardPile.group) {
                if (c instanceof AbstractGunCard) cards.add(c);
            }
            cards.remove(this);
            if (cards.size() > 0) {
                int ran = AbstractDungeon.miscRng.random(99);
                if (ran < p.getPower(DemoMod.makeID("ChanceBulletsPower")).amount) {
                    p.getPower(DemoMod.makeID("ChanceBulletsPower")).flash();
                    redirected = true;
                    ran = AbstractDungeon.cardRandomRng.random(cards.size() - 1);
                    if (this.target != CardTarget.ENEMY) {
                        m = AbstractDungeon.getRandomMonster(AbstractDungeon.getCurrRoom().monsters.getMonster(Decoy.ID));
                    }
                    cards.get(ran).calculateCardDamage(m);
                    cards.get(ran).use(p, m);
                    redirected = false;
                    if (!((ChanceBulletsPower)p.getPower(DemoMod.makeID("ChanceBulletsPower"))).upgraded) return;
                }
            }
        }
        if (!redirected) this.capacity -= 1;

        if (p.hasPower(GunslingerPower.POWER_ID)) {
            autoReload(p, m);
            afterReload();
        }

        if (this.capacity < 0) {
            this.capacity = this.maxCapacity;
            this.isReload = true;
            this.target = this.defaultTarget;
            if (p.hasRelic(DemoMod.makeID("CrisisStone"))) {
                ((CrisisStone)p.getRelic(DemoMod.makeID("CrisisStone"))).onReload(1);
            }
            if (this.cardID.equals(DemoMod.makeID("DirectionalPad"))) {
                this.exhaust = true;
            }
        } else if (this.capacity == 0 && !redirected) {
            if (!p.hasRelic("DemoMod:HipHolster")
                    && !p.hasPower(SlingerPower.POWER_ID)
                    && !this.cardID.equals(DemoMod.makeID("DirectionalPad"))) {
                this.target = CardTarget.NONE;
            }
        }
        if (!isReload || p.hasRelic("DemoMod:HipHolster") || p.hasPower(SlingerPower.POWER_ID)) {
            this.returnToHand = (this.isSemiAutomatic || this.isSemiAutomaticForTurn) && this.costForTurn > 0;
            if (!this.returnToHand) {
                this.isSemiAutomaticForTurn = false;
            }
            fire(p, m);
            if (isReload && p.hasRelic("DemoMod:HipHolster")) {
                p.getRelic("DemoMod:HipHolster").flash();
                if (!p.hasPower(SlingerPower.POWER_ID)) {
                    DemoSoundMaster.playA(this.reloadSoundKey, 0F);
                    isReload = false;
                }
                autoReload(p, m);
                afterReload();
            }
        } else {
            autoReload(p, m);
            afterReload();
            DemoSoundMaster.playA(this.reloadSoundKey, 0F);
            isReload = false;
        }
    }

    void afterReload() {
        AbstractPlayer p = AbstractDungeon.player;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof PostReloadSubscriber) {
                PostReloadSubscriber reloadSubscriber = (PostReloadSubscriber) relic;
                reloadSubscriber.onReload();
            }
        }
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power instanceof PostReloadSubscriber) {
                PostReloadSubscriber reloadSubscriber = (PostReloadSubscriber) power;
                reloadSubscriber.onReload();
            }
        }
        ArrayList<AbstractCard> cards = new ArrayList<>();
        cards.addAll(AbstractDungeon.player.drawPile.group);
        cards.addAll(AbstractDungeon.player.hand.group);
        cards.addAll(AbstractDungeon.player.discardPile.group);
        for (AbstractCard card : cards) {
            if (card instanceof PostReloadSubscriber) {
                PostReloadSubscriber reloadSubscriber = (PostReloadSubscriber) card;
                reloadSubscriber.onReload();
            }
        }
        List<AbstractCard> reflexes = new ArrayList<>();
        for (AbstractCard card : p.discardPile.group) {
            if (card instanceof ConditionalReflex) {
                reflexes.add(card);
            }
        }
        for (AbstractCard card : reflexes) {
            if (p.hand.size() < Settings.MAX_HAND_SIZE) {
                p.discardPile.moveToHand(card);
            } else {
                p.createHandIsFullDialog();
                break;
            }
        }
        if (reflexes.size() > 0) p.hand.refreshHandLayout();
    }

    /**
     * 这把枪被手动填装时触发此方法。
     */
    public void reload() {
        if (this.capacity == this.maxCapacity) return;
        this.capacity = this.maxCapacity;
        DemoSoundMaster.playA(this.reloadSoundKey, 0F);
        if (AbstractDungeon.player.hasRelic("DemoMod:HipHolster")) {
            AbstractDungeon.player.getRelic("DemoMod:HipHolster").flash();
            AbstractMonster m = AbstractDungeon.getRandomMonster();
            fire(AbstractDungeon.player, m);
        }
        afterReload();
    }

    /**
     * 这把枪被自动填装时触发此方法。
     */
    protected abstract void autoReload(AbstractPlayer p, AbstractMonster m);

    /**
     * 这把枪开火时触发此方法。
     */
    public abstract void fire(AbstractPlayer p, AbstractMonster m);

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

        AbstractGunCard gunCard = (AbstractGunCard) card;
        gunCard.capacity = this.capacity;
        gunCard.maxCapacity = this.maxCapacity;
        if (this.capacity <= 0) {
            if (!AbstractDungeon.player.hasRelic("DemoMod:HipHolster")
                    && !AbstractDungeon.player.hasPower(DemoMod.makeID("SlingerPower"))) {
                this.target = CardTarget.NONE;
            }
        }
        return card;
    }

    public void clearAmmo() {
        this.capacity = 0;
        if (!AbstractDungeon.player.hasRelic("DemoMod:HipHolster")
                && !AbstractDungeon.player.hasPower(DemoMod.makeID("SlingerPower"))) {
            this.target = CardTarget.NONE;
        }
    }

    public int getDisplayedCapacity() {
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(ElasticCartridgeClip.ID)) {
            return 99;
        }
        return this.capacity;
    }

    @Override
    public GunCardSaveData onSave() {
        GeneralGunCardData data = new GeneralGunCardData();
        data.capacity = this.capacity;
        data.maxCapacity = this.maxCapacity;
        return data;
    }

    @Override
    public void onLoad(GunCardSaveData saveData) {
        if (saveData != null) {
            GeneralGunCardData gunCardData = (GeneralGunCardData)saveData;
            this.capacity = gunCardData.capacity;
            this.maxCapacity = gunCardData.maxCapacity;
            if (this.capacity == 0) this.target = CardTarget.NONE;
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<GeneralGunCardData>(){}.getType();
    }
}

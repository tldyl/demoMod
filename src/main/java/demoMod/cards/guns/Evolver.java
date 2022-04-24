package demoMod.cards.guns;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.dto.EvolverData;
import demoMod.dto.GunCardSaveData;
import demoMod.sounds.DemoSoundMaster;

import java.lang.reflect.Type;

@SuppressWarnings("Duplicates")
public class Evolver extends AbstractGunCard implements Combo,
        PostAddedToMasterDeckSubscriber,
        CustomSavable<GunCardSaveData> {
    public static final String ID = DemoMod.makeID("Evolver");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/evolver_1.png";

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/evolver.png"));

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;
    private static boolean[] combos = new boolean[]{false, false};

    private boolean isRemoving = false;

    private int level = 1;
    private int exp = 0;

    private static int EXP_FOR_LEVEL_UP = 6;

    public Evolver() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 6;
        this.capacity = 6;
        this.maxCapacity = 6;
    }

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard card = super.makeStatEquivalentCopy();
        Evolver evolver = (Evolver)card;
        evolver.level = this.level;
        evolver.exp = this.exp;
        evolver.capacity = this.capacity;
        evolver.maxCapacity = this.maxCapacity;
        evolver.defaultTarget = this.defaultTarget;
        evolver.target = this.target;
        if (this.capacity <= 0) evolver.target = CardTarget.NONE;
        return evolver;
    }

    private void levelUp() {
        this.exp = 0;
        if (this.level >= 6) {
            return;
        }
        this.level++;
        if (this.level == 6) this.upgraded = true;
        modeShift();
        this.capacity = this.maxCapacity;
    }

    private void modeShift() {
        if (combos[0]) {
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
        } else {
            this.rawDescription = cardStrings.DESCRIPTION;
        }
        switch (this.level) {
            case 2:
                this.baseDamage = 9;
                break;
            case 3:
                this.baseDamage = 12;
                break;
            case 4:
                this.baseDamage = 5;
                if (combos[0]) {
                    this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[2];
                } else {
                    this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
                }
                break;
            case 5:
                this.baseDamage = 6;
                this.baseMagicNumber = 8;
                if (combos[0]) {
                    this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[4];
                } else {
                    this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[3];
                }
                break;
            case 6:
                this.baseDamage = 38;
                this.capacity = 3;
                this.maxCapacity = 3;
                break;
        }
        this.initializeDescription();
        this.portrait = new TextureAtlas.AtlasRegion(new Texture(DemoMod.getResourcePath("cards/evolver_" + this.level + ".png")), 0, 0, 250, 190);
    }

    private void saveExp() {
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card.uuid.equals(this.uuid)) {
                Evolver evolver = (Evolver)card;
                evolver.exp++;
                if (evolver.exp >= EXP_FOR_LEVEL_UP) evolver.levelUp();
                break;
            }
        }
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        switch (this.level) {
            case 4:
                DemoSoundMaster.playA("GUN_FIRE_EVOLVER_3", 0.0F);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
                break;
            case 5:
                DemoSoundMaster.playV("GUN_FIRE_EVOLVER_4", 0.1F);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new ConstrictedPower(m, p, this.baseMagicNumber)));
                break;
            case 6:
                DemoSoundMaster.playA("GUN_FIRE_EVOLVER_5", 0.0F);
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.LONG, true);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
                break;
            default:
                if (this.level == 3) {
                    DemoSoundMaster.playV("GUN_FIRE_EVOLVER_2", 0.1F);
                } else {
                    DemoSoundMaster.playV("GUN_FIRE_EVOLVER_1", 0.1F);
                }
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
        }
        for (CardQueueItem item : AbstractDungeon.actionManager.cardQueue) {
            AbstractCard card = item.card;
            if (card.uuid.equals(this.uuid) && card != this) {
                Evolver evolver = (Evolver)card;
                evolver.exp++;
                break;
            }
        }
        for (AbstractCard card : AbstractDungeon.player.discardPile.group) {
            if (card.uuid.equals(this.uuid)) {
                Evolver evolver = (Evolver)card;
                evolver.exp++;
                if (evolver.exp >= EXP_FOR_LEVEL_UP) evolver.levelUp();
                break;
            }
        }
        this.exp++;
        saveExp();
        if (this.exp >= EXP_FOR_LEVEL_UP) this.levelUp();
    }

    @Override
    public void upgrade() {
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:NaturalSelection":
                combos[0] = true;
                EXP_FOR_LEVEL_UP = 4;
                for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    if (c instanceof Evolver) {
                        Evolver evolver = (Evolver)c;
                        switch (evolver.level) {
                            case 4:
                                evolver.rawDescription = cardStrings.EXTENDED_DESCRIPTION[2];
                                evolver.initializeDescription();
                                break;
                            case 5:
                                evolver.rawDescription = cardStrings.EXTENDED_DESCRIPTION[4];
                                evolver.initializeDescription();
                                break;
                            default:
                                evolver.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
                                evolver.initializeDescription();
                                break;
                        }
                    }
                }
                break;
            case "DemoMod:PowerhouseOfTheCell":
                if (!combos[1]) for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    if (c instanceof Evolver) {
                        Evolver evolver = (Evolver)c;
                        evolver.levelUp();
                    }
                }
                combos[1] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:NaturalSelection":
                combos[0] = false;
                EXP_FOR_LEVEL_UP = 6;
                break;
            case "DemoMod:PowerhouseOfTheCell":
                combos[1] = false;
                break;
        }
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
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        ComboManager.addCombo(DemoMod.makeID("NaturalSelection:BigBoy"), Evolver.class);
        ComboManager.addCombo(DemoMod.makeID("NaturalSelection:MonsterBlood"), Evolver.class);
        ComboManager.addCombo(DemoMod.makeID("PowerhouseOfTheCell"), Evolver.class);
    }

    @Override
    public void onAddedToMasterDeck() {
        ComboManager.detectComboInGame();
    }

    @Override
    public void onRemoveFromMasterDeck() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public GunCardSaveData onSave() {
        EvolverData data = new EvolverData();
        data.exp = this.exp;
        data.level = this.level;
        data.portraitPath = DemoMod.getResourcePath("cards/evolver_" + this.level + ".png");
        data.rawDescription = this.rawDescription;
        data.baseDamage = this.baseDamage;
        data.baseMagicNumber = this.baseMagicNumber;
        data.cost = this.cost;
        data.maxCapacity = this.maxCapacity;
        return data;
    }

    @Override
    public void onLoad(GunCardSaveData saveData) {
        if (saveData != null) {
            EvolverData data = (EvolverData) saveData;
            this.exp = data.exp;
            this.level = data.level;
            this.portrait = new TextureAtlas.AtlasRegion(new Texture(data.portraitPath), 0, 0, 250, 190);
            this.rawDescription = data.rawDescription;
            this.baseDamage = data.baseDamage;
            this.baseMagicNumber = data.baseMagicNumber;
            this.cost = data.cost;
            this.costForTurn = data.cost;
            this.capacity = data.maxCapacity;
            this.maxCapacity = data.maxCapacity;
            if (this.capacity == 0) this.target = CardTarget.NONE;
            this.initializeDescription();
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<EvolverData>(){}.getType();
    }
}

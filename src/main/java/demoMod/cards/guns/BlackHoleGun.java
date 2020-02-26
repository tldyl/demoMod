package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.HeartMegaDebuffEffect;
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.sounds.DemoSoundMaster;

public class BlackHoleGun extends AbstractGunCard implements Combo, PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("BlackHoleGun");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/blackHoleGun.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/blackHoleGun.png"));

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;

    private static final int COST = 3;

    private boolean isRemoving = false;
    private static boolean isCombo = false;

    public BlackHoleGun() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 10;
        this.capacity = 1;
        this.maxCapacity = 1;
        this.baseMagicNumber = 99;
        this.magicNumber = this.baseMagicNumber;
        this.reloadSoundKey = "GUN_RELOAD_BLACK_HOLE";
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        this.calculateCardDamage(null);
        DemoSoundMaster.playA("GUN_FIRE_BLACK_HOLE", 0.0F);
        AbstractDungeon.effectList.add(new HeartMegaDebuffEffect());
        AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(AbstractDungeon.player, this.multiDamage, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.FIRE));
        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!monster.isDeadOrEscaped()) {
                this.addToBot(new ApplyPowerAction(monster, p, new StrengthPower(monster, -this.magicNumber), -this.magicNumber));
                if (isCombo) {
                    this.addToBot(new ApplyPowerAction(monster, p, new ConstrictedPower(monster, p, 8)));
                }
                if (!monster.hasPower("Artifact")) {
                    this.addToBot(new ApplyPowerAction(monster, p, new GainStrengthPower(monster, this.magicNumber), this.magicNumber));
                }
            }
        }
        this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, -this.magicNumber), -this.magicNumber));
        if (!p.hasPower("Artifact")) {
            this.addToBot(new ApplyPowerAction(p, p, new GainStrengthPower(p, this.magicNumber), this.magicNumber));
        }
        for (AbstractCard card : p.discardPile.group) {
            if (card instanceof AbstractGunCard) {
                AbstractGunCard gunCard = (AbstractGunCard) card;
                gunCard.clearAmmo();
            }
        }
        this.addToBot(new MakeTempCardInDiscardAction(new VoidCard(), 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(2);
        }
    }

    @Override
    public void onRemoveFromMasterDeck() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public void onAddedToMasterDeck() {
        ComboManager.detectComboInGame();
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:CosmicTerror":
                isCombo = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:CosmicTerror":
                isCombo = false;
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
        ComboManager.addCombo(DemoMod.makeID("CosmicTerror"), BlackHoleGun.class);
    }
}

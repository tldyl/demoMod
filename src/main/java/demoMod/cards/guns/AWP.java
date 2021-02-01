package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.sounds.DemoSoundMaster;

public class AWP extends AbstractGunCard implements Combo, PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("AWP");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/awp.png";

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/awp.png"));

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;
    private boolean isRemoving = false;

    private static boolean[] combos = new boolean[1];

    public AWP() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 12;
        this.capacity = 1;
        this.maxCapacity = this.capacity;
        this.reloadSoundKey = "GUN_RELOAD_AWP";
    }

    @Override
    public void onRemoveFromMasterDeck() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        boolean isOnlyGunCard = true;
        int realBaseDamage = this.baseDamage;
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (card instanceof AbstractGunCard && card != this) {
                isOnlyGunCard = false;
                break;
            }
        }
        if (isOnlyGunCard) {
            this.baseDamage *= 2;
        }
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void applyPowers() {
        boolean isOnlyGunCard = true;
        int realBaseDamage = this.baseDamage;
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (card instanceof AbstractGunCard && card != this) {
                isOnlyGunCard = false;
                break;
            }
        }
        if (isOnlyGunCard) {
            this.baseDamage *= 2;
        }
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        DemoSoundMaster.playV("GUN_FIRE_AWP", 0.1F);
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(6);
        }
    }

    @Override
    public void initializeDescription() {
        super.initializeDescription();
        this.keywords.remove(AbstractGunCard.GUN_CARD_KEYWORD_NAME.TEXT[0]);
    }

    @Override
    public void initializeDescriptionCN() {
        super.initializeDescriptionCN();
        this.keywords.remove(AbstractGunCard.GUN_CARD_KEYWORD_NAME.TEXT[0]);
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        ComboManager.addCombo(DemoMod.makeID("SniperWoof"), AWP.class);
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:SniperWoof":
                combos[0] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:SniperWoof":
                combos[0] = false;
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

    @Override
    public void onAddedToMasterDeck() {
        ComboManager.detectComboInGame();
    }
}

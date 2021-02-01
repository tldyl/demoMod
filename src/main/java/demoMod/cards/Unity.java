package demoMod.cards;

import basemod.abstracts.CustomCard;
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
import demoMod.cards.guns.AbstractGunCard;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;

public class Unity extends CustomCard implements Combo, PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("Unity");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/unity.png";

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/unity.png"));

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static boolean combos[] = new boolean[]{false, false, false};

    private static final int COST = 1;

    private boolean isRemoving = false;

    public Unity() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
        this.baseMagicNumber = 2;
        this.magicNumber = this.baseMagicNumber;
        this.baseDamage = 6;
    }

    public static int countCards() {
        int cnt = 0;
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c instanceof AbstractGunCard) cnt++;
        }
        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
            if (c instanceof AbstractGunCard) cnt++;
        }
        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            if (c instanceof AbstractGunCard) cnt++;
        }
        return cnt;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        if (combos[0]) this.magicNumber = this.baseMagicNumber + 2;
        this.baseDamage += this.magicNumber * countCards();
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += this.magicNumber * countCards();
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        ComboManager.addCombo(DemoMod.makeID("WhatEngineDoYouUse"), Unity.class);
        ComboManager.addCombo("DemoExt:CaptainPlantIt", Unity.class);
        ComboManager.addCombo(DemoMod.makeID("GarbageCollecting"), Unity.class);
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:WhatEngineDoYouUse":
                combos[0] = true;
                break;
            case "DemoMod:CaptainPlantIt":
                combos[1] = true;
                break;
            case "DemoMod:GarbageCollecting":
                combos[2] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:WhatEngineDoYouUse":
                combos[0] = false;
                break;
            case "DemoMod:CaptainPlantIt":
                combos[1] = false;
                break;
            case "DemoMod:GarbageCollecting":
                combos[2] = false;
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

    @Override
    public void onRemoveFromMasterDeck() {
        isRemoving = true;
        ComboManager.detectCombo();
    }
}

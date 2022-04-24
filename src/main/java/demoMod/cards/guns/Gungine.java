package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.interfaces.MultiAttackCard;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.sounds.DemoSoundMaster;

public class Gungine extends AbstractGunCard implements MultiAttackCard,
                                                        Combo,
                                                        PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("Gungine");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/gungine.png";

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/gungine.png"));

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static boolean combos[] = new boolean[]{false, false, false};

    private static final int COST = 1;
    private int multi = 2;
    private boolean isRemoving = false;

    public Gungine() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 5;
        this.baseMagicNumber = 1;
        this.capacity = 9;
        this.maxCapacity = 9;
    }

    @Override
    public void reload() {
        AbstractPlayer p = AbstractDungeon.player;
        if (this.capacity == this.maxCapacity && !p.hasRelic("DemoExt:AncientHerosBandana")) return; //不允许满弹夹装弹
        this.capacity = this.maxCapacity;
        if (combos[0]) {
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(this.baseMagicNumber + 1));
        } else {
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(this.baseMagicNumber));
        }
        DemoSoundMaster.playA(this.reloadSoundKey, 0F);
        if (p.hasRelic("DemoMod:HipHolster")) {
            p.getRelic("DemoMod:HipHolster").flash();
            AbstractMonster m = AbstractDungeon.getRandomMonster();
            for (int i=0;i<multi;i++) {
                if (!m.isDeadOrEscaped()) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
                }
            }
        }
        afterReload();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
        if (combos[0]) {
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(this.baseMagicNumber + 1));
        } else {
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(this.baseMagicNumber));
        }
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        for (int i=0;i<multi;i++) {
            if (!m.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
            }
        }
        if (p.hasRelic("DemoMod:HipHolster") && this.capacity == this.maxCapacity) {
            if (combos[0]) {
                AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(this.baseMagicNumber + 1));
            } else {
                AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(this.baseMagicNumber));
            }
        }
        for (AbstractCard c : p.hand.group) {
            if (c.type == CardType.CURSE || c.type == CardType.STATUS) {
                AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(c, p.hand, true));
            }
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            multi++;
            this.upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public int getMulti() {
        return multi;
    }

    @Override
    public boolean isMultiModified() {
        return this.upgraded;
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        ComboManager.addCombo(DemoMod.makeID("WhatEngineDoYouUse"), Gungine.class);
        ComboManager.addCombo(DemoMod.makeID("BuckleUp"), Gungine.class);
        ComboManager.addCombo(DemoMod.makeID("ContractualObligation"), Gungine.class);
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
            case "DemoMod:BuckleUp":
                combos[1] = true;
                break;
            case "DemoMod:ContractualObligation":
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
            case "DemoMod:BuckleUp":
                combos[1] = false;
                break;
            case "DemoMod:ContractualObligation":
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

    public void onRemoveFromMasterDeck() {
        isRemoving = true;
        ComboManager.detectCombo();
    }
}

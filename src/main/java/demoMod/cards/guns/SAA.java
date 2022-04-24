package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.sounds.DemoSoundMaster;

public class SAA extends AbstractGunCard implements Combo, PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("SAA");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/saa.png";

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/saa.png"));

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;

    private boolean isRemoving = false;

    public SAA() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 6;
        this.damage = this.baseDamage;
        this.capacity = 6;
        this.maxCapacity = 6;
        this.baseMagicNumber = 2;
        this.magicNumber = this.baseMagicNumber;
        this.isSemiAutomatic = true;
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(3);
            upgradeMagicNumber(1);
        }
    }

    @Override
    public void reload() {
        if (this.capacity == this.maxCapacity && !AbstractDungeon.player.hasRelic("DemoExt:AncientHerosBandana")) return; //不允许满弹夹装弹
        this.capacity = this.maxCapacity;
        DemoSoundMaster.playA(this.reloadSoundKey, 0F);
        AbstractPlayer p = AbstractDungeon.player;
        if (Settings.MAX_HAND_SIZE - p.hand.size() <= this.magicNumber) {
            this.magicNumber = Settings.MAX_HAND_SIZE - p.hand.size() - 1;
            p.createHandIsFullDialog();
        }
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(this.magicNumber));
        if (p.hasRelic("DemoMod:HipHolster")) {
            p.getRelic("DemoMod:HipHolster").flash();
            AbstractMonster m = AbstractDungeon.getRandomMonster();
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        }
        afterReload();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
        if (Settings.MAX_HAND_SIZE - p.hand.size() <= this.magicNumber) {
            this.magicNumber = Settings.MAX_HAND_SIZE - p.hand.size() - 1;
            p.createHandIsFullDialog();
        }
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(this.magicNumber));
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        ComboManager.addCombo(DemoMod.makeID("ReloadRoll:SAA"), SAA.class);
        ComboManager.addCombo(DemoMod.makeID("PrettyGood:HipHolster"), SAA.class);
        ComboManager.addCombo(DemoMod.makeID("PrettyGood:NanoMachines"), SAA.class);
        ComboManager.addCombo(DemoMod.makeID("PrettyGood:FortunesFavor"), SAA.class);
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {

    }

    @Override
    public void onComboDisabled(String comboId) {

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

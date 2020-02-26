package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
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

public class AC15 extends AbstractGunCard implements Combo, PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("AC15");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/ac-15_no_armor.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/ac-15.png"));

    private static final int COST = 1;
    private boolean firstPlayInCombat = false;
    private boolean isRemoving = false;
    private static boolean combo = false;

    public AC15() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.reloadSoundKey = "GUN_RELOAD_AC15";
        this.baseDamage = 0;
        this.capacity = 6;
        this.maxCapacity = 6;
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        this.calculateCardDamage(m);
        DemoSoundMaster.playV("GUN_FIRE_AC15", 0.1F);
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        if (!firstPlayInCombat) {
            firstPlayInCombat = true;
            this.portrait = new TextureAtlas.AtlasRegion(new Texture(DemoMod.getResourcePath("cards/ac-15_armor.png")), 0, 0, 250, 190);
        }
        this.baseDamage += p.currentBlock;
        if (!combo) {
            this.addToBot(new LoseBlockAction(p, p, p.currentBlock));
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        this.baseDamage += AbstractDungeon.player.currentBlock;
        super.calculateCardDamage(mo);
        this.baseDamage -= AbstractDungeon.player.currentBlock;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void applyPowers() {
        this.baseDamage += AbstractDungeon.player.currentBlock;
        super.applyPowers();
        this.baseDamage -= AbstractDungeon.player.currentBlock;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(4);
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        ComboManager.addCombo(DemoMod.makeID("SteelSkin"), AC15.class);
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
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:SteelSkin":
                combo = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:SteelSkin":
                combo = false;
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
}

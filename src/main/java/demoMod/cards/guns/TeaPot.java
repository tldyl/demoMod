package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.OmegaPower;
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.effects.TeaPotEffect;
import demoMod.sounds.DemoSoundMaster;

public class TeaPot extends AbstractGunCard implements Combo, PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("TeaPot");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/teaPot.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/teaPot.png"));

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private int BASE_DMG = 8;
    private boolean isRemoving = false;
    private static boolean combos[] = new boolean[]{false, false};
    private static final int COST = 1;

    public TeaPot() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.capacity = 3;
        this.maxCapacity = 3;
        this.baseDamage = 8;
        this.extraDamage = 8;
        this.baseMagicNumber = 5;
        this.reloadSoundKey = "GUN_RELOAD_TEAPOT";
    }

    @Override
    public void reload() {
        if (this.capacity == this.maxCapacity) return; //不允许满弹夹装弹
        if (this.capacity == 0) this.baseDamage = BASE_DMG;
        this.capacity = this.maxCapacity;
        DemoSoundMaster.playA(this.reloadSoundKey, 0F);
        AbstractPlayer p = AbstractDungeon.player;
        if (!combos[0]) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new OmegaPower(p, this.baseMagicNumber)));
        } else {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new OmegaPower(p, 2 * this.baseMagicNumber)));
        }
        if (AbstractDungeon.player.hasRelic("DemoMod:HipHolster")) {
            AbstractDungeon.player.getRelic("DemoMod:HipHolster").flash();
            AbstractMonster m = AbstractDungeon.getRandomMonster();
            DemoSoundMaster.playA("GUN_FIRE_TEAPOT_1", 0F);
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(AbstractDungeon.player, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.NONE));
            AbstractDungeon.effectsQueue.add(new TeaPotEffect(m.drawX, m.drawY, false));
        }
        afterReload();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
        if (!combos[0]) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new OmegaPower(p, this.baseMagicNumber)));
        } else {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new OmegaPower(p, 2 * this.baseMagicNumber)));
        }
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        if (this.capacity == 0) {
            this.baseDamage += this.extraDamage;
            this.calculateCardDamage(null);
            DemoSoundMaster.playA("GUN_FIRE_TEAPOT_2", 0F);
            AbstractDungeon.effectsQueue.add(new TeaPotEffect(m.drawX, m.drawY, true));
        } else {
            DemoSoundMaster.playA("GUN_FIRE_TEAPOT_1", 0F);
            AbstractDungeon.effectsQueue.add(new TeaPotEffect(m.drawX, m.drawY, false));
        }
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.NONE));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(3);
            this.upgradeMagicNumber(1);
            BASE_DMG += 3;
        }
    }

    @Override
    public String getItemId() {
        return DemoMod.makeID("TeaPot");
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:TeaForTwo":
                combos[0] = true;
                break;
            case "DemoMod:MonstersAndMonocles":
                combos[1] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:TeaForTwo":
                combos[0] = false;
                break;
            case "DemoMod:MonstersAndMonocles":
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
        ComboManager.addCombo(DemoMod.makeID("TeaForTwo:Wolf"), TeaPot.class);
        ComboManager.addCombo(DemoMod.makeID("TeaForTwo:SerJunkan"), TeaPot.class);
        ComboManager.addCombo(DemoMod.makeID("TeaForTwo:BabyGoodMimic"), TeaPot.class);
        ComboManager.addCombo(DemoMod.makeID("TeaForTwo:Turkey"), TeaPot.class);
        ComboManager.addCombo(DemoMod.makeID("TeaForTwo:ClownMask"), TeaPot.class);
        ComboManager.addCombo(DemoMod.makeID("MonstersAndMonocles"), TeaPot.class);
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

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
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;

public class AbyssalTentacle extends AbstractGunCard implements Combo, PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("AbyssalTentacle");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/abyssalTentacle.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/abyssalTentacle.png"));

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;
    public static boolean[] combos = new boolean[]{false, false};

    private boolean isRemoving = false;

    public AbyssalTentacle() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 3;
        this.baseMagicNumber = 5;
        this.capacity = 3;
        this.maxCapacity = this.capacity;
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(2);
            upgradeMagicNumber(3);
        }
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.NONE));
        if (combos[1]) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new ConstrictedPower(m, p, 2 * this.baseMagicNumber)));
        } else {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new ConstrictedPower(m, p, this.baseMagicNumber)));
        }
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:DecoyOctorok":
                combos[0] = true;
                break;
            case "DemoMod:KalibersGrip":
                combos[1] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:DecoyOctorok":
                combos[0] = false;
                break;
            case "DemoMod:KalibersGrip":
                combos[1] = false;
                break;
        }
    }

    @Override
    public boolean isRemoving() {
        return isRemoving;
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
    public Texture getComboPortrait() {
        return comboTexture;
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        ComboManager.addCombo(DemoMod.makeID("DecoyOctorok"), AbyssalTentacle.class);
        ComboManager.addCombo(DemoMod.makeID("KalibersGrip"), AbyssalTentacle.class);
        ComboManager.addCombo(DemoMod.makeID("CosmicTerror"), AbyssalTentacle.class);
    }
}

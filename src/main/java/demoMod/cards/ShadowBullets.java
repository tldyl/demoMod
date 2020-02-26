package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.powers.ShadowBulletsPower;

public class ShadowBullets extends CustomCard implements Combo,
                                                         PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("ShadowBullets");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/shadowBullets.png";

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/shadowBullets.png"));

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.POWER;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    public static boolean[] combos = new boolean[] {false, false, false, false, false};

    private static final int COST = 1;

    private boolean isRemoving = false;

    public ShadowBullets() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
        this.baseMagicNumber = 15;
        this.magicNumber = this.baseMagicNumber;
    }


    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(10);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new ShadowBulletsPower(this.baseMagicNumber)));
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        ComboManager.addCombo(DemoMod.makeID("ChainLightning"), ShadowBullets.class);
        ComboManager.addCombo(DemoMod.makeID("GrubersBane"), ShadowBullets.class);
        ComboManager.addCombo(DemoMod.makeID("KageBunshin"), ShadowBullets.class);
        ComboManager.addCombo(DemoMod.makeID("MrShadow"), ShadowBullets.class);
        ComboManager.addCombo(DemoMod.makeID("NoxinCannon:ShadowBullets"), ShadowBullets.class);
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:ChainLightning":
                combos[0] = true;
                break;
            case "DemoMod:GrubersBane":
                combos[1] = true;
                break;
            case "DemoMod:KageBunshin":
                combos[2] = true;
                break;
            case "DemoMod:MrShadow":
                combos[3] = true;
                break;
            case "DemoMod:NoxinCannon":
                combos[4] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:ChainLightning":
                combos[0] = false;
                break;
            case "DemoMod:GrubersBane":
                combos[1] = false;
                break;
            case "DemoMod:KageBunshin":
                combos[2] = false;
                break;
            case "DemoMod:MrShadow":
                combos[3] = false;
                break;
            case "DemoMod:NoxinCannon":
                combos[4] = false;
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

package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.characters.HuntressCharacter;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.sounds.DemoSoundMaster;

@SuppressWarnings("Duplicates")
public class CursedBullets extends CustomCard implements PostAddedToMasterDeckSubscriber,
                                                         Combo {
    public static final String ID = DemoMod.makeID("CursedBullets");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/cursedBullets.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/cursedBullets.png"));

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.POWER;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    private static final int COST = 1;
    private static boolean combos[] = new boolean[]{false, false, false};

    private boolean isRemoving = false;
    private boolean isAdded = false;

    public CursedBullets() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.isInnate = true;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (p instanceof HuntressCharacter) {
            if (HuntressCharacter.curse <= 0) return;
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, (int)Math.ceil(HuntressCharacter.curse / 2.0))));
        } else {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, 3)));
        }
    }

    @Override
    public void onAddedToMasterDeck() {
        if (!isAdded) {
            DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                HuntressCharacter.curse += 1;
            }
            isAdded = true;
        }
    }

    @Override
    public void onRemoveFromMasterDeck() {
        isRemoving = true;
        ComboManager.detectCombo();
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse -= 1;
        }
    }

    @Override
    public String getItemId() {
        return DemoMod.makeID("CursedBullets");
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:Kaliber k'pow uboom k'bhang":
                combos[0] = true;
                break;
            case "DemoMod:BlessingAndACurse":
                combos[1] = true;
                break;
            case "DemoMod:NoxinCannon":
                combos[2] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:Kaliber k'pow uboom k'bhang":
                combos[0] = false;
                break;
            case "DemoMod:BlessingAndACurse":
                combos[1] = false;
                break;
            case "DemoMod:NoxinCannon":
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

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        ComboManager.addCombo(DemoMod.makeID("Kaliber k'pow uboom k'bhang:CursedBullets"), CursedBullets.class);
        ComboManager.addCombo(DemoMod.makeID("BlessingAndACurse"), CursedBullets.class);
        ComboManager.addCombo(DemoMod.makeID("NoxinCannon:CursedBullets"), CursedBullets.class);
    }
}

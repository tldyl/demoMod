package demoMod.cards;

import basemod.abstracts.CustomCard;
import basemod.abstracts.CustomSavable;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import demoMod.DemoMod;
import demoMod.blights.SpiceCounter;
import demoMod.characters.HuntressCharacter;
import demoMod.dto.SpiceData;
import demoMod.sounds.DemoSoundMaster;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Spice extends CustomCard implements CustomSavable<SpiceData> {

    public static final String ID = DemoMod.makeID("Spice");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/spice.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    public static double dropChance = 0;
    public static boolean isFirstUse = true;

    private static final int COST = 0;

    public Spice() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, CardColor.COLORLESS, RARITY, TARGET);
        this.exhaust = true;
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        if (this.upgraded) {
            AbstractCard newCard = new Spice();
            newCard.upgrade();
            return newCard;
        } else {
            return new Spice();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        DemoSoundMaster.playV("ITEM_DOUBLE_VISION", 0.1F);
        if (!isFirstUse) {
            DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
            if (p instanceof HuntressCharacter) {
                HuntressCharacter.curse += 1;
            }
        } else {
            if (p instanceof HuntressCharacter) {
                HuntressCharacter.curse += 0.5;
                SpiceCounter spiceCounter = new SpiceCounter(this.magicNumber);
                spiceCounter.obtain();
                //spiceCounter.currentX = spiceCounter.img.getWidth() / 2;
                //spiceCounter.currentY = Settings.HEIGHT - 96.0F;
                SaveFile saveFile = CardCrawlGame.saveFile;
                if (saveFile == null) saveFile = new SaveFile();
                if (saveFile.endless_increments == null) {
                    saveFile.endless_increments = new ArrayList<>();
                    saveFile.endless_increments.add(0);
                }
            }
            isFirstUse = false;
        }
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber)));
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
        AbstractDungeon.player.decreaseMaxHealth(2);
        dropChance += (1 - dropChance) * 0.07;
        if (dropChance > 0.95) dropChance = 0.95;
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }

    @Override
    public SpiceData onSave() {
        SpiceData spiceData = new SpiceData();
        spiceData.dropChance = dropChance;
        spiceData.isFirstUse = isFirstUse;

        return spiceData;
    }

    @Override
    public void onLoad(SpiceData loadSpiceData) {
        if (loadSpiceData != null) {
            dropChance = loadSpiceData.dropChance;
            isFirstUse = loadSpiceData.isFirstUse;
        } else {
            System.out.println("DemoMod:Could not load data from spice!");
            dropChance = 0;
            isFirstUse = true;
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<SpiceData>(){}.getType();
    }
}

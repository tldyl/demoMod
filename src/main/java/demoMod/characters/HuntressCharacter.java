package demoMod.characters;

import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import demoMod.DemoMod;
import demoMod.cards.Defend_Huntress;
import demoMod.cards.Strike_Huntress;
import demoMod.cards.guns.CrossBow;
import demoMod.cards.ManualReload;
import demoMod.cards.Roll;
import demoMod.cards.guns.RustySidearm;
import demoMod.patches.AbstractCardEnum;
import demoMod.patches.HuntressEnum;
import demoMod.relics.Dog;
import demoMod.sounds.DemoSoundMaster;

import java.util.ArrayList;

public class HuntressCharacter extends CustomPlayer {

    private static final CharacterStrings charStrings;
    public static final String NAME;
    public static final String DESCRIPTION;
    public static double curse = 0;

    private static final String[] orbTextures = {
            "DemoImages/char/orb/layer1.png",
            "DemoImages/char/orb/layer2.png",
            "DemoImages/char/orb/layer3.png",
            "DemoImages/char/orb/layer4.png",
            "DemoImages/char/orb/layer5.png",
            "DemoImages/char/orb/layer6.png",
            "DemoImages/char/orb/layer1d.png",
            "DemoImages/char/orb/layer2d.png",
            "DemoImages/char/orb/layer3d.png",
            "DemoImages/char/orb/layer4d.png",
            "DemoImages/char/orb/layer5d.png"
    };

    public HuntressCharacter(String name, PlayerClass setClass) {
        super(name, setClass, orbTextures, "DemoImages/char/orb/vfx.png", null, (String) null);
        this.initializeClass(DemoMod.getResourcePath("char/character.png"), DemoMod.getResourcePath("char/shoulder2.png"), DemoMod.getResourcePath("char/shoulder.png"), DemoMod.getResourcePath("char/corpse.png"), this.getLoadout(), 0.0F, -10F, 310.0F, 260.0F, new EnergyManager(3));
        if (ModHelper.enabledMods.size() > 0 && (ModHelper.isModEnabled("Diverse") || ModHelper.isModEnabled("Chimera") || ModHelper.isModEnabled("Blue Cards"))) {
            this.masterMaxOrbs = 1;
        }
    }

    @Override
    public String getPortraitImageName() {
        return "portrait.png";
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> startingDeck = new ArrayList<>();
        startingDeck.add(Strike_Huntress.ID);
        startingDeck.add(Strike_Huntress.ID);
        startingDeck.add(Strike_Huntress.ID);
        startingDeck.add(Defend_Huntress.ID);
        startingDeck.add(Defend_Huntress.ID);
        startingDeck.add(Defend_Huntress.ID);
        startingDeck.add(Defend_Huntress.ID);
        startingDeck.add(Roll.ID);
        startingDeck.add(ManualReload.ID);
        startingDeck.add(RustySidearm.ID);
        startingDeck.add(CrossBow.ID);
        return startingDeck;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(Dog.ID);
        UnlockTracker.markRelicAsSeen(Dog.ID);
        return retVal;
    }

    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(NAME, DESCRIPTION, 70, 70, 0, 99, 5, this,
                getStartingRelics(), getStartingDeck(), false);
    }

    @Override
    public String getTitle(PlayerClass playerClass) {
        return NAME;
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return AbstractCardEnum.HUNTRESS;
    }

    @Override
    public Color getCardRenderColor() {
        return DemoMod.mainHuntressColor;
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new ManualReload();
    }

    @Override
    public Color getCardTrailColor() {
        return DemoMod.mainHuntressColor.cpy();
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return 7;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontGreen;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        DemoSoundMaster.playV("RELIC_VORPAL_GUN", 0.1F);
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, true);
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        DemoSoundMaster.playV("RELIC_VORPAL_GUN", 0.1F);
        return "";
    }

    @Override
    public String getLocalizedCharacterName() {
        return NAME;
    }

    @Override
    public AbstractPlayer newInstance() {
        return new HuntressCharacter(NAME, HuntressEnum.HUNTRESS);
    }

    @Override
    public String getSpireHeartText() {
        return charStrings.TEXT[1];
    }

    @Override
    public Color getSlashAttackColor() {
        return Color.FIREBRICK;
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL,
                AbstractGameAction.AttackEffect.SLASH_VERTICAL,
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL,
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL,
                AbstractGameAction.AttackEffect.SLASH_VERTICAL,
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        };
    }

    @Override
    public String getVampireText() {
        return com.megacrit.cardcrawl.events.city.Vampires.DESCRIPTIONS[1];
    }

    static {
        charStrings = CardCrawlGame.languagePack.getCharacterString("Gungeon Huntress");
        NAME = charStrings.NAMES[0];
        DESCRIPTION = charStrings.TEXT[0];
    }
}

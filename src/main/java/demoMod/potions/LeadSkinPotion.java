package demoMod.potions;

import basemod.abstracts.CustomPotion;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import demoMod.DemoMod;
import demoMod.powers.LeadSkinPower;
import demoMod.utils.CustomPotionTexture;

@SuppressWarnings("WeakerAccess")
public class LeadSkinPotion extends CustomPotion {
    public static final String ID = DemoMod.makeID("LeadSkinPotion");
    private static final PotionStrings potionStrings;

    public LeadSkinPotion() {
        super(potionStrings.NAME, ID, PotionRarity.COMMON, PotionSize.BOTTLE, PotionColor.BLUE);
        this.isThrown = false;
        CustomPotionTexture.setPotionTexture(this, "DemoImages/potions/leadSkinPotion.png");
        this.potency = 10;
    }

    @Override
    public void use(AbstractCreature owner) {
        if (AbstractDungeon.player.hasRelic("SacredBark")) this.potency = 20;
        AbstractPlayer p = AbstractDungeon.player;
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new LeadSkinPower(p, p, this.potency)));
    }

    public void initializeData() {
        this.potency = this.getPotency();
        this.description = potionStrings.DESCRIPTIONS[0] + this.potency + potionStrings.DESCRIPTIONS[1];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    @Override
    public int getPotency(int ascensionLevel) {
        return 10;
    }

    @Override
    public AbstractPotion makeCopy() {
        return new LeadSkinPotion();
    }

    static {
        potionStrings = CardCrawlGame.languagePack.getPotionString(DemoMod.makeID("LeadSkinPotion"));
    }
}

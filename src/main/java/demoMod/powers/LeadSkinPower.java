package demoMod.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;

@SuppressWarnings("WeakerAccess")
public class LeadSkinPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("LeadSkinPower");
    public static PowerType POWER_TYPE = PowerType.BUFF;
    private AbstractCreature source;

    public static String[] DESCRIPTIONS;

    public LeadSkinPower(AbstractCreature owner, AbstractCreature source, int amount) {
        this.ID = POWER_ID;
        this.owner = owner;
        this.source = source;
        this.loadRegion("noPain");
        this.type = POWER_TYPE;
        this.amount = amount;
        DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;
        updateDescription();
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public int onAttacked(DamageInfo info, int damage) {
        System.out.println(damage);
        if (damage <= this.amount && damage > 1 && info.type != DamageType.HP_LOSS && info.type != DamageType.THORNS) {
            this.flash();
            return 1;
        }
        return damage;
    }
}

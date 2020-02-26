package demoMod.powers;

import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.monsters.LordOfTheJammed;

public class JammedPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("JammedPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public JammedPower(AbstractCreature owner){
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        updateDescription();
        loadRegion("fading");
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        boolean isAllDead = true;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped() && !(m instanceof LordOfTheJammed)) {
                isAllDead = false;
                break;
            }
        }
        if (isAllDead) {
            AbstractDungeon.actionManager.addToBottom(new SuicideAction((AbstractMonster)this.owner));
        }
    }

    @Override
    public int onAttacked(DamageInfo info, int damage) {
        return 0;
    }
}

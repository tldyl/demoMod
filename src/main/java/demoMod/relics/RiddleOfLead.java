package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import demoMod.DemoMod;

public class RiddleOfLead extends CustomRelic {
    public static final String ID = DemoMod.makeID("RiddleOfLead");
    public static final String IMG_PATH = "relics/riddleOfLead.png";

    public RiddleOfLead() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.BOSS, LandingSound.HEAVY);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.increaseMaxHp(10, false);
        AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth);
    }

    @Override
    public void atBattleStart() {
        AbstractPlayer p = AbstractDungeon.player;
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, 2)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DexterityPower(p, 2)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new FocusPower(p, 2)));
        this.addToTop(new RelicAboveCreatureAction(p, this));
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (damageAmount > 0) {
            if (p.currentHealth <= p.maxHealth * 0.25) {
                if (AbstractDungeon.miscRng.randomBoolean()) {
                    this.flash();
                    return 0;
                }
            }
        }
        return damageAmount;
    }
}

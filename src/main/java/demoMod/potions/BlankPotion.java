package demoMod.potions;

import basemod.abstracts.CustomPotion;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import demoMod.DemoMod;
import demoMod.effects.BlankWaveEffect;
import demoMod.sounds.DemoSoundMaster;
import demoMod.utils.CustomPotionTexture;

@SuppressWarnings("WeakerAccess")
public class BlankPotion extends CustomPotion {

    public static final String ID = DemoMod.makeID("BlankPotion");
    private static final PotionStrings potionStrings;

    public BlankPotion() {
        super(potionStrings.NAME, ID, PotionRarity.RARE, PotionSize.BOTTLE, PotionColor.BLUE);
        this.isThrown = true;
        CustomPotionTexture.setPotionTexture(this, "DemoImages/potions/blankPotion.png");
    }

    public void initializeData() {
        this.potency = this.getPotency();
        this.description = potionStrings.DESCRIPTIONS[0] + this.potency + potionStrings.DESCRIPTIONS[1];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    @Override
    public void use(AbstractCreature abstractCreature) {
        AbstractPlayer p = AbstractDungeon.player;
        DemoSoundMaster.playV("POTION_BLANK", 0.1F);
        this.addToBot(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(this.potency, true), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
        AbstractDungeon.actionManager.addToBottom(new VFXAction(p, new BlankWaveEffect(p.hb.cX, p.hb.cY, Color.WHITE, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.1F));
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped() && !m.hasPower(StunMonsterPower.POWER_ID)) {
                this.addToBot(new StunMonsterAction(m, p));
            }
        }
    }

    @Override
    public void setAsObtained(int potionSlot) {
        super.setAsObtained(potionSlot);
        DemoSoundMaster.playA("POTION_BLANK_OBTAIN", 0F);
    }

    @Override
    public int getPotency(int ascensionLevel) {
        return 3;
    }

    @Override
    public AbstractPotion makeCopy() {
        return new BlankPotion();
    }

    static {
        potionStrings = CardCrawlGame.languagePack.getPotionString(DemoMod.makeID("BlankPotion"));
    }
}

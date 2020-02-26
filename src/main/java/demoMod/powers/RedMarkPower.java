package demoMod.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import demoMod.DemoMod;
import demoMod.effects.BlankWaveEffect;
import demoMod.sounds.DemoSoundMaster;

public class RedMarkPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("RedMarkPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public RedMarkPower(AbstractMonster owner) {
        this.ID = POWER_ID;
        this.owner = owner;
        this.name = NAME;
        this.type = PowerType.DEBUFF;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/RedMark84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/RedMark32.png")), 0, 0, 32, 32);
        this.updateDescription();
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (power.ID.equals(DemoMod.makeID("BlueMarkPower")) && target == owner) {
            DemoSoundMaster.playV("POTION_BLANK", 0.1F);
            AbstractDungeon.effectsQueue.add(new BlankWaveEffect(owner.hb.cX, owner.hb.cY, Color.WHITE, ShockWaveEffect.ShockWaveType.CHAOTIC));
            AbstractDungeon.actionManager.addToBottom(new DamageAction(owner, new DamageInfo(source, 6, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
            if (!owner.hasPower("stslib:Stunned")) {
                AbstractDungeon.actionManager.addToBottom(new StunMonsterAction((AbstractMonster) owner, source));
            }
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(owner, source, ID));
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(owner, source, DemoMod.makeID("BlueMarkPower")));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(DemoMod.makeID("RedMarkPower"));
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

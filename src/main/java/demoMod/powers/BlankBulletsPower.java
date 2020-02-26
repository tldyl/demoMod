package demoMod.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.effects.BlankWaveEffect;
import demoMod.sounds.DemoSoundMaster;

public class BlankBulletsPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("BlankBulletsPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public BlankBulletsPower(int percentage) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = AbstractDungeon.player;
        this.amount = percentage;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/BlankBullets84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/BlankBullets32.png")), 0, 0, 32, 32);
        this.updateDescription();
    }

    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        if (card instanceof AbstractGunCard) {
            AbstractGunCard gunCard = (AbstractGunCard)card;
            if (gunCard.target != AbstractCard.CardTarget.NONE && m != null) {
                int ran = MathUtils.random(99);
                if (ran < this.amount) {
                    if (!m.isDeadOrEscaped() && !m.hasPower("stslib:Stunned")) {
                        this.flash();
                        DemoSoundMaster.playV("POTION_BLANK", 0.1F);
                        AbstractDungeon.actionManager.addToBottom(new VFXAction(m, new BlankWaveEffect(m.hb.cX, m.hb.cY, Color.WHITE, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.1F));
                        this.addToBot(new StunMonsterAction(m, this.owner));
                    }
                }
            }
        }
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(DemoMod.makeID("BlankBulletsPower"));
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

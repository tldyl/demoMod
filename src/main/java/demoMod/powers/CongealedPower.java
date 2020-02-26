package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.watcher.TriggerMarksAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.FlashPowerEffect;
import demoMod.DemoMod;
import demoMod.patches.AbstractMonsterEnum;
import demoMod.sounds.DemoSoundMaster;

public class CongealedPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("CongealedPower");
    public static String[] DESCRIPTIONS;
    public boolean activated = false;
    private int oct = 0;

    public CongealedPower(AbstractCreature owner, int amount) {
        this.ID = POWER_ID;
        this.name = CardCrawlGame.languagePack.getPowerStrings(this.ID).NAME;
        this.owner= owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;
        DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings(this.ID).DESCRIPTIONS;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/Congealed84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/Congealed32.png")), 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        this.oct = this.amount / 10;
        this.addToBot(new TriggerMarksAction(null));
    }

    @Override
    public void triggerMarks(AbstractCard card) {
        if (this.amount / 10 > this.oct && !this.activated) {
            this.flash();
            AbstractDungeon.effectList.add(new FlashPowerEffect(this));
            this.activated = true;
            AbstractMonster m = (AbstractMonster)this.owner;
            m.setMove((byte)-1, AbstractMonsterEnum.CONGEALED);
            m.createIntent();
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        this.activated = false;
    }

    @Override
    public void onDeath() {
        int ctr = 0;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m != this.owner && !m.isDeadOrEscaped()) {
                ctr++;
            }
        }
        DemoSoundMaster.playA("GUN_KILLED_ELIMENTALER", 0.0F);
        if (ctr == 0) return;
        int avgAmt = this.amount / ctr;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m != this.owner && !m.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this.owner, new CongealedPower(m, avgAmt)));
                this.addToBot(new TriggerMarksAction(null));
            }
        }
    }
}

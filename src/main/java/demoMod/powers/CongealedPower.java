package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.watcher.TriggerMarksAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.FlashPowerEffect;
import demoMod.DemoMod;
import demoMod.cards.guns.Elimentaler;
import demoMod.patches.AbstractMonsterEnum;
import demoMod.sounds.DemoSoundMaster;

import java.lang.reflect.Field;

public class CongealedPower extends AbstractPower {
    public static final String POWER_ID = DemoMod.makeID("CongealedPower");
    public static String[] DESCRIPTIONS;
    private int oct = 0;
    private byte moveByte;
    private AbstractMonster.Intent moveIntent;
    private EnemyMoveInfo move;
    private AbstractCard callingCard;

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
    }

    @Override
    public void triggerMarks(AbstractCard card) {
        if (!(card instanceof Elimentaler) || ((AbstractMonster)owner).intent == AbstractMonsterEnum.CONGEALED) return;
        this.callingCard = card;
        if (this.amount / 10 > this.oct) {
            this.flash();
            AbstractDungeon.effectList.add(new FlashPowerEffect(this));
            this.addToBot(new AbstractGameAction() {
                public void update() {
                    if (owner instanceof AbstractMonster) {
                        moveByte = ((AbstractMonster)owner).nextMove;
                        moveIntent = ((AbstractMonster)owner).intent;

                        try {
                            Field f = AbstractMonster.class.getDeclaredField("move");
                            f.setAccessible(true);
                            move = (EnemyMoveInfo)f.get(owner);
                            move.intent = AbstractMonsterEnum.CONGEALED;
                            ((AbstractMonster)owner).createIntent();
                        } catch (NoSuchFieldException | IllegalAccessException var2) {
                            var2.printStackTrace();
                        }
                    }

                    this.isDone = true;
                }
            });
        }
    }

    public void atEndOfTurn(boolean isPlayer) {
        if (this.owner instanceof AbstractMonster) {
            AbstractMonster m = (AbstractMonster)this.owner;
            if (m.intent == AbstractMonsterEnum.CONGEALED) {
                if (this.move != null) {
                    m.setMove(this.moveByte, this.moveIntent, this.move.baseDamage, this.move.multiplier, this.move.isMultiDamage);
                } else {
                    m.setMove(this.moveByte, this.moveIntent);
                }
                m.createIntent();
                m.applyPowers();
            }
        }
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
        int avgAmt = (int)Math.round(this.amount / (double)ctr);
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m != this.owner && !m.isDeadOrEscaped()) {
                if (m.hasPower(POWER_ID) && m.getPower(POWER_ID).amount + avgAmt > 20) {
                    avgAmt = 20 - m.getPower(POWER_ID).amount;
                }
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this.owner, new CongealedPower(m, avgAmt)));
                this.addToBot(new TriggerMarksAction(this.callingCard));
            }
        }
    }
}

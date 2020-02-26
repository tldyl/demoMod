package demoMod.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.DemoMod;
import demoMod.effects.RelodestoneEffect;
import demoMod.sounds.DemoSoundMaster;

public class Relodestone extends AbstractClickRelic {

    public static final String ID = DemoMod.makeID("Relodestone");
    public static final String IMG_PATH = "relics/relodestone.png";
    private int maxCharge = 6;
    private boolean enabled = false;
    private boolean working = false;
    private RelodestoneEffect effect;

    public Relodestone() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.RARE, LandingSound.SOLID);
        this.counter = maxCharge;
        this.effect = new RelodestoneEffect();
    }

    public void onEquip() {
        this.beginLongPulse();
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public void atTurnStart() {
        if (this.working) {
            this.flash();
            int[] damageMatrix = new int[AbstractDungeon.getCurrRoom().monsters.monsters.size()];
            for (int i=0;i<damageMatrix.length;i++) {
                damageMatrix[i] = this.counter;
            }
            AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(AbstractDungeon.player, damageMatrix, DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.BLUNT_HEAVY));
            this.counter = 0;
            effect.stop();
        }
        this.working = false;
        this.enabled = true;
    }

    @Override
    public void onPlayerEndTurn() {
        this.enabled = false;
    }

    @Override
    public void onVictory() {
        if (this.working) this.counter = 0;
        if (this.counter < maxCharge) {
            this.counter++;
        } else {
            if (!this.pulse) this.beginLongPulse();
        }
        this.working = false;
        this.enabled = false;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Relodestone();
    }

    @Override
    protected void onRightClick() {
        if (this.counter == this.maxCharge && this.enabled) {
            DemoSoundMaster.playV("RELIC_RELODESTONE", 0.1F);
            effect.start();
            AbstractDungeon.effectsQueue.add(effect);
            this.flash();
            this.stopPulse();
            this.counter = 0;
            this.working = true;
        }
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (damageAmount > 0 && this.working && info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS) {
            this.flash();
            this.counter += damageAmount;
            return 0;
        }
        return damageAmount;
    }
}

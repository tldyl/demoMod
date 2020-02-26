package demoMod.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.guns.LifeOrb;
import demoMod.effects.LifeOrbEffect;
import demoMod.sounds.DemoSoundMaster;

public class LifeOrbAction extends AbstractGameAction {
    private int damage;
    private int multi;
    private LifeOrb card;
    private LifeOrbEffect lifeOrbEffect;
    private AbstractMonster target;
    private int startingCounter;
    private float durationCounter;

    public LifeOrbAction(int damage, int multi, LifeOrb card, AbstractMonster target) {
        this.damage = damage;
        this.multi = multi;
        this.card = card;
        this.duration = 2.0F;
        lifeOrbEffect = new LifeOrbEffect(target);
        this.target = target;
        this.startingCounter = 0;
        this.durationCounter = 0.0F;
    }

    @Override
    public void update() {
        if (this.duration == 2.0F) {
            DemoSoundMaster.playV("GUN_FIRE_LIFE_ORB", 0.1F);
            DemoMod.effectsQueue.add(lifeOrbEffect);
        }

        if (lifeOrbEffect.beginToTakeDamage && this.startingCounter < this.multi) {
            if (this.durationCounter >= 0.2F) {
                this.startingCounter++;
                this.target.damage(new DamageInfo(AbstractDungeon.player, this.damage, DamageInfo.DamageType.NORMAL));
                if (this.target.isDead || this.target.currentHealth <= 0) {
                    this.card.reloadDamage += this.target.maxHealth;
                    this.card.portrait = new TextureAtlas.AtlasRegion(new Texture(DemoMod.getResourcePath("cards/lifeOrb_saved.png")), 0, 0, 250, 190);
                    DemoSoundMaster.playV("GUN_KILLED_LIFE_ORB", 0.1F);
                    for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                        if (card.uuid.equals(this.card.uuid)) {
                            LifeOrb lifeOrb = (LifeOrb)card;
                            lifeOrb.reloadDamage += this.target.maxHealth;
                            lifeOrb.portrait = this.card.portrait;
                            break;
                        }
                    }
                    this.isDone = true;
                }
                this.durationCounter = 0.0F;
            } else {
                this.durationCounter += Gdx.graphics.getDeltaTime();
            }
        }

        this.tickDuration();
    }
}

package demoMod.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.sounds.DemoSoundMaster;


public class VorpalBullet extends CustomRelic implements CustomSavable<Double> {
    public static final String ID = "DemoMod:VorpalBullet";
    public static final String IMG_PATH = "relics/vorpalBullet.png";
    public static final String OUTLINE_IMG_PATH = "relics/vorpalBulletOutline.png";
    private boolean activated = false;

    public static double chance = 0.03;

    public VorpalBullet() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)), new Texture(DemoMod.getResourcePath(OUTLINE_IMG_PATH)),
                RelicTier.RARE, LandingSound.CLINK);
    }

    private void setDescriptionAfterLoading() {
        this.description = this.DESCRIPTIONS[0] + this.DESCRIPTIONS[1] + (int)(chance * 100) + this.DESCRIPTIONS[2];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (monster.type == AbstractMonster.EnemyType.BOSS) {
                activated = false;
                return;
            }
        }
        setDescriptionAfterLoading();
        activated = true;
    }

    @Override
    public void onVictory() {
        activated = false;
    }

    @Override
    public int onAttackToChangeDamage(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner == AbstractDungeon.player && activated) {
            double ran = AbstractDungeon.miscRng.random(1.0F);
            if (ran <= chance) {
                this.flash();
                DemoSoundMaster.playV("RELIC_VORPAL_GUN", 0.1F);
                chance = 0.03;
                setDescriptionAfterLoading();
                return 9 * damageAmount;
            } else {
                chance += 0.01;
            }
            setDescriptionAfterLoading();
        }
        return damageAmount;
    }

    @Override
    public Double onSave() {
        return chance;
    }

    @Override
    public void onLoad(Double loadChance) {
        if (loadChance != null) {
            chance = loadChance;
        } else {
            chance = 0.03;
        }
    }
}

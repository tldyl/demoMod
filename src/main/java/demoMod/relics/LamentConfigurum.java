package demoMod.relics;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.DemoMod;
import demoMod.actions.ObtainRelicAction;
import demoMod.characters.HuntressCharacter;
import demoMod.monsters.*;
import demoMod.sounds.DemoSoundMaster;

import java.util.HashMap;
import java.util.Map;

public class LamentConfigurum extends AbstractClickRelic implements CustomSavable<Integer> {
    public static final String ID = DemoMod.makeID("LamentConfigurum");
    public static final String IMG_PATH = "relics/lamentConfigurum.png";

    private int maxCount = 2;
    private int relicGained = 0;
    private boolean enabled = false;

    private static Map<Integer, NewMonster> monsterFactory;

    public LamentConfigurum() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.COMMON, LandingSound.CLINK);
        this.counter = maxCount;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public boolean canSpawn() {
        return Settings.isEndless || AbstractDungeon.floorNum <= 31;
    }

    @Override
    public void onEquip() {
        DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse++;
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new LamentConfigurum();
    }

    @Override
    public void atBattleStart() {
        this.enabled = true;
    }

    @Override
    public void onVictory() {
        this.enabled = false;
    }

    @Override
    protected void onRightClick() {
        if (enabled && !this.usedUp) {
            this.counter--;
            this.enabled = false;
            for (int i=0;i<this.maxCount;i++) {
                this.addToBot(new SpawnMonsterAction(getRandomMonster(), false));
            }

            if (this.counter <= 0) {
                this.maxCount++;
                this.counter = this.maxCount;
                this.relicGained++;
                if (this.relicGained >= 5) {
                    this.usedUp();
                    this.counter = -2;
                }
                DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
                if (AbstractDungeon.player instanceof HuntressCharacter) {
                    HuntressCharacter.curse++;
                }
                RelicTier tier = AbstractDungeon.relicRng.random(1) == 1 ? RelicTier.UNCOMMON : RelicTier.RARE;
                this.addToBot(new ObtainRelicAction(AbstractDungeon.returnRandomRelic(tier)));
            }
        }
    }

    public static AbstractMonster getRandomMonster() {
        return monsterFactory.get(AbstractDungeon.miscRng.random(4)).getMonster();
    }

    static {
        monsterFactory = new HashMap<>();
        monsterFactory.put(0, () -> new BulletKin(MathUtils.random(-300.0F, 180.0F), MathUtils.random(-100.0F, 380.0F), MathUtils.randomBoolean()));
        monsterFactory.put(1, () -> new RedShotgunKin(MathUtils.random(-300.0F, 180.0F), MathUtils.random(-100.0F, 380.0F)));
        monsterFactory.put(2, () -> new BlueShotgunKin(MathUtils.random(-300.0F, 180.0F), MathUtils.random(-100.0F, 380.0F)));
        monsterFactory.put(3, () -> new VeteranBulletKin(MathUtils.random(-300.0F, 180.0F), MathUtils.random(-100.0F, 380.0F)));
        monsterFactory.put(4, () -> new VeteranShotgunKin(MathUtils.random(-300.0F, 180.0F), MathUtils.random(-100.0F, 380.0F)));
    }

    @Override
    public Integer onSave() {
        return this.maxCount;
    }

    @Override
    public void onLoad(Integer i) {
        if (i != null) {
            this.maxCount = i;
        } else {
            this.maxCount = 2;
        }
    }

    private interface NewMonster {
        AbstractMonster getMonster();
    }
}

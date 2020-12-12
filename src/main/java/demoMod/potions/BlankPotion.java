package demoMod.potions;

import basemod.abstracts.CustomPotion;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import demoMod.DemoMod;
import demoMod.effects.BlankWaveEffect;
import demoMod.sounds.DemoSoundMaster;
import demoMod.utils.CustomPotionTexture;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    public boolean canUse() {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) return true;
        if (AbstractDungeon.getCurrRoom() instanceof EventRoom) {
            AbstractEvent event = AbstractDungeon.getCurrRoom().event;
            if (event instanceof AbstractImageEvent) {
                AbstractImageEvent imageEvent = (AbstractImageEvent) event;
                if (Loader.isModLoaded("GungeonModExtend")) {
                    try {
                        Class<?> cls = Class.forName("demoMod.events.SecretRoom");
                        return imageEvent.getClass().isAssignableFrom(cls);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return super.canUse();
    }

    @Override
    public void use(AbstractCreature abstractCreature) {
        AbstractPlayer p = AbstractDungeon.player;
        DemoSoundMaster.playV("POTION_BLANK", 0.1F);
        if (AbstractDungeon.getCurrRoom() instanceof EventRoom && AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.SHORT, false);
            AbstractEvent event = AbstractDungeon.getCurrRoom().event;
            try {
                Method method = AbstractEvent.class.getDeclaredMethod("buttonEffect", int.class);
                method.setAccessible(true);
                method.invoke(event, 1);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        }
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

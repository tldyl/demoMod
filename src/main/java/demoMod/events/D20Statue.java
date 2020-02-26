package demoMod.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.potions.SmokeBomb;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import demoMod.DemoMod;
import demoMod.cards.SpreadAmmo;
import demoMod.characters.HuntressCharacter;
import demoMod.effects.TimeFreezeEffect;
import demoMod.potions.BlankPotion;
import demoMod.relics.Armor;
import demoMod.relics.Limited;
import demoMod.relics.StrengthOfFortune;
import demoMod.relics.Unsteady;
import demoMod.sounds.DemoSoundMaster;

public class D20Statue extends AbstractImageEvent {
    public static final String ID = DemoMod.makeID("D20Statue");
    private static final EventStrings eventStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;

    private DiceEffects[] goodEffects = new DiceEffects[9];
    private DiceEffects[] badEffects = new DiceEffects[9];

    private int screenNum = 0;

    public D20Statue() {
        super(NAME, DESCRIPTIONS[0] + DESCRIPTIONS[1], DemoMod.getResourcePath("events/D20Statue.png"));
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        initGoodEffects();
        initBadEffects();
    }

    private void initGoodEffects() {
        DiceEffects renewed = () -> {
            int healAmount = AbstractDungeon.miscRng.random(10, 14);
            AbstractDungeon.player.heal(healAmount);
        };
        DiceEffects bolstered = () -> {
            int healAmount = AbstractDungeon.miscRng.random(5, 10);
            AbstractDungeon.player.increaseMaxHp(healAmount, true);
        };
        DiceEffects hasted = () -> { //原版为加移速
            for(int i = 0; i < AbstractDungeon.player.potionSlots; ++i) {
                AbstractDungeon.player.obtainPotion(new SmokeBomb());
            }
        };
        DiceEffects paid = () -> {
            int golds = AbstractDungeon.miscRng.random(20, 99);
            AbstractDungeon.player.gainGold(golds);
            AbstractDungeon.effectList.add(new RainingGoldEffect(golds));
        };
        DiceEffects shielded = () -> {
            int amount = AbstractDungeon.miscRng.random(2);
            if (AbstractDungeon.player.hasRelic(DemoMod.makeID("Armor"))) {
                AbstractDungeon.player.getRelic(DemoMod.makeID("Armor")).counter += amount + 1;
            } else {
                Armor armor = new Armor();
                armor.counter += amount;
                armor.instantObtain();
            }
        };
        DiceEffects cleansed = () -> {
            if (HuntressCharacter.curse > 10) {
                HuntressCharacter.curse -= 10;
            } else if (HuntressCharacter.curse > 0) {
                HuntressCharacter.curse = 0;
            }
        };
        DiceEffects gift = () -> {
            AbstractRelic relic = AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier());
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), relic);
        };
        DiceEffects reloaded = () -> { //原版为弹药容量提升125%或所有枪填满弹药
            AbstractDungeon.effectsQueue.add(new ShowCardAndObtainEffect(new SpreadAmmo(), (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
        };
        DiceEffects blanked = () -> {
            for(int i = 0; i < AbstractDungeon.player.potionSlots; ++i) {
                AbstractDungeon.player.obtainPotion(new BlankPotion());
            }
        };

        goodEffects[0] = renewed;
        goodEffects[1] = bolstered;
        goodEffects[2] = hasted;
        goodEffects[3] = paid;
        goodEffects[4] = shielded;
        goodEffects[5] = cleansed;
        goodEffects[6] = gift;
        goodEffects[7] = reloaded;
        goodEffects[8] = blanked;
    }

    private void initBadEffects() {
        DiceEffects pained = () -> {
            int curHp = AbstractDungeon.player.currentHealth;
            int damageAmount = AbstractDungeon.miscRng.random(curHp / 10, curHp / 2);
            CardCrawlGame.sound.play("DEBUFF_2");
            AbstractDungeon.player.damage(new DamageInfo(null, damageAmount, DamageInfo.DamageType.HP_LOSS));
        };
        DiceEffects enfeebled = () -> AbstractDungeon.player.decreaseMaxHealth(5);
        DiceEffects robbed = () -> {
            int robAmount = (int)(AbstractDungeon.player.gold * AbstractDungeon.miscRng.random(0.25F, 1.0F));
            AbstractDungeon.player.loseGold(robAmount);
        };
        DiceEffects disarmed = () -> {
            AbstractCard card = CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck).getRandomCard(AbstractDungeon.cardRng);
            AbstractDungeon.player.masterDeck.removeCard(card);
            AbstractDungeon.effectList.add(new PurgeCardEffect(card));
        };
        DiceEffects limited = () -> { //原版为弹药容量减少30%
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new Limited());
        };
        DiceEffects deBlanked = () -> {
            for(int i = 0; i < AbstractDungeon.player.potionSlots; ++i) {
                if (!(AbstractDungeon.player.potions.get(i) instanceof PotionSlot)) {
                    AbstractDungeon.topPanel.destroyPotion(i);
                }
            }
        };
        DiceEffects cursed = () -> {
            DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
            HuntressCharacter.curse += 5;
        };
        DiceEffects unsteady = () -> { //原版为增加装弹时间
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new Unsteady());
        };
        DiceEffects priceless = () -> {
            //没有坏效果
        };

        badEffects[0] = pained;
        badEffects[1] = enfeebled;
        badEffects[2] = robbed;
        badEffects[3] = disarmed;
        badEffects[4] = limited;
        badEffects[5] = deBlanked;
        badEffects[6] = cursed;
        badEffects[7] = unsteady;
        badEffects[8] = priceless;
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0: //进入房间
                switch (buttonPressed) {
                    case 0: //选择“滚动石头”
                        this.screenNum = 1;
                        if (AbstractDungeon.miscRng.random(99) < 99) {
                            int goodIndex = AbstractDungeon.miscRng.random(0, 8);
                            int badIndex = AbstractDungeon.miscRng.random(0, 8);
                            DiceEffects goodEffects = this.goodEffects[goodIndex];
                            DiceEffects badEffects = this.badEffects[badIndex];
                            goodEffects.apply();
                            badEffects.apply();
                            CardCrawlGame.screenShake.mildRumble(5.0F);
                            if (Settings.AMBIANCE_ON) {
                                CardCrawlGame.sound.play("EVENT_GOLDEN");
                            }
                            this.imageEventText.updateBodyText(DESCRIPTIONS[3] + DESCRIPTIONS[4] +
                                    " NL #g~" + DESCRIPTIONS[5 + goodIndex] +
                                    "~   #r~" + DESCRIPTIONS[14 + badIndex] + "~ NL " +
                                    DESCRIPTIONS[23]
                            );
                        } else {
                            DemoMod.effectsQueue.add(new TimeFreezeEffect(2.0F, false));
                            DemoSoundMaster.playA("RELIC_BIG_BOY", 0.0F);
                            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.XLONG, true);
                            AbstractDungeon.player.maxHealth = 1;
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new StrengthOfFortune());
                            this.imageEventText.updateBodyText(DESCRIPTIONS[24] + DESCRIPTIONS[25] + DESCRIPTIONS[26]);
                        }
                        break;
                    case 1: //选择无视
                        this.screenNum = 1;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        break;
                }
                this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                this.imageEventText.clearRemainingOptions();
                break;
            case 1: //做出选择后
                this.openMap();
                break;
            default:
                this.openMap();
        }
    }

    interface DiceEffects {
        void apply();
    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString(DemoMod.makeID("D20Statue"));
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
    }
}

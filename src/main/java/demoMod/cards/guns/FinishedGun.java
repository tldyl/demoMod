package demoMod.cards.guns;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.characters.HuntressCharacter;
import demoMod.sounds.DemoSoundMaster;

import java.lang.reflect.Field;

@SuppressWarnings("Duplicates")
public class FinishedGun extends AbstractGunCard implements PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("FinishedGun");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/finishedGun.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private int BASE_DMG = 14;

    private static final int COST = 1;
    private boolean isAdded = false;

    public FinishedGun() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.capacity = 3;
        this.maxCapacity = 3;
        this.baseDamage = 14;
        this.extraDamage = 11;
        this.isSemiAutomatic = true;
    }

    public void onAddedToMasterDeck() {
        if (!isAdded) {
            DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                HuntressCharacter.curse += 5;
            }
            isAdded = true;
        }
    }

    @Override
    public void onRemoveFromMasterDeck() {
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse -= 5;
        }
    }

    @Override
    public void reload() {
        if (this.capacity == this.maxCapacity) return; //不允许满弹夹装弹
        if (this.capacity == 0) this.baseDamage = BASE_DMG;
        this.capacity = this.maxCapacity;
        DemoSoundMaster.playA(this.reloadSoundKey, 0F);
        if (AbstractDungeon.player.hasRelic("DemoMod:HipHolster")) {
            AbstractDungeon.player.getRelic("DemoMod:HipHolster").flash();
            AbstractMonster m = AbstractDungeon.getRandomMonster();
            DemoSoundMaster.playA("GUN_FIRE_FINISHED_1", 0F);
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(AbstractDungeon.player, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        }
        afterReload();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
        this.baseDamage = BASE_DMG;
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        if (this.capacity == 0) {
            this.baseDamage += this.extraDamage;
            this.calculateCardDamage(null);
            DemoSoundMaster.playA("GUN_FIRE_FINISHED_2", 0F);
        } else {
            DemoSoundMaster.playA("GUN_FIRE_FINISHED_1", 0F);
        }
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        if (this.capacity == 0) {
            int multi = 1;
            try {
                Field multiField = AbstractMonster.class.getDeclaredField("intentMultiAmt");
                multiField.setAccessible(true);
                multi = (int)multiField.get(m);
                if (multi <= 0) multi = 1;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (m.getIntentBaseDmg() > 0) AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, m.getIntentDmg() * multi, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(4);
            this.BASE_DMG += 4;
            this.extraDamage += 4;
            this.isExtraDamageModified = true;
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

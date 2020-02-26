package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import demoMod.DemoMod;
import demoMod.effects.TextureAboveCreatureEffect;
import demoMod.sounds.DemoSoundMaster;

public class JK47 extends AbstractGunCard {
    public static final String ID = DemoMod.makeID("JK47");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/JK47.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;

    public JK47() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 5;
        this.baseMagicNumber = 2;
        this.capacity = 6;
        this.maxCapacity = 6;
        this.reloadSoundKey = "GUN_RELOAD_JK47";
    }

    @Override
    public void reload() {
        AbstractPlayer p = AbstractDungeon.player;
        if (this.capacity == this.maxCapacity) return;
        if (AbstractDungeon.player.hasRelic("DemoMod:HipHolster")) {
            AbstractDungeon.player.getRelic("DemoMod:HipHolster").flash();
            AbstractMonster m = AbstractDungeon.getRandomMonster();
            DemoSoundMaster.playV("GUN_FIRE_JK47", 0.1F);
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        }
        DemoSoundMaster.playA(this.reloadSoundKey, 0.0F);
        this.capacity = this.maxCapacity;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped()) {
                DemoMod.effectsQueue.add(new TextureAboveCreatureEffect(m, new Texture("DemoImages/effects/jk47.png")));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new WeakPower(m, this.baseMagicNumber, false)));
            }
        }
        afterReload();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!monster.isDeadOrEscaped()) {
                DemoMod.effectsQueue.add(new TextureAboveCreatureEffect(monster, new Texture("DemoImages/effects/jk47.png")));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(monster, p, new WeakPower(monster, this.baseMagicNumber, false)));
            }
        }
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        DemoSoundMaster.playV("GUN_FIRE_JK47", 0.1F);
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(2);
            this.upgradeMagicNumber(1);
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

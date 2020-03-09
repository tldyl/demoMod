package demoMod.cards.guns;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rewards.RewardItem;
import demoMod.DemoMod;
import demoMod.rewards.GlassGuonStone;
import demoMod.sounds.DemoSoundMaster;

public class DirectionalPad extends AbstractGunCard {
    public static final String ID = DemoMod.makeID("DirectionalPad");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/directionalPad.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;

    public DirectionalPad() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 7;
        this.baseBlock = 9;
        this.capacity = 3;
        this.maxCapacity = 3;
    }

    @Override
    public void reload() {
        if (this.capacity == this.maxCapacity) return; //不允许满弹夹装弹
        this.capacity = this.maxCapacity;
        this.exhaust = false;
        DemoSoundMaster.playA(this.reloadSoundKey, 0F);
        if (AbstractDungeon.player.hasRelic("DemoMod:HipHolster")) {
            AbstractDungeon.player.getRelic("DemoMod:HipHolster").flash();
            AbstractMonster m = AbstractDungeon.getRandomMonster();
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(AbstractDungeon.player, this.damage), AbstractGameAction.AttackEffect.FIRE));
        }
        afterReload();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
        if (this.capacity == this.maxCapacity) {
            fire(p, m);
            AbstractDungeon.player.masterDeck.removeCard(this.cardID);
            addRandomRewards();
        }
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(AbstractDungeon.player, this.damage), AbstractGameAction.AttackEffect.FIRE));
        if (AbstractDungeon.actionManager.cardsPlayedThisCombat.size() >= 2 && (AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 2)).type == CardType.ATTACK) {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, this.block));
        }
        if (isReload && p.hasPower(DemoMod.makeID("SlingerPower")) && !p.hasRelic("DemoMod:HipHolster")) {
            AbstractDungeon.player.masterDeck.removeCard(this.cardID);
            addRandomRewards();
        }
    }

    public void triggerOnGlowCheck() {
        if (!AbstractDungeon.actionManager.cardsPlayedThisCombat.isEmpty() && (AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1)).type == CardType.ATTACK) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(4);
            this.upgradeBlock(3);
        }
    }

    private void addRandomRewards() {
        double ran = AbstractDungeon.treasureRng.random(1.0F);
        if (ran < 0.2F) {
            int gold = 10 + AbstractDungeon.treasureRng.random(50);
            AbstractDungeon.getCurrRoom().addGoldToRewards(gold);
        } else if (ran < 0.3F) {
            AbstractDungeon.getCurrRoom().rewards.add(new GlassGuonStone());
            AbstractDungeon.getCurrRoom().rewards.add(new GlassGuonStone());
            AbstractDungeon.getCurrRoom().rewards.add(new GlassGuonStone());
        } else if (ran < 0.4F) {
            RewardItem cardReward = new RewardItem();
            cardReward.cards = AbstractDungeon.getRewardCards();
            AbstractDungeon.getCurrRoom().addCardReward(cardReward);
        } else if (ran < 0.7F) {
            AbstractDungeon.getCurrRoom().addPotionToRewards();
        } else {
            AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier()));
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

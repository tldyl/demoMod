package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.watcher.SkipEnemiesTurnAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import demoMod.DemoMod;
import demoMod.cards.interfaces.ChargeCard;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.effects.TimeFreezeEffect;
import demoMod.powers.BulletBorePower;
import demoMod.sounds.DemoSoundMaster;

@SuppressWarnings("Duplicates")
public class MegaHand extends AbstractGunCard implements Combo, ChargeCard, PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("MegaHand");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/megaHand.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/megaHand.png"));
    public static final Texture IMG_AIR_SHOOTER = new Texture(DemoMod.getResourcePath("cards/airShooter.png"));
    public static final Texture IMG_CRASH_BOMBER = new Texture(DemoMod.getResourcePath("cards/crashBomber.png"));
    public static final Texture IMG_TIME_LIMITER = new Texture(DemoMod.getResourcePath("cards/timeLimiter.png"));
    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static boolean combos[] = new boolean[]{false, false, false, false, false, false, false, false};
    private static int baseDamages[] = new int[]{8, 7, 0, 10, 10, 0, 0, 0, 0};
    private static final int COST = 1;
    private int mode = -1;
    private boolean normalPlay = false;
    private boolean isRemoving = false;

    public MegaHand() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 8;
        this.capacity = 6;
        this.maxCapacity = 6;
        this.baseMagicNumber = 20;
        this.canFullReload = true;
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        int tmp = this.baseDamage;
        this.baseDamage = this.baseMagicNumber;
        super.calculateCardDamage(m);
        this.magicNumber = m != null ? this.damage : this.multiDamage[0];
        this.baseDamage = tmp;
        super.calculateCardDamage(m);
    }

    @Override
    public void reload() {
        if (this.capacity == this.maxCapacity) {
            if (this.mode != -1) {
                int t = this.mode;
                for (int i=1;i<combos.length;i++) {
                    if (combos[(i + this.mode) % combos.length]) {
                        this.mode = (i + this.mode) % combos.length;
                        break;
                    }
                }
                if (t == this.mode) this.mode = -1;
            } else {
                for (int i=0;i<combos.length;i++) {
                    if (combos[i]) {
                        this.mode = i;
                        break;
                    }
                }
                if (this.mode == -1) return;
            }
            modeShift();
        }
        if (AbstractDungeon.player.hasRelic("DemoMod:HipHolster")) {
            AbstractDungeon.player.getRelic("DemoMod:HipHolster").flash();
            AbstractMonster m = AbstractDungeon.getRandomMonster();
            normalPlay = false;
            onNonCharge(AbstractDungeon.player, m);
        }
        this.capacity = this.maxCapacity;
        afterReload();
    }

    private void modeShift() {
        if (this.mode != -1) {
            this.name = cardStrings.EXTENDED_DESCRIPTION[this.mode];
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[this.mode + combos.length];
        } else {
            this.name = NAME;
            this.rawDescription = DESCRIPTION;
        }
        this.baseDamage = baseDamages[this.mode + 1];
        this.initializeDescription();
        this.upgradeBaseCost(1);
        this.target = this.defaultTarget;
        switch (this.mode) {
            case -1:
                this.portrait = new TextureAtlas.AtlasRegion(new Texture(DemoMod.getResourcePath(IMG_PATH)), 0, 0, 250, 190);
                break;
            case 0:
                this.portrait = new TextureAtlas.AtlasRegion(IMG_AIR_SHOOTER, 0, 0, 250, 190);
                break;
            case 2:
                this.portrait = new TextureAtlas.AtlasRegion(IMG_CRASH_BOMBER, 0, 0, 250, 190);
                break;
            case 3:
                this.portrait = new TextureAtlas.AtlasRegion(IMG_TIME_LIMITER, 0, 0, 250, 190);
                this.target = CardTarget.ALL_ENEMY;
                this.updateCost(1);
                this.costForTurn = 2;
                break;
        }
        if (this.upgraded) this.name += "+";
        this.initializeTitle();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        if (this.mode == -1 && EnergyPanel.getCurrentEnergy() >= this.costForTurn + 1) {
            select(m);
        } else {
            normalPlay = true;
            onNonCharge(p, m);
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(4);
            this.upgradeMagicNumber(6);
        }
    }

    @Override
    public void onCharge(AbstractPlayer p, AbstractMonster m) {
        EnergyPanel.setEnergy(EnergyPanel.getCurrentEnergy() - 1);
        int t = this.baseDamage;
        this.baseDamage = this.baseMagicNumber;
        this.calculateCardDamage(m);
        DemoSoundMaster.playA("GUN_FIRE_MEGAHAND_2", 0.0F);
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        this.baseDamage = t;
    }

    @Override
    public void onNonCharge(AbstractPlayer p, AbstractMonster m) {
        switch (this.mode) {
            default:
            case -1: //默认形态
                DemoSoundMaster.playA("GUN_FIRE_MEGAHAND_1", 0.0F);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
            case 0: //空中射手
                int t = this.baseDamage;
                this.baseDamage = 7;
                this.calculateCardDamage(m);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
                if (!m.isDying && !m.isEscaping) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
                }
                if (!m.isDying && !m.isEscaping) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
                }
                this.baseDamage = t;
                break;
            case 1:
            case 2: //撞击轰炸机
                AbstractDungeon.actionManager.addToBottom(new LoseHPAction(m, p, this.damage));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new BulletBorePower(m, this.damage)));
                this.mode = -1;
                modeShift();
                break;
            case 3: //时间限制器
                if (p.hasRelic(DemoMod.makeID("HipHolster")) && !normalPlay) {
                    DemoSoundMaster.playA("GUN_FIRE_MEGAHAND_1", 0.0F);
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                    break;
                }
                DemoSoundMaster.playA("GUN_FIRE_TIME_LIMITER", 0.0F);
                DemoMod.effectsQueue.add(new TimeFreezeEffect());
                this.calculateCardDamage(null);
                AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new SkipEnemiesTurnAction());
                this.mode = -1;
                modeShift();
                break;
        }
        p.cardInUse = this;
    }

    @Override
    public void onRemoveFromMasterDeck() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public String getItemId() {
        return DemoMod.makeID("MegaHand");
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:-A-":
                combos[0] = true;
                break;
            case "DemoMod:-B-":
                combos[1] = true;
                break;
            case "DemoMod:-C-":
                combos[2] = true;
                break;
            case "DemoMod:-F-":
                combos[3] = true;
                break;
            case "DemoMod:-H-":
                combos[4] = true;
                break;
            case "DemoMod:-M-":
                combos[5] = true;
                break;
            case "DemoMod:-Q-":
                combos[6] = true;
                break;
            case "DemoMod:-W-":
                combos[7] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:-A-":
                combos[0] = false;
                break;
            case "DemoMod:-B-":
                combos[1] = false;
                break;
            case "DemoMod:-C-":
                combos[2] = false;
                break;
            case "DemoMod:-F-":
                combos[3] = false;
                break;
            case "DemoMod:-H-":
                combos[4] = false;
                break;
            case "DemoMod:-M-":
                combos[5] = false;
                break;
            case "DemoMod:-Q-":
                combos[6] = false;
                break;
            case "DemoMod:-W-":
                combos[7] = false;
                break;
        }
    }

    @Override
    public boolean isRemoving() {
        return isRemoving;
    }

    @Override
    public Texture getComboPortrait() {
        return comboTexture;
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        ComboManager.addCombo(DemoMod.makeID("-A-"), MegaHand.class);
        ComboManager.addCombo(DemoMod.makeID("-B-"), MegaHand.class);
        ComboManager.addCombo(DemoMod.makeID("-C-"), MegaHand.class);
        ComboManager.addCombo(DemoMod.makeID("-F-:AgedBell"), MegaHand.class);
        ComboManager.addCombo(DemoMod.makeID("-F-:Camera"), MegaHand.class);
        ComboManager.addCombo(DemoMod.makeID("-H-"), MegaHand.class);
        ComboManager.addCombo(DemoMod.makeID("-M-"), MegaHand.class);
        ComboManager.addCombo(DemoMod.makeID("-Q-"), MegaHand.class);
        ComboManager.addCombo(DemoMod.makeID("-W-"), MegaHand.class);
    }

    @Override
    public void onAddedToMasterDeck() {
        ComboManager.detectComboInGame();
    }
}

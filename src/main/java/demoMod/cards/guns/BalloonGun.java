package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.powers.LoseFlightPower;
import demoMod.powers.PlayerFlightPower;
import demoMod.sounds.DemoSoundMaster;

public class BalloonGun extends AbstractGunCard implements Combo, PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("BalloonGun");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/balloonGun.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/balloonGun.png"));

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;

    private boolean isRemoving = false;

    public BalloonGun() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 5;
        this.damage = this.baseDamage;
        this.capacity = 3;
        this.maxCapacity = 3;
        this.reloadSoundKey = "GUN_RELOAD_BALLOON";
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(2);
        }
    }

    @Override
    public void triggerWhenDrawn() {
        AbstractPlayer p = AbstractDungeon.player;
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new PlayerFlightPower(p, 1)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new LoseFlightPower(p)));
    }

    @Override
    public void triggerOnManualDiscard() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.hasPower("DemoMod:PlayerFlightPower") && !p.hand.contains(this)) {
            p.getPower("DemoMod:PlayerFlightPower").amount--;
            p.getPower("DemoMod:PlayerFlightPower").atStartOfTurn();
            if (p.hasPower("DemoMod:LoseFlightPower")) AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(p, p, "DemoMod:LoseFlightPower"));
        }
    }

    @Override
    public void triggerAtStartOfTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        if (AbstractDungeon.player.hand.contains(this)) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new PlayerFlightPower(p, 1)));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new LoseFlightPower(p)));
        }
    }

    @Override
    public void tookDamage() {
        if (AbstractDungeon.player.hand.group.contains(this)) {
            AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(this, AbstractDungeon.player.hand));
        } else if (AbstractDungeon.player.drawPile.group.contains(this)) {
            AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(this, AbstractDungeon.player.drawPile));
        } else if (AbstractDungeon.player.discardPile.group.contains(this)) {
            AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(this, AbstractDungeon.player.discardPile));
        } else {
            AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(this, AbstractDungeon.player.limbo));
        }
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        if (!m.isDying && !m.isEscaping) {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        }
        if (!m.isDying && !m.isEscaping) {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        }
    }

    @Override
    public void onRemoveFromMasterDeck() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        ComboManager.addCombo(DemoMod.makeID("-A-"), BalloonGun.class);
        ComboManager.addCombo(DemoMod.makeID("PaperLanterns"), BalloonGun.class);
        ComboManager.addCombo(DemoMod.makeID("ThreeSheets"), BalloonGun.class);
    }

    @Override
    public String getItemId() {
        return DemoMod.makeID("BalloonGun");
    }

    @Override
    public void onComboActivated(String comboId) {

    }

    @Override
    public void onComboDisabled(String comboId) {

    }

    @Override
    public boolean isRemoving() {
        return isRemoving;
    }

    @Override
    public Texture getComboPortrait() {
        return comboTexture;
    }

    @Override
    public void onAddedToMasterDeck() {
        ComboManager.detectComboInGame();
    }
}

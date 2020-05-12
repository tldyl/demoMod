package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import demoMod.DemoMod;
import demoMod.actions.DarkMarkerBlastAction;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.powers.BlueMarkPower;
import demoMod.powers.RedMarkPower;
import demoMod.sounds.DemoSoundMaster;

public class DarkMarker extends AbstractGunCard implements Combo, PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("DarkMarker");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/darkMarker.png";

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/darkMarker.png"));

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;
    private boolean blastMode = false;
    private static boolean redMark = true;
    private static boolean isCombo = false;

    private boolean isRemoving = false;
    private int BASE_DMG = 10;

    public DarkMarker() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 10;
        this.capacity = 4;
        this.maxCapacity = 4;
        this.canFullReload = true;
        if (isCombo) {
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
            this.initializeDescription();
        }
    }

    @Override
    public void reload() {
        if (this.capacity == this.maxCapacity) {
            blastMode = !blastMode;
            if (blastMode) {
                this.cost = -1;
                this.costForTurn = -1;
                this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
                this.exhaust = true;
                this.target = CardTarget.ALL_ENEMY;
                this.baseDamage /= 2;
                this.isMultiDamage = true;
            } else {
                this.cost = COST;
                this.costForTurn = COST;
                if (!isCombo) {
                    this.rawDescription = cardStrings.DESCRIPTION;
                } else {
                    if (redMark) {
                        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
                    } else {
                        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[2];
                    }
                }
                this.exhaust = false;
                this.target = this.defaultTarget;
                this.baseDamage = BASE_DMG;
                this.isMultiDamage = false;
            }
            this.initializeDescription();
        }
        this.capacity = this.maxCapacity;
        afterReload();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {

    }

    public void calculateCardDamage(AbstractMonster m) {
        super.calculateCardDamage(m);
        if (this.blastMode) {
            super.calculateCardDamage(null);
            AbstractPlayer p = AbstractDungeon.player;
            int boost = 0;
            if (p.hasRelic("Chemical X")) {
                boost = 2;
            }
            for (int i=0;i<this.multiDamage.length;i++) {
                this.multiDamage[i] *= this.energyOnUse + boost;
            }
            this.damage = this.multiDamage[0];
        }
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        if (blastMode) {
            if (this.energyOnUse > 0) {
                EnergyPanel.setEnergy(0);
                if (p.hasRelic("Chemical X")) {
                    p.getRelic("Chemical X").flash();
                }
                AbstractDungeon.actionManager.addToBottom(new DarkMarkerBlastAction(this));
            }
        } else {
            DemoSoundMaster.playV("GUN_FIRE_DARK_MARKER_1", 0.1F);
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
            if (isCombo) {
                if (redMark) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new RedMarkPower(m)));
                    redMark = false;
                } else {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new BlueMarkPower(m)));
                    redMark = true;
                }
                this.updateComboText();
                for (AbstractCard card : p.drawPile.group) {
                    if (card instanceof DarkMarker && card != this) {
                        DarkMarker darkMarker = (DarkMarker) card;
                        darkMarker.updateComboText();
                    }
                }
                for (AbstractCard card : p.discardPile.group) {
                    if (card instanceof DarkMarker && card != this) {
                        DarkMarker darkMarker = (DarkMarker) card;
                        darkMarker.updateComboText();
                    }
                }
                for (AbstractCard card : p.hand.group) {
                    if (card instanceof DarkMarker && card != this) {
                        DarkMarker darkMarker = (DarkMarker) card;
                        darkMarker.updateComboText();
                    }
                }
                for (AbstractCard card : p.exhaustPile.group) {
                    if (card instanceof DarkMarker && card != this) {
                        DarkMarker darkMarker = (DarkMarker) card;
                        darkMarker.updateComboText();
                    }
                }
            }
        }
    }

    private void updateComboText() {
        if (redMark) {
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[2];
        } else {
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
        }
        this.initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(3);
            this.BASE_DMG += 3;
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        ComboManager.addCombo(DemoMod.makeID("Antichamber"), DarkMarker.class);
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        isCombo = true;
    }

    @Override
    public void onComboDisabled(String comboId) {
        isCombo = false;
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

    @Override
    public void onRemoveFromMasterDeck() {
        isRemoving = true;
        ComboManager.detectCombo();
    }
}

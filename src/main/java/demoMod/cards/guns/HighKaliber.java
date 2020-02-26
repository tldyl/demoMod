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
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.powers.OutOfBodyPower;
import demoMod.sounds.DemoSoundMaster;

public class HighKaliber extends AbstractGunCard implements Combo, PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("HighKaliber");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/highKaliber.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/highKaliber.png"));

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;

    private static boolean combo = false;

    private boolean isRemoving = false;

    public HighKaliber() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.capacity = 3;
        this.maxCapacity = 3;
        this.baseDamage = 4;
        this.baseMagicNumber = 4;
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        DemoSoundMaster.playA("GUN_FIRE_KALIBER", 0F);
        if (combo) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, AbstractDungeon.player, new OutOfBodyPower(m, AbstractDungeon.player, this.baseMagicNumber + 2)));
        } else {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, AbstractDungeon.player, new OutOfBodyPower(m, AbstractDungeon.player, this.baseMagicNumber)));
        }
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(AbstractDungeon.player, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(2);
            this.upgradeMagicNumber(2);
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
        ComboManager.addCombo(DemoMod.makeID("Kaliber k'pow uboom k'bhang:CursedBullets"), HighKaliber.class);
        ComboManager.addCombo(DemoMod.makeID("Kaliber k'pow uboom k'bhang:HoleyGrail"), HighKaliber.class);
        ComboManager.addCombo(DemoMod.makeID("Kaliber k'pow uboom k'bhang:YellowChamber"), HighKaliber.class);
    }

    @Override
    public String getItemId() {
        return DemoMod.makeID("HighKaliber");
    }

    @Override
    public void onComboActivated(String comboId) {
        combo = true;
    }

    @Override
    public void onComboDisabled(String comboId) {
        combo = false;
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

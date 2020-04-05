package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.actions.BetterAttackDamageRandomEnemyAction;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.characters.HuntressCharacter;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.monsters.Decoy;
import demoMod.sounds.DemoSoundMaster;

@SuppressWarnings("Duplicates")
public class HuntsMan extends AbstractGunCard implements PostAddedToMasterDeckSubscriber,
                                                         Combo {
    public static final String ID = DemoMod.makeID("HuntsMan");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/huntsMan.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/huntsMan.png"));

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static boolean combos[] = new boolean[]{false, false, false, false};

    private static final int COST = 1;

    private boolean isRemoving = false;
    private boolean isAdded = false;

    public HuntsMan() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 2;
        this.baseBlock = 7;
        this.block = this.baseBlock;
        this.capacity = 3;
        this.maxCapacity = 3;
    }

    @Override
    public void reload() {
        if (this.capacity == this.maxCapacity) return;
        if (AbstractDungeon.player.hasRelic("DemoMod:HipHolster")) {
            AbstractDungeon.player.getRelic("DemoMod:HipHolster").flash();
            for (int i=0;i<4;i++) {
                this.addToBot(new BetterAttackDamageRandomEnemyAction(this, AbstractGameAction.AttackEffect.BLUNT_LIGHT, AbstractDungeon.getCurrRoom().monsters.getMonster(Decoy.ID)));
            }
        }
        this.calculateCardDamage(null);
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, this.block));
        this.capacity = this.maxCapacity;
        afterReload();
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
        this.calculateCardDamage(null);
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, this.block));
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster monster) {
        for (int i=0;i<4;i++) {
            this.addToBot(new BetterAttackDamageRandomEnemyAction(this, AbstractGameAction.AttackEffect.BLUNT_LIGHT, AbstractDungeon.getCurrRoom().monsters.getMonster(Decoy.ID)));
        }
        if (this.capacity == this.maxCapacity) {
            this.applyPowersToBlock();
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, this.block));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(3);
            this.upgradeDamage(1);
        }
    }

    @Override
    public void onAddedToMasterDeck() {
        if (!isAdded) {
            DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                HuntressCharacter.curse += 1;
            }
            isAdded = true;
        }
    }

    @Override
    public void onRemoveFromMasterDeck() {
        isRemoving = true;
        ComboManager.detectCombo();
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse -= 1;
        }
    }

    @Override
    public String getItemId() {
        return DemoMod.makeID("HuntsMan");
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:TheRedHood":
                combos[0] = true;
                break;
            case "DemoMod:Insight":
                combos[1] = true;
                break;
            case "DemoMod:IronStance":
                combos[2] = true;
                break;
            case "DemoMod:Lumberjacked":
                combos[3] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:TheRedHood":
                combos[0] = false;
                break;
            case "DemoMod:Insight":
                combos[1] = false;
                break;
            case "DemoMod:IronStance":
                combos[2] = false;
                break;
            case "DemoMod:Lumberjacked":
                combos[3] = false;
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
        ComboManager.addCombo(DemoMod.makeID("TheRedHood"), HuntsMan.class);
        ComboManager.addCombo(DemoMod.makeID("Insight"), HuntsMan.class);
        ComboManager.addCombo(DemoMod.makeID("IronStance"), HuntsMan.class);
        ComboManager.addCombo(DemoMod.makeID("Lumberjacked"), HuntsMan.class);
    }
}

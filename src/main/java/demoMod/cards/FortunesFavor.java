package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.interfaces.PostEnterNewActSubscriber;

import java.lang.reflect.Field;

import static demoMod.relics.HipHolster.activateComboEffect;

@SuppressWarnings("Duplicates")
public class FortunesFavor extends CustomCard implements Combo,
                                                         PostAddedToMasterDeckSubscriber,
                                                         PostEnterNewActSubscriber {

    public static final String ID = DemoMod.makeID("FortunesFavor");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/fortunesFavor.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final int COST = 2;

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/fortunesFavor.png"));
    private boolean isRemoving = false;
    private static boolean combo = false;

    public FortunesFavor() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(1);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int multi = 1;
        int sum = 0;
        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!monster.isDeadOrEscaped() &&
                    (monster.intent == AbstractMonster.Intent.ATTACK
                    || monster.intent == AbstractMonster.Intent.ATTACK_DEBUFF
                    || monster.intent == AbstractMonster.Intent.ATTACK_DEFEND
                    || monster.intent == AbstractMonster.Intent.ATTACK_BUFF)) {
                try {
                    Field multiField = AbstractMonster.class.getDeclaredField("intentMultiAmt");
                    multiField.setAccessible(true);
                    multi = (int) multiField.get(monster);
                    if (multi <= 0) multi = 1;
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                sum += monster.getIntentDmg() * multi;
            }
        }
        this.addToBot(new GainBlockAction(p, (int) Math.ceil(sum / 2.0)));
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        ComboManager.addCombo(DemoMod.makeID("PrettyGood:FortunesFavor"), FortunesFavor.class);
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

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        combo = true;
        activateComboEffect(comboId);
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:PrettyGood":
                PotionHelper.POTION_COMMON_CHANCE = 70;
                PotionHelper.POTION_UNCOMMON_CHANCE = 25;
                try {
                    Field commonRelicChanceField = AbstractDungeon.class.getDeclaredField("commonRelicChance");
                    commonRelicChanceField.setAccessible(true);
                    commonRelicChanceField.set(CardCrawlGame.dungeon, 50);
                    Field uncommonRelicChanceField = AbstractDungeon.class.getDeclaredField("uncommonRelicChance");
                    uncommonRelicChanceField.setAccessible(true);
                    uncommonRelicChanceField.set(CardCrawlGame.dungeon, 33);
                    Field rareRelicChanceField = AbstractDungeon.class.getDeclaredField("rareRelicChance");
                    rareRelicChanceField.setAccessible(true);
                    rareRelicChanceField.set(CardCrawlGame.dungeon, 17);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
        }
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
    public void onEnterNewAct() {
        if (combo) {
            activateComboEffect("DemoMod:PrettyGood");
        }
    }
}

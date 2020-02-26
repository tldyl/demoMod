package demoMod.cards.guns;

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
import demoMod.monsters.Decoy;
import demoMod.powers.ControlPower;

public class ThirdPartyController extends AbstractGunCard {
    public static final String ID = DemoMod.makeID("ThirdPartyController");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/thirdPartyController.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;

    public ThirdPartyController() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.capacity = 1;
        this.maxCapacity = 1;
        this.baseDamage = 3;
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        if (m == null) m = AbstractDungeon.getRandomMonster(AbstractDungeon.getMonsters().getMonster(Decoy.ID));
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.NONE));
        if (m.type != AbstractMonster.EnemyType.BOSS) AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new ControlPower(m)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.maxCapacity = 2;
            this.capacity = 2;
            this.isMaxCapacityModified = true;
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;

public class OldTrick extends CustomCard {
    public static final String ID = DemoMod.makeID("OldTrick");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/oldTrick.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;

    public OldTrick() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 4;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(2);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        AbstractCard skillCard = null;
        for (int i = AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1; i >= 0; i--) {
            if (AbstractDungeon.actionManager.cardsPlayedThisCombat.get(i).type == CardType.SKILL) {
                skillCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(i);
                if (AbstractDungeon.actionManager.cardsPlayedThisCombat.size() > 1) {
                    for (int j=i-1;j>=0;j--) {
                        if (skillCard.uuid.equals(AbstractDungeon.actionManager.cardsPlayedThisCombat.get(j).uuid)) {
                            i = j;
                            skillCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(j);
                        } else {
                            if (AbstractDungeon.actionManager.cardsPlayedThisCombat.get(j).type == CardType.SKILL) break;
                        }
                    }
                }
                if (!p.exhaustPile.contains(skillCard)) {
                    break;
                }
            }
        }
        if (skillCard != null) {
            if (p.hand.size() < Settings.MAX_HAND_SIZE) {
                if (p.discardPile.contains(skillCard)) {
                    p.discardPile.moveToHand(skillCard);
                } else if (p.drawPile.contains(skillCard)) {
                    p.drawPile.moveToHand(skillCard);
                }
            } else {
                p.createHandIsFullDialog();
            }
            p.hand.refreshHandLayout();
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

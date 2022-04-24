package demoMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.interfaces.PostReloadSubscriber;
import demoMod.powers.SkilledReloadPower;
import demoMod.relics.CrisisStone;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class SpreadAmmo extends CustomCard {
    public static final String ID = DemoMod.makeID("SpreadAmmo");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/spreadAmmo.png";

    private static final CardStrings cardStrings;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    private static final int COST = 1;

    public SpreadAmmo() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, DemoMod.characterColor, RARITY, TARGET);
        this.exhaust = true;
    }


    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(0);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        List<AbstractCard> guns = new ArrayList<>();
        for (AbstractCard c : p.drawPile.group) {
            if (c instanceof AbstractGunCard) {
                guns.add(c);
            }
        }
        for (AbstractCard c : p.hand.group) {
            if (c instanceof AbstractGunCard) {
                guns.add(c);
            }
        }
        for (AbstractCard c : p.discardPile.group) {
            if (c instanceof AbstractGunCard) {
                guns.add(c);
            }
        }
        for (AbstractCard c : guns) {
            AbstractGunCard gunCard = (AbstractGunCard)c;
            if (gunCard.canFullReload() || gunCard.capacity < gunCard.maxCapacity) {
                gunCard.target = gunCard.defaultTarget;
                if (gunCard.capacity <= 0 && p.hasRelic(CrisisStone.ID)) {
                    ((CrisisStone)p.getRelic(CrisisStone.ID)).onReload(1);
                }
                gunCard.reload();
                gunCard.isReload = false;
                gunCard.superFlash();
            }
        }
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
    }
}

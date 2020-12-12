package demoMod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.monsters.Decoy;

public class GunFriendPower extends AbstractPower {
    private AbstractGunCard gunCard;

    public static final String POWER_ID = DemoMod.makeID("GunFriendPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public GunFriendPower(int amount, AbstractGunCard gunCard) {
        this.gunCard = (AbstractGunCard) gunCard.makeStatEquivalentCopy();
        this.ID = POWER_ID + gunCard.cardID + gunCard.name;
        this.owner = AbstractDungeon.player;
        this.name = NAME;
        this.amount = amount;
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/GunFriend84.png")), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(DemoMod.getResourcePath("powers/GunFriend32.png")), 0, 0, 32, 32);
        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.gunCard.name + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        for (int i=0;i<this.amount;i++) {
            AbstractCard tmp = this.gunCard.makeStatEquivalentCopy();
            AbstractMonster m = AbstractDungeon.getMonsters().getRandomMonster(AbstractDungeon.getMonsters().getMonster(Decoy.ID), true, AbstractDungeon.cardRandomRng);
            if (m != null && !m.isDeadOrEscaped()) {
                tmp.calculateCardDamage(m);
                tmp.use(AbstractDungeon.player, m);
            }
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(DemoMod.makeID("GunFriendPower"));
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

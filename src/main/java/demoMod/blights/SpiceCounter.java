package demoMod.blights;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import demoMod.DemoMod;
import demoMod.cards.Spice;

@SuppressWarnings("FieldCanBeLocal")
public class SpiceCounter extends AbstractBlight implements CustomSavable<Integer> {
    public static final String ID = DemoMod.makeID("SpiceCounter");
    public static final String NAME;
    public static final String DESCRIPTION;
    private static RelicStrings relicStrings;
    private static String IMG_PATH = "DemoImages/relics/spiceCounter.png";


    public SpiceCounter(int amount) {
        super(ID, NAME, DESCRIPTION, "durian.png", true);
        this.counter = amount;
        this.img = new Texture(IMG_PATH);
        this.outlineImg = new Texture(IMG_PATH);
        this.increment = 0;
    }

    @Override
    public void atBattleStart() {
        AbstractPlayer p = AbstractDungeon.player;
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, this.counter)));
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        if (c instanceof Spice) {
            this.counter += c.magicNumber;
            this.description = relicStrings.DESCRIPTIONS[1] + this.counter + relicStrings.DESCRIPTIONS[2];
            this.initializeTips();
        }
    }

    static {
        relicStrings = CardCrawlGame.languagePack.getRelicStrings(ID);
        NAME = relicStrings.NAME;
        DESCRIPTION = relicStrings.DESCRIPTIONS[0];
    }

    @Override
    public Integer onSave() {
        return this.counter;
    }

    @Override
    public void onLoad(Integer i) {
        if (i != null) {
            this.counter = i;
        } else {
            this.counter = 0;
        }
    }
}

package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.Defect;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.DemoMod;
import demoMod.actions.ObtainRelicAction;

public class Junk extends CustomRelic {
    public static final String ID = DemoMod.makeID("Junk");
    public static final String IMG_PATH = "relics/junk.png";

    public Junk() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p instanceof Defect) {
            int t = this.counter + (p.hasRelic(GoldJunk.ID) ? 1 : 0);
            t /= 2;
            if (t > 0) {
                this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, t)));
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        if (AbstractDungeon.player.hasRelic(SerJunkan.ID)) {
            AbstractDungeon.player.getRelic(SerJunkan.ID).onTrigger();
        }
    }

    @Override
    public void onUnequip() {
        if (this.counter > 1) {
            DemoMod.actionsQueue.add(new ObtainRelicAction(this));
            this.counter--;
            if (AbstractDungeon.player.hasRelic(SerJunkan.ID)) AbstractDungeon.player.getRelic(SerJunkan.ID).onTrigger();
        }
    }

    @Override
    public void obtain() {
        int ran = AbstractDungeon.miscRng.random(99);
        if (ran == 99 && !AbstractDungeon.player.hasRelic(GoldJunk.ID)) {
            DemoMod.actionsQueue.add(new ObtainRelicAction(new GoldJunk()));
            return;
        }
        if (AbstractDungeon.player.hasRelic(ID)) {
            AbstractRelic junk = AbstractDungeon.player.getRelic(ID);
            ++junk.counter;
            junk.flash();
            this.hb.hovered = false;
            onEquip();
            return;
        }
        this.counter = 1;
        super.obtain();
    }

    @Override
    public void instantObtain() {
        int ran = AbstractDungeon.miscRng.random(99);
        if (ran == 99 && !AbstractDungeon.player.hasRelic(GoldJunk.ID)) {
            DemoMod.actionsQueue.add(new ObtainRelicAction(new GoldJunk()));
            return;
        }
        if (AbstractDungeon.player.hasRelic(ID)) {
            AbstractRelic junk = AbstractDungeon.player.getRelic(ID);
            ++junk.counter;
            junk.flash();
            junk.playLandingSFX();
            this.hb.hovered = false;
            if (AbstractDungeon.topPanel != null) {
                AbstractDungeon.topPanel.adjustRelicHbs();
            }
            onEquip();
            return;
        }
        this.counter = 1;
        super.instantObtain();
    }
}

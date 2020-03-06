package demoMod.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import demoMod.DemoMod;
import demoMod.characters.HuntressCharacter;
import demoMod.sounds.DemoSoundMaster;

public class FountainOfPurify extends AbstractImageEvent {
    public static final String ID = DemoMod.makeID("FountainOfPurify");
    private static final EventStrings eventStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;

    private int screenNum = 0;

    public FountainOfPurify() {
        super(NAME, DESCRIPTIONS[0], DemoMod.getResourcePath("events/Spring.png"));
        if (AbstractDungeon.player.gold >= HuntressCharacter.curse * 10.0) {
            this.imageEventText.setDialogOption(OPTIONS[0] + (int) (HuntressCharacter.curse * 10.0) + OPTIONS[1]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[4] + (int) (HuntressCharacter.curse * 10.0) + OPTIONS[5], true);
        }
        this.imageEventText.setDialogOption(OPTIONS[2]);
        this.imageEventText.setDialogOption(OPTIONS[3]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        AbstractDungeon.player.loseGold((int)(HuntressCharacter.curse * 10.0));
                        HuntressCharacter.curse = 0;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        DemoSoundMaster.playV("PURIFY", 0.0F);
                        this.screenNum = 1;
                        break;
                    case 1:
                        int golds = AbstractDungeon.miscRng.random(100, 300);
                        AbstractDungeon.player.gainGold(golds);
                        AbstractDungeon.effectList.add(new RainingGoldEffect(golds));
                        HuntressCharacter.curse += 5;
                        DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.screenNum = 1;
                        break;
                    case 2:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.screenNum = 1;
                        break;
                }
                this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                this.imageEventText.clearRemainingOptions();
                break;
            case 1:
                this.openMap();
                break;
            default:
                this.openMap();
                break;
        }
    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString(DemoMod.makeID("FountainOfPurify"));
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
    }
}

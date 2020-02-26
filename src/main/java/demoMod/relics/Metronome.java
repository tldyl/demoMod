package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import demoMod.DemoMod;
import demoMod.sounds.DemoSoundMaster;

public class Metronome extends CustomRelic {
    public static final String ID = "DemoMod:Metronome";
    public static final String IMG_PATH = "relics/metronome.png";
    public static final String OUTLINE_IMG_PATH = "relics/metronomeOutline.png";

    private static int strengthGainedFromThisRelic = 0;
    private static boolean activated = false;

    public Metronome() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)), new Texture(DemoMod.getResourcePath(OUTLINE_IMG_PATH)),
                RelicTier.COMMON, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        strengthGainedFromThisRelic = 0;
        activated = true;
    }

    @Override
    public void onVictory() {
        activated = false;
    }

    @Override
    public void onMonsterDeath(AbstractMonster m) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.cardInUse != null) {
            if (p.cardInUse.type == AbstractCard.CardType.ATTACK) {
                this.flash();
                DemoSoundMaster.playA("RELIC_METRONOME", (float)strengthGainedFromThisRelic / 20F);
                strengthGainedFromThisRelic += 2;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, 2)));
            }
        }
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (activated && info.type == DamageInfo.DamageType.NORMAL && damageAmount > p.currentBlock && strengthGainedFromThisRelic > 0) {
            this.flash();
            DemoSoundMaster.playA("RELIC_METRONOME_BREAK", 0F);
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, -strengthGainedFromThisRelic)));
            strengthGainedFromThisRelic = 0;
        }
        return damageAmount;
    }

}

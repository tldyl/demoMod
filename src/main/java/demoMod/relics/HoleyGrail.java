package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.characters.HuntressCharacter;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.sounds.DemoSoundMaster;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class HoleyGrail extends CustomRelic implements Combo {
    public static final String ID = DemoMod.makeID("HoleyGrail");
    public static final String IMG_PATH = "relics/holeyGrail.png";
    public static final String OUTLINE_IMG_PATH = "relics/holeyGrailOutline.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/holeyGrail.png"));
    private static boolean combos[] = new boolean[]{false, false, false, false, false, false, false};

    private boolean isRemoving = false;

    public HoleyGrail() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)), new Texture(DemoMod.getResourcePath(OUTLINE_IMG_PATH)),
                RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        ComboManager.detectComboInGame();
        DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse += 1;
        }
    }

    @Override
    public void onUnequip() {
        isRemoving = true;
        ComboManager.detectCombo();
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            HuntressCharacter.curse -= 1;
        }
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (info.type == DamageInfo.DamageType.NORMAL && damageAmount > p.currentBlock) {
            this.flash();
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
                AbstractGunCard gunCard = (AbstractGunCard) c;
                if (gunCard.canFullReload() || gunCard.capacity < gunCard.maxCapacity) {
                    gunCard.target = gunCard.defaultTarget;
                    if (p.hasRelic(DemoMod.makeID("CrisisStone"))) {
                        if (gunCard.capacity <= 0) {
                            ((CrisisStone) p.getRelic(DemoMod.makeID("CrisisStone"))).onReload(1);
                        }
                    }
                    gunCard.reload();
                    gunCard.isReload = false;
                    gunCard.superFlash();
                }
            }
        }
        return damageAmount;
    }

    @Override
    public String getItemId() {
        return DemoMod.makeID("HoleyGrail");
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:Kaliber k'pow uboom k'bhang":
                combos[0] = true;
                break;
            case "DemoMod:EmptyVessels":
                combos[1] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:Kaliber k'pow uboom k'bhang":
                combos[0] = false;
                break;
            case "DemoMod:EmptyVessels":
                combos[1] = false;
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
        ComboManager.addCombo(DemoMod.makeID("Kaliber k'pow uboom k'bhang:HoleyGrail"), HoleyGrail.class);
        ComboManager.addCombo(DemoMod.makeID("EmptyVessels"), HoleyGrail.class);
        ComboManager.addCombo(DemoMod.makeID("KalibersGrip"), HoleyGrail.class);
    }
}

package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.potions.BlankPotion;
import demoMod.interfaces.PostEnterNewActSubscriber;

public class WhiteGuonStone extends CustomRelic implements PostEnterNewActSubscriber, Combo {
    public static final String ID = DemoMod.makeID("WhiteGuonStone");
    public static final String IMG_PATH = "relics/whiteGuonStone.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/whiteGuonStone.png"));
    private boolean isRemoving = false;
    private static boolean combo = false;

    public WhiteGuonStone() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.obtainPotion(new BlankPotion());
        ComboManager.detectComboInGame();
    }

    @Override
    public void onUnequip() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public void onPlayerEndTurn() {
        this.flash();
        if (combo) {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, 4));
        } else {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, 2));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        if (room instanceof TreasureRoomBoss) {
            this.flash();
            this.beginLongPulse();
        } else {
            this.stopPulse();
        }
    }

    @Override
    public void onEnterNewAct() {
        AbstractDungeon.player.obtainPotion(new BlankPotion());
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        combo = true;
    }

    @Override
    public void onComboDisabled(String comboId) {
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

    static {
        ComboManager.addCombo(DemoMod.makeID("WhiterGuonStone"), WhiteGuonStone.class);
    }
}

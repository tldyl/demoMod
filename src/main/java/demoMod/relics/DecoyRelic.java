package demoMod.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import demoMod.DemoMod;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.monsters.Decoy;
import demoMod.patches.ChangeTargetPatch;
import demoMod.sounds.DemoSoundMaster;
import demoMod.utils.Point;
import demoMod.utils.Utils;

public class DecoyRelic extends AbstractClickRelic implements Combo {
    public static final String ID = DemoMod.makeID("DecoyRelic");
    public static final String IMG_PATH = "relics/decoyRelic.png";
    private int maxCharge = 4;
    private boolean enabled = false;

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/decoyRelic.png"));

    private boolean isRemoving = false;

    public static boolean combos[] = new boolean[]{false, false, false, false, false, false};

    public DecoyRelic() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.UNCOMMON, LandingSound.FLAT);
        this.counter = maxCharge;
        this.beginPulse();
    }

    @Override
    public void onEquip() {
        ComboManager.detectComboInGame();
        setDescriptionAfterLoading();
    }

    public void onUnequip() {
        isRemoving = true;
        ComboManager.detectCombo();
        setDescriptionAfterLoading();
    }

    private void setDescriptionAfterLoading() {
        this.description = this.DESCRIPTIONS[0];
        if (combos[0]) {
            this.description += this.DESCRIPTIONS[1];
        }
        if (combos[4]) {
            this.description += this.DESCRIPTIONS[2];
        }
        if (combos[1]) {
            this.description += this.DESCRIPTIONS[3];
        }
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void onVictory() {
        if (this.counter < maxCharge) {
            this.counter++;
        } else {
            if (!this.pulse) this.beginPulse();
        }
        ChangeTargetPatch.source.clear();
    }

    @Override
    public void atBattleStart() {
        setDescriptionAfterLoading();
        DemoMod.canSteal = false;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new DecoyRelic();
    }

    @Override
    public void atTurnStart() {
        enabled = true;
    }

    @Override
    public void onPlayerEndTurn() {
        enabled = false;
    }

    @Override
    protected void onRightClick() {
        if (this.counter == maxCharge && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT
                && enabled) {
            this.stopPulse();
            this.flash();
            DemoSoundMaster.playV("ITEM_PLACE_SOMETHING", 0.1F);
            Point center = new Point((double)(AbstractDungeon.player.hb.x - 1200.0F), (double)(AbstractDungeon.player.hb_y + 170.0F));
            Point point = Utils.getCirclePoint(center, -0.2617993877991494D, 400.0D);
            Decoy decoy;
            if (combos[1]) {
                int maxHp = 20;
                for (int i=0;i<AbstractDungeon.getCurrRoom().monsters.monsters.size();i++) {
                    if (!AbstractDungeon.getCurrRoom().monsters.monsters.get(i).isDeadOrEscaped()) {
                        maxHp = AbstractDungeon.getCurrRoom().monsters.monsters.get(i).maxHealth;
                        break;
                    }
                }
                if (maxHp > 100) maxHp = 100;
                decoy = new Decoy((float)point.x, (float)point.y, maxHp);
            } else {
                decoy = new Decoy((float)point.x, (float)point.y);
            }
            AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(decoy, false));
            ChangeTargetPatch.source.clear();
            ChangeTargetPatch.source.addAll(AbstractDungeon.getCurrRoom().monsters.monsters);
            ChangeTargetPatch.target = decoy;
            this.counter = 0;
        }
        if (this.counter == maxCharge && AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
            this.stopPulse();
            this.flash();
            DemoSoundMaster.playV("ITEM_PLACE_SOMETHING", 0.1F);
            DemoMod.canSteal = true;
            this.counter = 0;
        }
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:DeadlyDistraction":
                combos[0] = true;
                break;
            case "DemoMod:DecoyOctorok":
                combos[1] = true;
                break;
            case "DemoMod:FrostyDistraction":
                combos[2] = true;
                break;
            case "DemoMod:HardWood":
                combos[3] = true;
                break;
            case "DemoMod:KageBunshin":
                combos[4] = true;
                break;
            case "DemoMod:Kinjutsu":
                combos[5] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:DeadlyDistraction":
                combos[0] = false;
                break;
            case "DemoMod:DecoyOctorok":
                combos[1] = false;
                break;
            case "DemoMod:FrostyDistraction":
                combos[2] = false;
                break;
            case "DemoMod:HardWood":
                combos[3] = false;
                break;
            case "DemoMod:KageBunshin":
                combos[4] = false;
                break;
            case "DemoMod:Kinjutsu":
                combos[5] = false;
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
        ComboManager.addCombo(DemoMod.makeID("DeadlyDistraction"), DecoyRelic.class);
        ComboManager.addCombo(DemoMod.makeID("DecoyOctorok"), DecoyRelic.class);
        ComboManager.addCombo(DemoMod.makeID("FrostyDistraction"), DecoyRelic.class);
        ComboManager.addCombo(DemoMod.makeID("HardWood"), DecoyRelic.class);
        ComboManager.addCombo(DemoMod.makeID("KageBunshin"), DecoyRelic.class);
        ComboManager.addCombo(DemoMod.makeID("Kinjutsu"), DecoyRelic.class);
    }
}

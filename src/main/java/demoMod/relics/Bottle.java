package demoMod.relics;

import basemod.BaseMod;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.powers.RegenPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import demoMod.DemoMod;
import demoMod.actions.BottleAction;
import demoMod.actions.EmptyVesselsAction;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;

@SuppressWarnings({"WeakerAccess", "Duplicates"})
public class Bottle extends AbstractClickRelic implements RenderSubscriber,
                                                          PostUpdateSubscriber,
                                                          Combo {
    private boolean isHidden = true;
    private AbstractMonster hoveredCreature;
    private Vector2 controlPoint;
    private float arrowScaleTimer;
    private float arrowScale;
    private Vector2[] points = new Vector2[20];
    private boolean activated = false;

    public static final String ID = DemoMod.makeID("Bottle");
    public static final String IMG_PATH = "relics/bottle.png";
    public static final Texture EMPTY_VESSELS_IMG = new Texture(DemoMod.getResourcePath("relics/bottle_empty_vessels.png"));
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/bottle.png"));

    private static boolean combos[] = new boolean[] {false, false, false};
    private int emptyVessels = 0;
    private boolean isRemoving = false;

    public Bottle() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)), RelicTier.UNCOMMON, LandingSound.SOLID);
        this.counter = 0;
        BaseMod.subscribe(this);
        for(int i = 0; i < this.points.length; ++i) {
            this.points[i] = new Vector2();
        }
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.potionSlots += 1;
        AbstractDungeon.player.potions.add(new PotionSlot(AbstractDungeon.player.potionSlots - 1));
        ComboManager.detectComboInGame();
        for (boolean combo : combos) {
            if (combo) {
                setDescriptionAfterLoading();
                break;
            }
        }
    }

    @Override
    public void onUnequip() {
        isRemoving = true;
        AbstractDungeon.player.potionSlots -= 1;
        AbstractDungeon.player.potions.remove(AbstractDungeon.player.potions.get(AbstractDungeon.player.potions.size() - 1));
        ComboManager.detectCombo();
        BaseMod.unsubscribe(this);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    private void setDescriptionAfterLoading() {
        this.description = this.DESCRIPTIONS[0] + this.DESCRIPTIONS[1] + this.counter + this.DESCRIPTIONS[2];
        for (int i=0;i<combos.length;i++) {
            if (combos[i]) {
                this.description += this.DESCRIPTIONS[i + 3];
            }
        }
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (info.type == DamageInfo.DamageType.NORMAL && combos[0] && emptyVessels != -1
                && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT
                && damageAmount > p.currentBlock) {
            emptyVessels++;
            this.img = EMPTY_VESSELS_IMG;
        }
        return damageAmount;
    }

    @Override
    public void receivePostUpdate() {
        if (!this.isHidden) {
            this.updateTargetMode();
        }
    }

    private void close() {
        this.isHidden = true;
    }

    private void updateTargetMode() {
        if (AbstractDungeon.isScreenUp || (float)InputHelper.mY > (float)Settings.HEIGHT - 80.0F * Settings.scale || AbstractDungeon.player.hoveredCard != null || (float)InputHelper.mY < 140.0F * Settings.scale) {
            GameCursor.hidden = false;
            this.close();
        }
        this.hoveredCreature = null;
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (monster.hb.hovered && !monster.isDying) {
                this.hoveredCreature = monster;
                break;
            }
        }
        if (InputHelper.justClickedLeft) {
            InputHelper.justClickedLeft = false;
            if (this.hoveredCreature != null) {
                this.takeDamage(this.hoveredCreature);
            }

            GameCursor.hidden = false;
            this.close();
        }
    }

    private void takeDamage(AbstractMonster m) {
        if (m == null) return;
        AbstractPlayer p = AbstractDungeon.player;
        activated = false;
        if (p.hasPower("Surrounded")) {
            p.flipHorizontal = m.drawX < p.drawX;
        }
        AbstractDungeon.actionManager.addToBottom(new BottleAction(m, new DamageInfo(m, 3, DamageInfo.DamageType.THORNS)));
        setDescriptionAfterLoading();
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        this.render(sb);
    }

    public void render(SpriteBatch sb) {
        if (!this.isHidden) {
            this.renderTargetingUi(sb);
            if (this.hoveredCreature != null) {
                this.hoveredCreature.renderReticle(sb);
            }
        }
    }

    public void renderTargetingUi(SpriteBatch sb) {
        float x = (float)InputHelper.mX;
        float y = (float)InputHelper.mY;
        this.controlPoint = new Vector2(this.currentX - (x - this.currentX) / 4.0F, this.currentY + (y - this.currentY - 40.0F * Settings.scale) / 2.0F);
        if (this.hoveredCreature == null) {
            this.arrowScale = Settings.scale;
            this.arrowScaleTimer = 0.0F;
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, 1.0F));
        } else {
            this.arrowScaleTimer += Gdx.graphics.getDeltaTime();
            if (this.arrowScaleTimer > 1.0F) {
                this.arrowScaleTimer = 1.0F;
            }

            this.arrowScale = Interpolation.elasticOut.apply(Settings.scale, Settings.scale * 1.2F, this.arrowScaleTimer);
            sb.setColor(new Color(1.0F, 0.2F, 0.3F, 1.0F));
        }

        Vector2 tmp = new Vector2(this.controlPoint.x - x, this.controlPoint.y - y);
        tmp.nor();
        this.drawCurvedLine(sb, new Vector2(this.currentX, this.currentY), new Vector2(x, y), this.controlPoint);
        sb.draw(ImageMaster.TARGET_UI_ARROW, x - 128.0F, y - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, this.arrowScale, this.arrowScale, tmp.angle() + 90.0F, 0, 0, 256, 256, false, false);
    }

    private void drawCurvedLine(SpriteBatch sb, Vector2 start, Vector2 end, Vector2 control) {
        float radius = 7.0F * Settings.scale;

        for(int i = 0; i < this.points.length - 1; ++i) {
            this.points[i] = Bezier.quadratic(this.points[i], (float)i / 20.0F, start, control, end, new Vector2());
            radius += 0.4F * Settings.scale;
            float angle;
            Vector2 tmp;
            if (i != 0) {
                tmp = new Vector2(this.points[i - 1].x - this.points[i].x, this.points[i - 1].y - this.points[i].y);
                angle = tmp.nor().angle() + 90.0F;
            } else {
                tmp = new Vector2(this.controlPoint.x - this.points[i].x, this.controlPoint.y - this.points[i].y);
                angle = tmp.nor().angle() + 270.0F;
            }

            sb.draw(ImageMaster.TARGET_UI_CIRCLE, this.points[i].x - 64.0F, this.points[i].y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, radius / 18.0F, radius / 18.0F, angle, 0, 0, 128, 128, false, false);
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Bottle();
    }

    @Override
    protected void onRightClick() {
        if (activated) this.isHidden = false;
        InputHelper.justClickedRight = false;
    }

    @Override
    public void atBattleStart() {
        activated = true;
        AbstractPlayer p = AbstractDungeon.player;
        if (this.counter > 0) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new RegenPower(p, this.counter)));
            AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.counter = 0;
        }
        setDescriptionAfterLoading();
    }

    @Override
    public void atTurnStartPostDraw() {
        if (emptyVessels > 0) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new EmptyVesselsAction(30));
            this.img = new Texture(DemoMod.getResourcePath(IMG_PATH));
            emptyVessels = -1;
        }
        activated = true;
    }

    @Override
    public void onPlayerEndTurn() {
        activated = false;
    }

    @Override
    public void onVictory() {
        activated = false;
        emptyVessels = 0;
    }

    @Override
    public String getItemId() {
        return DemoMod.makeID("Bottle");
    }

    @Override
    public void onComboActivated(String comboId) {
        switch (comboId) {
            case "DemoMod:EmptyVessels":
                combos[0] = true;
                break;
            case "DemoMod:ClearerGuonStone":
                combos[1] = true;
                break;
            case "DemoMod:MakPak":
                combos[2] = true;
                break;
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        switch (comboId) {
            case "DemoMod:EmptyVessels":
                combos[0] = false;
                break;
            case "DemoMod:ClearerGuonStone":
                combos[1] = false;
                break;
            case "DemoMod:MakPak":
                combos[2] = false;
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
        ComboManager.addCombo(DemoMod.makeID("EmptyVessels"), Bottle.class);
        ComboManager.addCombo(DemoMod.makeID("ClearerGuonStone:Bottle"), Bottle.class);
        ComboManager.addCombo(DemoMod.makeID("MakPak:Bottle"), Bottle.class);
    }
}

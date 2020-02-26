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
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.DemoMod;
import demoMod.actions.ResourcefulSackAction;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;

@SuppressWarnings({"WeakerAccess", "Duplicates"})
public class ResourcefulSack extends AbstractClickRelic implements RenderSubscriber,
        PostUpdateSubscriber,
        Combo {

    public static final String ID = DemoMod.makeID("ResourcefulSack");
    public static final String IMG_PATH = "relics/resourcefulSack.png";
    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/relics/resourcefulSack.png"));

    private boolean isHidden = true;
    private AbstractMonster hoveredCreature;
    private Vector2 controlPoint;
    private float arrowScaleTimer;
    private Vector2[] points = new Vector2[20];
    private boolean activated = false;
    private boolean isRemoving = false;

    private Texture originCorpseImg;

    public static boolean isComboActivated = false;

    public ResourcefulSack() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)), RelicTier.RARE, LandingSound.MAGICAL);
        this.counter = 0;
        BaseMod.subscribe(this);
        for(int i = 0; i < this.points.length; ++i) {
            this.points[i] = new Vector2();
        }
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    private void setDescriptionAfterLoading() {
        this.description = this.DESCRIPTIONS[0] + this.DESCRIPTIONS[1] + this.counter + this.DESCRIPTIONS[2];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
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
        activated = false;
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new ResourcefulSackAction(m, this.counter));
        this.counter = 0;
        setDescriptionAfterLoading();
    }

    @Override
    public AbstractRelic makeCopy() {
        return new ResourcefulSack();
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (this.counter < 30) this.counter++;
        setDescriptionAfterLoading();
        return damageAmount;
    }

    @Override
    protected void onRightClick() {
        if (activated) this.isHidden = false;
        InputHelper.justClickedRight = false;
    }

    @Override
    public void onEquip() {
        ComboManager.detectComboInGame();
    }

    @Override
    public void onUnequip() {
        BaseMod.unsubscribe(this);
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public void atBattleStart() {
        setDescriptionAfterLoading();
    }

    @Override
    public void atTurnStart() {
        if (this.counter > 0) activated = true;
    }

    @Override
    public void onPlayerEndTurn() {
        activated = false;
    }

    @Override
    public void onVictory() {
        activated = false;
        this.description = this.DESCRIPTIONS[0];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void receivePostUpdate() {
        if (!this.isHidden) {
            this.updateTargetMode();
        }
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
        float arrowScale;
        if (this.hoveredCreature == null) {
            arrowScale = Settings.scale;
            this.arrowScaleTimer = 0.0F;
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, 1.0F));
        } else {
            this.arrowScaleTimer += Gdx.graphics.getDeltaTime();
            if (this.arrowScaleTimer > 1.0F) {
                this.arrowScaleTimer = 1.0F;
            }

            arrowScale = Interpolation.elasticOut.apply(Settings.scale, Settings.scale * 1.2F, this.arrowScaleTimer);
            sb.setColor(new Color(1.0F, 0.2F, 0.3F, 1.0F));
        }

        Vector2 tmp = new Vector2(this.controlPoint.x - x, this.controlPoint.y - y);
        tmp.nor();
        this.drawCurvedLine(sb, new Vector2(this.currentX, this.currentY), new Vector2(x, y), this.controlPoint);
        sb.draw(ImageMaster.TARGET_UI_ARROW, x - 128.0F, y - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, arrowScale, arrowScale, tmp.angle() + 90.0F, 0, 0, 256, 256, false, false);
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
    public void receiveRender(SpriteBatch sb) {
        this.render(sb);
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        this.originCorpseImg = AbstractDungeon.player.corpseImg;
        AbstractDungeon.player.corpseImg = new Texture(DemoMod.getResourcePath("char/corpse_rat.png"));
        isComboActivated = true;
    }

    @Override
    public void onComboDisabled(String comboId) {
        if (this.originCorpseImg != null) AbstractDungeon.player.corpseImg = this.originCorpseImg;
        isComboActivated = false;
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
        ComboManager.addCombo(DemoMod.makeID("ResourcefulIndeed"), ResourcefulSack.class);
    }
}

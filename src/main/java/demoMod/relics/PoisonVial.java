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
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.combat.PotionBounceEffect;
import demoMod.DemoMod;

@SuppressWarnings("Duplicates")
public class PoisonVial extends AbstractClickRelic implements RenderSubscriber,
                                                              PostUpdateSubscriber {
    public static final String ID = DemoMod.makeID("PoisonVial");
    public static final String IMG_PATH = "relics/poisonVial.png";

    private boolean isHidden = true;
    private AbstractMonster hoveredCreature;
    private Vector2 controlPoint;
    private float arrowScaleTimer;
    private float arrowScale;
    private Vector2[] points = new Vector2[20];

    private boolean enabled = false;

    public PoisonVial() {
        super(ID, new Texture(DemoMod.getResourcePath(IMG_PATH)),
                RelicTier.COMMON, LandingSound.SOLID);
        this.counter = 0;
        for(int i = 0; i < this.points.length; ++i) {
            this.points[i] = new Vector2();
        }
        BaseMod.subscribe(this);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onUnequip() {
        BaseMod.unsubscribe(this);
    }

    @Override
    public void onVictory() {
        enabled = false;
        counter++;
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        this.counter++;
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
    public AbstractRelic makeCopy() {
        return new PoisonVial();
    }

    @Override
    protected void onRightClick() {
        if (enabled && this.counter > 0) {
            this.isHidden = false;
            InputHelper.justClickedRight = false;
        }
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
                this.applyPoison(this.hoveredCreature);
            }

            GameCursor.hidden = false;
            this.close();
        }

    }

    private void applyPoison(AbstractMonster m) {
        AbstractPlayer p = AbstractDungeon.player;
        AbstractDungeon.actionManager.addToBottom(new VFXAction(new PotionBounceEffect(p.hb.cX, p.hb.cY, m.hb.cX, m.hb.cY), 0.3F));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new PoisonPower(m, p, this.counter)));
        this.counter = 0;
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
}

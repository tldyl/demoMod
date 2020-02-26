package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;
import demoMod.utils.Point;
import demoMod.utils.Utils;

public class BulletSprayEffect extends AbstractGameEffect {

    private boolean showShrink = false;
    private boolean isIntro = false;
    private boolean isOutro = false;
    private Point points[] = new Point[5];
    private Point cent;
    private int commonCtr = 0;
    private int outroCtr = 0;
    private int shrinkStartIdx = 0;
    private double radius;

    private static TextureRegion[] sprays;
    private static TextureRegion[] shrinks;
    private static TextureRegion[][] outros;
    private static int[] outroAnim;

    public AbstractMonster owner;

    public BulletSprayEffect() {
        this.duration = 0.5F;
        cent = new Point(Settings.WIDTH * 0.5, Settings.HEIGHT);
        for (int i=0;i<points.length;i++) {
            points[i] = Utils.getCirclePoint(cent, 3.14159265 * (1 + (1.0 + i) / 6.0), 0.675 * Settings.HEIGHT);
        }
    }

    public void shrink() {
        this.showShrink = true;
        this.shrinkStartIdx = DemoMod.frameRateRemap(commonCtr, 30, shrinks.length - 1);
    }

    public void fadeIn() {
        this.radius = 1.675 * Settings.HEIGHT;
        this.isDone = false;
        this.isIntro = true;
    }

    public void fadeOut() {
        this.isOutro = true;
        for (int i = 0; i< outroAnim.length; i++) {
            outroAnim[i] = MathUtils.random(1);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        int idx;
        sb.setColor(1, 1, 1, 1);
        if (!isOutro) {
            idx = DemoMod.frameRateRemap(commonCtr, 30, sprays.length);
            if (idx >= sprays.length) {
                idx = 0;
                commonCtr = 0;
            }
            for (Point point : points) {
                sb.draw(sprays[idx], (float)point.x - 512.0F, (float)point.y - 512.0F, 512.0F, 512.0F, 1024.0F, 1024.0F, Settings.scale, Settings.scale, 0.0F);
            }
        } else {
            idx = DemoMod.frameRateRemap(outroCtr, 30, outros[0].length);
            if (idx >= outros[0].length) {
                outroCtr = 0;
                isOutro = false;
                this.radius = 1.675 * Settings.HEIGHT;
                this.isDone = true;
                return;
            }
            for (int i = 0; i< outroAnim.length; i++) {
                sb.draw(outros[outroAnim[i]][idx], (float)points[i].x - 512.0F, (float)points[i].y - 512.0F, 512.0F, 512.0F, 1024.0F, 1024.0F, Settings.scale, Settings.scale, 0.0F);
            }
        }
        if (showShrink) {
            sb.draw(shrinks[idx], (float)points[2].x - 512.0F, (float)points[2].y - 512.0F, 512.0F, 512.0F, 1024.0F, 1024.0F, Settings.scale, Settings.scale, 0.0F);
            if ((idx + 1) % shrinks.length == this.shrinkStartIdx) {
                showShrink = false;
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, new DamageInfo(this.owner, 6), AbstractGameAction.AttackEffect.FIRE));
            }
        }
    }

    @Override
    public void update() {
        if (this.isDone) return;
        if (isOutro) {
            outroCtr++;
        } else {
            commonCtr++;
        }
        if (isIntro) {
            this.radius -= Settings.HEIGHT * Gdx.graphics.getDeltaTime() * 2.0F;
            for (Point point : points) {
                Utils.changeDist(this.cent, point, this.radius);
            }
            if (this.radius <= Settings.HEIGHT * 0.675F) {
                isIntro = false;
            }
        }
    }

    @Override
    public void dispose() {

    }

    public static void init() {
        sprays = new TextureRegion[14];
        shrinks = new TextureRegion[14];
        outros = new TextureRegion[2][30];
        for (int i=100000;i<=100013;i++) {
            String n = Integer.toString(i).substring(1);
            sprays[i - 100000] = new TextureRegion(new Texture("DemoImages/effects/bulletSprayEffect/bulletSprayEffect_" + n + ".png")) ;
            shrinks[i - 100000] = new TextureRegion(new Texture("DemoImages/effects/bulletSprayEffect/shrink_" + n + ".png"));
        }
        for (int i=100000;i<=100029;i++) {
            String n = Integer.toString(i).substring(1);
            outros[0][i - 100000] = new TextureRegion(new Texture("DemoImages/effects/bulletSprayEffect/outro1_" + n + ".png"));
            outros[1][i - 100000] = new TextureRegion(new Texture("DemoImages/effects/bulletSprayEffect/outro2_" + n + ".png"));
        }
        outroAnim = new int[]{0, 0, 0, 0, 0};
    }
}

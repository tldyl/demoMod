package demoMod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;
import demoMod.utils.Point;
import demoMod.utils.Utils;

public class BulletWaveEffect extends AbstractGameEffect {

    private TextureRegion bullet = new TextureRegion(new Texture(DemoMod.getResourcePath("effects/bulletSprayEffect/bullet.png")));
    private Point[] internalCircle = new Point[24];
    private Point[] externalCircle = new Point[26];
    private Point cent;

    @Deprecated
    public BulletWaveEffect(float x, float y) { //圆变椭圆
        this.duration = 0.7F;
        this.startingDuration = this.duration;
        cent = new Point(x, y);
        //画圆
        for (int i=0;i<internalCircle.length;i++) {
            internalCircle[i] = Utils.getCirclePoint(cent, 3.14159265 * (1 + (1.0 + i) / 25.0), -0.325 * Settings.HEIGHT + y);
        }
        for (int i=0;i<externalCircle.length;i++) {
            externalCircle[i] = Utils.getCirclePoint(cent, 3.14159265 * (1 + (1.0 + i) / 27.0), -0.325 * Settings.HEIGHT + y);
        }
        //变椭圆
        for (Point point : internalCircle) {
            point.y = (point.y + cent.y) / 2.0;
        }
        for (Point point : externalCircle) {
            point.y = (point.y + cent.y) / 2.0;
        }
        for (Point point : externalCircle) {
            Utils.incDist(cent, point,  48.0 * Settings.scale);
        }
    }

    public BulletWaveEffect(double x, double y, double semiFocalDistance) { //直接画椭圆
        this.duration = 0.7F;
        this.startingDuration = this.duration;
        cent = new Point(x, y);
        Point f1 = new Point(cent.x - semiFocalDistance, cent.y);
        Point f2 = new Point(cent.x + semiFocalDistance, cent.y + 0.05);
        double r1 = Settings.WIDTH * 0.1;
        double r2 = r1 + 2.0 * semiFocalDistance;
        double delta = 2.0 * semiFocalDistance / 26;
        double delta2 = semiFocalDistance / 26;
        double delta3 = delta2 / 13;
        for (int i=0;i<internalCircle.length;i++) {
            r1 += delta + delta2;
            r2 -= delta + delta2;
            delta2 -= delta3 * Math.cos(6.2831 * ((i + 1) / 24.0));
            internalCircle[i] = Utils.getIntersectionOfTwoCircles(f1, r1, f2, r2)[1];
        }
        r1 = Settings.WIDTH * 0.125;
        r2 = r1 + 2.0 * semiFocalDistance;
        delta = 2.0 * semiFocalDistance / 28;
        delta2 = semiFocalDistance / 28;
        delta3 = delta2 / 14;
        for (int i=0;i<externalCircle.length;i++) {
            r1 += delta + delta2;
            r2 -= delta + delta2;
            delta2 -= delta3 * Math.cos(6.2831 * ((i + 1) / 26.0));
            externalCircle[i] = Utils.getIntersectionOfTwoCircles(f1, r1, f2, r2)[1];
        }
    }

    @Override
    public void update() {
        float dist = Settings.WIDTH * 0.4F * Gdx.graphics.getDeltaTime() / this.startingDuration;
        for (Point point : internalCircle) {
            Utils.incDist(cent, point, dist);
        }
        for (Point point : externalCircle) {
            Utils.incDist(cent, point, dist);
        }
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 0) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, 1);
        for (Point point : internalCircle) {
            sb.draw(bullet, (float)point.x - 128.0F, (float)point.y - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, Settings.scale, Settings.scale, 0.0F);
        }
        for (Point point : externalCircle) {
            sb.draw(bullet, (float)point.x - 128.0F, (float)point.y - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, Settings.scale, Settings.scale, 0.0F);
        }
    }

    @Override
    public void dispose() {

    }
}

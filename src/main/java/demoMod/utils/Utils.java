package demoMod.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class Utils {
    public static Point getCirclePoint(Point point, double angle, double r) {
        double x = point.x + r * Math.cos(angle);
        double y = point.y + r * Math.sin(angle);
        return new Point(x, y);
    }

    public static void changeDist(Point centPoint, Point targetPoint, double newDist) {
        double x = centPoint.x - targetPoint.x;
        double y = centPoint.y - targetPoint.y;
        double dist = Math.sqrt(x * x + y * y);
        double scale = newDist / dist;
        targetPoint.x = centPoint.x - (x * scale);
        targetPoint.y = centPoint.y - (y * scale);
    }

    public static void incDist(Point centPoint, Point targetPoint, double deltaDist) {
        double x = centPoint.x - targetPoint.x;
        double y = centPoint.y - targetPoint.y;
        double dist = Math.sqrt(x * x + y * y);
        double scale = (dist + deltaDist) / dist;
        targetPoint.x = centPoint.x - (x * scale);
        targetPoint.y = centPoint.y - (y * scale);
    }

    public static Point[] getIntersectionOfTwoCircles(Point o1, double r1, Point o2, double r2) { //求两圆交点坐标
        Point[] ret = new Point[2];
        ret[0] = new Point(0, 0);
        ret[1] = new Point(0, 0);
        double x = Math.abs(o2.x - o1.x);
        double y = Math.abs(o2.y - o1.y);
        double dist = Math.sqrt(x * x + y * y);
        if (dist > r1 + r2) return ret; //外离
        if (Math.min(r1, r2) + dist < Math.max(r1, r2)) return ret; //内含
        double AE = (r1 * r1 - r2 * r2 + dist * dist) / (2.0 * dist);
        double x0 = o1.x + (AE / dist) * (o2.x - o1.x);
        double y0 = o1.y + (AE / dist) * (o2.y - o1.y);
        double k = (o1.x - o2.x) / (o2.y - o1.y);
        double CE = r1 * r1 - (x0 - o1.x) * (x0 - o1.x) - (y0 - o1.y) * (y0 - o1.y);
        CE = Math.sqrt(CE);
        double xc = x0 - CE / Math.sqrt(1.0 + k * k);
        double yc = y0 + k * (xc - x0);
        double xd = x0 + CE / Math.sqrt(1.0 + k * k);
        double yd = y0 + k * (xd - x0);
        ret[0].x = xc;
        ret[0].y = yc;
        ret[1].x = xd;
        ret[1].y = yd;
        return ret;
    }

    public static float calcMaxHpMultiplier() {
        return (float)AbstractDungeon.actNum / 6.0F + 19.0F / 15.0F;
    }

    public static float radToDeg(float rad) {
        return (rad / 3.14159265F) * 180.0F;
    }

    public static AbstractRelic returnRandomRelicTierRelic() {
        AbstractRelic.RelicTier tier;
        if (AbstractDungeon.relicRng.random(1.0F) < 0.5F) {
            tier = AbstractRelic.RelicTier.COMMON;
        } else if (AbstractDungeon.relicRng.random(1.0F) < 0.83F) {
            tier = AbstractRelic.RelicTier.UNCOMMON;
        } else {
            tier = AbstractRelic.RelicTier.RARE;
        }
        return AbstractDungeon.returnRandomRelic(tier);
    }

    public static void spawnMonsterWith4Slots(AbstractMonster owner, AbstractMonster monster, int index, boolean isMinion) {
        switch (index) {
            case 0:
                monster.drawX = owner.drawX - 80.0F * Settings.scale;
                monster.drawY = owner.drawY + 60.0F;
                break;
            case 1:
                monster.drawX = owner.drawX + 80.0F * Settings.scale;
                monster.drawY = owner.drawY + 60.0F;
                break;
            case 2:
                monster.drawX = owner.drawX - 80.0F * Settings.scale;
                monster.drawY = owner.drawY + 180.0F * Settings.scale;
                break;
            case 3:
                monster.drawX = owner.drawX + 80.0F * Settings.scale;
                monster.drawY = owner.drawY + 180.0F * Settings.scale;
                break;
            default:
                return;
        }
        monster.hb.move(monster.drawX + monster.hb_x + monster.animX, monster.drawY + monster.hb_y + monster.hb_h / 2.0F);
        monster.healthHb.move(monster.hb.cX, monster.hb.cY - monster.hb_h / 2.0F - monster.healthHb.height / 2.0F);
        monster.refreshIntentHbLocation();
        AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(monster, isMinion));
        monster.usePreBattleAction();
    }

    public static void drawTextureOnPixmap(Pixmap pixmap, Texture texture, int x, int y) {
        if (texture == null || pixmap == null) return;
        if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
        Pixmap src = texture.getTextureData().consumePixmap();
        pixmap.drawPixmap(src, x, y);
        if (texture.getTextureData() instanceof FileTextureData) {
            src.dispose();
        }
    }

    public static void drawTextureOnPixmap(Pixmap pixmap, Texture texture, int x, int y, Color color) {
        if (texture == null || pixmap == null || color == null) return;
        if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
        Pixmap src = texture.getTextureData().consumePixmap();
        if (src.getFormat() != Pixmap.Format.RGBA8888) {
            Pixmap tmp = new Pixmap(src.getWidth(), src.getHeight(), Pixmap.Format.RGBA8888);
            tmp.drawPixmap(src, 0, 0);
            if (texture.getTextureData() instanceof FileTextureData) {
                src.dispose();
            }
            src = tmp;
        }
        for (int i=0;i<src.getWidth();i++) {
            for (int j=0;j<src.getHeight();j++) {
                Color pixel = new Color(src.getPixel(i, j));
                pixel.r *= color.r;
                pixel.g *= color.g;
                pixel.b *= color.b;
                pixel.a *= color.a;
                Color originPixel = new Color(pixmap.getPixel(x + i, y + j));
                float r = originPixel.r + pixel.r;
                float g = originPixel.g + pixel.g;
                float b = originPixel.b + pixel.b;
                float a = originPixel.a + pixel.a;
                if (r > 1) r = 1;
                if (g > 1) g = 1;
                if (b > 1) b = 1;
                if (a > 1) a = 1;
                int colorInt = Color.rgba8888(r, g, b, a);
                pixmap.drawPixel(x + i, y + j, colorInt);
            }
        }
        if (texture.getTextureData() instanceof FileTextureData) {
            src.dispose();
        }
    }

    public static void savePixmapToFile(Pixmap pixmap, String filePath) {
        FileHandle fileHandle = Gdx.files.external(filePath);
        PixmapIO.writePNG(fileHandle, pixmap);
        System.out.println("Save success. File location:" + fileHandle.file().getAbsolutePath());
    }
}

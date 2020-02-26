package demoMod.utils;

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
}

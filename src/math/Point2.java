package math;

public class Point2 {
    public double x, y;

    public Point2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2() {
        this.x = 0.0;
        this.y = 0.0;
    }

    public Point2(Point2 p) {
        this.x = p.x;
        this.y = p.y;
    }

    public Point2 add(Point2 p) {
        return new Point2(x + p.x, y + p.y);
    }

    public Point2 subtract(Point2 p) {
        return new Point2(x - p.x, y - p.y);
    }
}

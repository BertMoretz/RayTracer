package math;

public class Point3 {
    public double x, y, z;

    public Point3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public Point3(Point3 p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }

    public Point3 add(Point3 p) {
        return new Point3(x + p.x, y + p.y, z + p.z);
    }

    public Point3 subtract(Point3 p) {
        return new Point3(x - p.x, y - p.y, z - p.z);
    }

    public double dot(Point3 second) {
        return this.x*second.x + this.y*second.y + this.z*second.z;
    }

    public double dot(Vec3 second) {
        return this.x*second.x + this.y*second.y + this.z*second.z;
    }

}

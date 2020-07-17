package math;

public class Vec3 {
    final double PI = 3.14159265359;
    public double x;
    public double y;
    public double z;

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getLength() {
        return Math.sqrt(getLengthSquare());
    }

    public double getLengthSquare() {
        return x*x + y*y + z*z;
    }

    public Vec3 subtract(Vec3 v) {
        Vec3 temp = new Vec3(x - v.x, y - v.y, z - v.z);
        return temp;
    }

    public Vec3 add(Vec3 v) {
        Vec3 temp = new Vec3(x + v.x, y + v.y, z + v.z);
        return temp;
    }

    public Vec3 normalize() {
        double nor2 = getLengthSquare();
        if (nor2 > 0) {
            double finalNorm = 1 / getLength();
            this.x *= finalNorm;
            this.y *= finalNorm;
            this.z *= finalNorm;
        }
        return this;
    }

    public double dot(Vec3 second) {
        return this.x*second.x + this.y*second.y + this.z*second.z;
    }

    public Vec3 multiply(double second) {
        return new Vec3(this.x * second, this.y * second, this.z * second);
    }

    public Vec3 divide(double second) {
        return new Vec3(this.x / second, this.y / second, this.z / second);
    }

    public Vec3 cross(Vec3 B) {
        return new Vec3(y*B.z - z*B.y, z*B.x - x*B.z, x*B.y - y*B.x);
    }

    public Vec3 cross(double Bx, double By, double Bz) {
        return new Vec3(y*Bz - z*By, z*Bx - x*Bz, x*By - y*Bx);
    }
}

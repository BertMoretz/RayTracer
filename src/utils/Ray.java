package utils;
import math.*;

public class Ray {
    public Point3 origin;
    public Vec3 direction;

    public Ray(Point3 origin, Vec3 direction) {
        this.origin = origin;
        this.direction = direction;
    }
}

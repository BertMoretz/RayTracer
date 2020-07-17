package math;

public class Sphere extends Surface {
    public Vec3 center;
    public double radius1, radiusSquare;
    public Color surfaceColor;
    public double transparency, reflection, refraction;
    public int shininess;
    public double ka, kd, ks;

    public Sphere(Vec3 center, double radius1, Color surfaceColor, double transparency, double reflection,
                  double refraction, int shininess, double ka, double kd, double ks) {
        this.center = center;
        this.radius1 = radius1;
        this.radiusSquare = radius1*radius1;
        this.surfaceColor = surfaceColor;
        this.transparency = transparency;
        this.reflection = reflection;
        this.refraction = refraction;
        this.shininess = shininess;
        this.ka = ka;
        this.kd = kd;
        this.ks = ks;
    }

    public Sphere (Sphere s) {
        this.center = s.center;
        this.radius1 = s.radius1;
        this.radiusSquare = s.radius1*s.radius1;
        this.surfaceColor = s.surfaceColor;
        this.transparency = s.transparency;
        this.reflection = s.reflection;
        this.refraction = s.refraction;
        this.shininess = s.shininess;
        this.ka = s.ka;
        this.kd = s.kd;
        this.ks = s.ks;
    }

    public double intersect(Vec3 rayOrig, Vec3 rayDir) {
        double a = rayDir.dot(rayDir);
        double b = 2 * (rayOrig.subtract(center).dot(rayDir));
        double c = rayOrig.subtract(center).dot(rayOrig.subtract(center)) - radiusSquare;

        double disc = b*b - 4*a*c;

        if (disc < 0.0) {
            return 0.0;
        } else {
            double t = (- b - Math.sqrt(disc)) / (2 * a);

            if (t > 10E-9) {
                return t;
            } else {
                return 0.0;
            }
        }
    }
}

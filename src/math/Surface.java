package math;

import java.util.ArrayList;

public class Surface {
    public double transparency, reflection, refraction;
    public Vec3 center;
    public int shininess;
    public ArrayList<Vec3> normals;
    public ArrayList<Vec3> vertices;
    public double ka, kd, ks;
    public Color surfaceColor;

}

package math;

import java.util.ArrayList;

public class Mesh extends Surface {
    public double transparency, reflection, refraction;
    public int shininess;
    public ArrayList<Vec3> faceNormals;
    public ArrayList<Vec3> faces;
    public ArrayList<Vec3> texelF;
    public double ka, kd, ks;
    public Color surfaceColor;

    public Mesh(double transparency, double reflection, double refraction, int shininess, double ka, double kd,
                double ks, ArrayList<Vec3> faceNormals, ArrayList<Vec3> faces, ArrayList<Vec3> texelF) {
        this.transparency = transparency;
        this.reflection = reflection;
        this.refraction = refraction;
        this.shininess = shininess;
        this.faceNormals = faceNormals;
        this.texelF = texelF;
        this.faces = faces;
        this.ka = ka;
        this.kd = kd;
        this.ks = ks;
    }
}

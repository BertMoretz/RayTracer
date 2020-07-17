import math.Color;
import math.Mesh;
import math.Sphere;
import math.Vec3;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class World {
    public String filename;
    public Vec3 cameraPosition;
    public Vec3 cameraLookAt;
    public Vec3 up;
    public int horizontalFOV;
    public int width;
    public int height;
    public int maxBounces;
    public Color backgroundColor;
    public ArrayList<Sphere> spheres = new ArrayList<>();
    public ArrayList<Mesh> meshes = new ArrayList<>();

    public Color ambient;

    public ArrayList<Color> lights = new ArrayList<>();
    public ArrayList<Vec3> lightsPos = new ArrayList<>();
    public ArrayList<String> lightType = new ArrayList<>();

    public BufferedImage texture;

    public World() {
    }
}

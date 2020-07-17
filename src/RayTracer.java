
import math.*;
import math.Sphere;
import math.Surface;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class RayTracer {
    public static int MAX_DEPTH;
    public static double u,v;

    public static void main(String[] args) throws FileNotFoundException {
        XMLParser parser = new XMLParser();
        World world = parser.parse(args[0]);

        renderImage(world);
    }

    private static void renderImage(World world) {
        int width = world.width;
        int height = world.height;

        // TODO: TAKE FILE NAME FROM XML
        File output = new File("../" + world.filename);
        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

//        Sphere sphere = new Sphere(new Vec3(0.0, 0.0, 0.0), 60.0, new Color(1.0F, 0.0F,0.0F), 0.0, 0.0);

        //Calculation of view matrix
        Vec3 look = new Vec3(world.cameraLookAt.x - world.cameraPosition.x,
                world.cameraLookAt.y - world.cameraPosition.y,
                world.cameraLookAt.z - world.cameraPosition.z);
        Vec3 dU = look.cross(world.up).normalize();
        Vec3 dV = look.cross(dU).normalize();
        double fl = (world.width / (2 * Math.tan((world.horizontalFOV)*Math.PI/180)));

        Vec3 Vp = look.normalize();
        Vp.x = Vp.x * fl - 0.5f * (world.width * dU.x + world.height * dV.x);
        Vp.y = Vp.y * fl - 0.5f * (world.width * dU.y + world.height * dV.y);
        Vp.z = Vp.z * fl - 0.5f * (world.width * dU.z + world.height * dV.z);

        MAX_DEPTH = world.maxBounces;
        int bounces = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vec3 rayOrig = world.cameraPosition;
                Vec3 rayDir = new Vec3(
                        x*dU.x + y*dV.x + Vp.x,
                        x*dU.y + y*dV.y + Vp.y,
                        x*dU.z + y*dV.z + Vp.z);


                Color color = trace(rayOrig, rayDir, world.spheres, bounces, world);
                buffer.setRGB(x,y, color.toRender());
            }
        }

        try {
            ImageIO.write(buffer, "PNG", output);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while writing PNG image");
        }
    }

    private static Color trace(Vec3 rayOrig, Vec3 rayDir, ArrayList<Sphere> spheres, int bounces, World world) {
        double min = Double.MAX_VALUE;
        Sphere sphere = null;
        Mesh mesh = null;
        boolean meshFlag = false;
        Vec3 nrm = null;
        Color sC = null;

        double r = 0;
        double g = 0;
        double b = 0;

        for (int i = 0; i < spheres.size(); i++) {
            double intersect = spheres.get(i).intersect(rayOrig, rayDir);
            if (intersect != 0.0 && intersect < min) {
                min = intersect;
                sphere = new Sphere(spheres.get(i));
            }
        }

        if (sphere == null) {
            for (int i = 0; i < world.meshes.size(); i++) {
                for (int j = 0; j < world.meshes.get(i).faces.size(); j+=3) {
                    ArrayList<Vec3> f = world.meshes.get(i).faces;
                    double intersect = polygonInt(rayOrig, rayDir, f.get(j), f.get(j+1), f.get(j+2));

                    if (intersect != 0.0 && intersect < min) {
                        min = intersect;
                        mesh = world.meshes.get(i);
                        meshFlag = true;
                        nrm = world.meshes.get(i).faceNormals.get(j);

                        if (world.meshes.get(i).surfaceColor != null) {
                            sC = world.meshes.get(i).surfaceColor;
                        }

                        if (world.texture != null) {
                            double a = (1 - u - v) * world.meshes.get(i).texelF.get(j).x + u * world.meshes.get(i).texelF.get(j+1).x + v * world.meshes.get(i).texelF.get(j+2).x;
                            double a0 = (1 - u - v) * world.meshes.get(i).texelF.get(j).y + u * world.meshes.get(i).texelF.get(j+1).y + v * world.meshes.get(i).texelF.get(j+2).y;

                            double xPos = Math.floor(world.texture.getWidth()*(1-a0));
                            double yPos = Math.floor(world.texture.getHeight()*(a));

                            int clr = world.texture.getRGB((int) xPos, (int) yPos);
                            double red =   ((clr & 0x00ff0000) >> 16) / (double) 255;
                            double green = ((clr & 0x0000ff00) >> 8)  / (double) 255;
                            double blue =   (clr & 0x000000ff)  / (double) 255;

                            sC = new Color( (float) red, (float) green, (float) blue);

                        }
                    }
                }
            }

            if (mesh == null) {
                return world.backgroundColor;
            }
        }


        Color surfaceColor = new Color(0, 0, 0);
        Vec3 intersectionPoint = rayOrig.add(rayDir.multiply(min));

        Vec3 normal = null;
        Color surfColor;
        double ka;
        double kd;
        double ks;
        int shininess;
        double tr, re, ref;
        if (meshFlag) {
            normal = nrm.normalize();

            surfColor = sC;
            ka = mesh.ka;
            kd = mesh.kd;
            ks = mesh.ks;
            shininess = mesh.shininess;
            tr = mesh.transparency;
            re = mesh.reflection;
            ref = mesh.refraction;
        } else {
            normal = (intersectionPoint.subtract(sphere.center)).normalize();

            surfColor = sphere.surfaceColor;
            ka = sphere.ka;
            kd = sphere.kd;
            ks = sphere.ks;
            shininess = sphere.shininess;
            tr = sphere.transparency;
            re = sphere.reflection;
            ref = sphere.refraction;
        }


        double bias =  0.001f;
        boolean inside = false;
        if (rayDir.dot(normal) > 0) {
            // We are inside the sphere
            normal = new Vec3(-normal.x, -normal.y, -normal.z);
            inside = true;
        }

        r += ka * surfColor.r * world.ambient.r;
        g += ka * surfColor.g * world.ambient.g;
        b += ka * surfColor.b * world.ambient.b;


        for (int i = 0; i < world.lights.size(); i++) {
            Color light = world.lights.get(i);
            Vec3 lightDir = world.lightsPos.get(i);

            Vec3 l;
            if (world.lightType.get(i).equals("DIRECTION")) {
                l = new Vec3(-lightDir.x, -lightDir.y, -lightDir.z).normalize();
            } else {
                l = new Vec3(lightDir.x - intersectionPoint.x, lightDir.y - intersectionPoint.y, lightDir.z - intersectionPoint.z).normalize();
//                l = new Vec3(-lightDir.x, -lightDir.y, -lightDir.z).normalize();
            }

            Vec3 pOffset = new Vec3(intersectionPoint.x + bias*l.x, intersectionPoint.y + bias*l.y, intersectionPoint.z + bias*l.z);
            if (traceShadow(pOffset, l, spheres)) {
            } else {
                if ((tr > 0 || re > 0) && bounces < MAX_DEPTH) {

                } else {
                    double lambertian = Math.max(normal.dot(l), 0.0);
                    if (lambertian > 0.0) {
                        if (kd > 0) {
                            double diffuse = kd * lambertian;
                            r += diffuse * surfColor.r * light.r;
                            g += diffuse * surfColor.g * light.g;
                            b += diffuse * surfColor.b * light.b;
                        }
                        if (ks > 0) {
                            lambertian *= 2;

                            Vec3 v = new Vec3(-rayDir.x, -rayDir.y, -rayDir.z).normalize();

//                        Vec3 halfway = v.add(l).divide(v.add(l).getLength());

                            double specular = v.dot(new Vec3(lambertian * normal.x - l.x,
                                    lambertian * normal.y - l.y,
                                    lambertian * normal.z - l.z));

//                        double specular = normal.dot(halfway);
                            if (specular > 0) {
                                specular = ks * (Math.pow(specular, shininess));
                                r += specular * light.r;
                                g += specular * light.g;
                                b += specular * light.b;
                            }
                        }
                    }
                }
            }
        }

        if (re > 0 && bounces < MAX_DEPTH) {
            double facingRatio = -rayDir.dot(normal);
            double fresnelEffect = mix(Math.pow(1 - facingRatio, 3), 1, 0.1);

            Vec3 reflDir = rayDir.subtract(normal.multiply(2).multiply(rayDir.dot(normal))).normalize();
            Color reflection = trace(intersectionPoint.add(normal.multiply(bias)), reflDir, spheres, bounces + 1, world);


            r += re * reflection.r;
            g += re * reflection.g;
            b += re * reflection.b;
        }

        if (tr > 0.0 && bounces < MAX_DEPTH) {
            double iof = ref;
            double eta = inside ? iof : 1 / iof;
            double cosi = -normal.dot(rayDir);
            double k = 1 - eta * eta * (1 - cosi * cosi);
            Vec3 refractionDir = rayDir.multiply(eta).add(normal.multiply(eta * cosi - Math.sqrt(k))).normalize();
            Color refraction = trace(intersectionPoint.subtract(normal.multiply(bias)), refractionDir, spheres, bounces + 1, world);

            r += refraction.r * tr;
            g += refraction.g * tr;
            b += refraction.b * tr;
        }

        if (bounces >= MAX_DEPTH)
            System.out.println("True");

        r = (r > 1f) ? 1f : r;
        g = (g > 1f) ? 1f : g;
        b = (b > 1f) ? 1f : b;
        return new Color((float)r, (float)g,(float) b);
    }

    public static double mix(double a, double b, double mix) {
        return b * mix + a * (1 - mix);
    }

    public static double polygonInt(Vec3 origRay, Vec3 dirRay, Vec3 v0, Vec3 v1, Vec3 v2) {
        Vec3 v0_v1 = v1.subtract(v0);
        Vec3 v0_v2 = v2.subtract(v0);
        Vec3 n = v0_v1.cross(v0_v2);

        double den = n.dot(n);

        double nDotDir = n.dot(dirRay);

        if (Math.abs(nDotDir) < 10E-9) {
            return 0.0;
        }

        double d = n.dot(v0);
        double t = -((n.dot(origRay) - d) / nDotDir);

        if (t < 0) return 0.0;

        Vec3 P = origRay.add(dirRay.multiply(t));
        Vec3 C;

        Vec3 edge0 = v1.subtract(v0);
        Vec3 vp0 = P.subtract(v0);
        C = edge0.cross(vp0);
        if (n.dot(C) < 0) return 0.0;

        Vec3 edge1 = v2.subtract(v1);
        Vec3 vp1 = P.subtract(v1);
        C = edge1.cross(vp1);
        if ( (u = n.dot(C)) < 0) return 0.0;

        Vec3 edge2 = v0.subtract(v2);
        Vec3 vp2 = P.subtract(v2);
        C = edge2.cross(vp2);
        if ( ( v = n.dot(C)) < 0) return 0.0;

        u /= den;
        v /= den;

        return t;
    }

    private static boolean traceShadow(Vec3 rayOrig, Vec3 rayDir, ArrayList<Sphere> spheres) {
        double min = Double.MAX_VALUE;
        Sphere sphere = null;

        for (int i = 0; i < spheres.size(); i++) {
            double intersect = spheres.get(i).intersect(rayOrig, rayDir);
            if (intersect != 0.0 && intersect < min) {
                min = intersect;
                sphere = new Sphere(spheres.get(i));
            }
        }

        if (sphere == null) {
            return false;
        } else {
            return true;
        }
    }
}

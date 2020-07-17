import math.Color;
import math.Mesh;
import math.Sphere;
import math.Vec3;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class XMLParser {

    public XMLParser () {}

    public World parse(String path) throws FileNotFoundException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(path));

            World newWorld = new World();

            Element filename = (Element) document.getElementsByTagName("scene").item(0);
            newWorld.filename = filename.getAttribute("output_file");

            //Camera Position Parsing
            Element position = (Element) document.getElementsByTagName("position").item(0);
            newWorld.cameraPosition = new Vec3(Double.parseDouble(position.getAttribute("x")),
                    Double.parseDouble(position.getAttribute("y")),
                    Double.parseDouble(position.getAttribute("z")));

            //Camera lookat Parsing
            Element lookAt = (Element) document.getElementsByTagName("lookat").item(0);
            newWorld.cameraLookAt = new Vec3(Double.parseDouble(lookAt.getAttribute("x")),
                    Double.parseDouble(lookAt.getAttribute("y")),
                    Double.parseDouble(lookAt.getAttribute("z")));

            //Camera up Parsing
            Element up = (Element) document.getElementsByTagName("up").item(0);
            newWorld.up = new Vec3(Double.parseDouble(up.getAttribute("x")),
                    Double.parseDouble(up.getAttribute("y")),
                    Double.parseDouble(up.getAttribute("z")));

            //FOV parsing
            Element fov = (Element) document.getElementsByTagName("horizontal_fov").item(0);
            newWorld.horizontalFOV = Integer.parseInt(fov.getAttribute("angle"));

            //Resolution parsing
            Element resolution = (Element) document.getElementsByTagName("resolution").item(0);
            newWorld.width = Integer.parseInt(resolution.getAttribute("horizontal"));
            newWorld.height = Integer.parseInt(resolution.getAttribute("vertical"));


            //Bounces
            Element bounce = (Element) document.getElementsByTagName("max_bounces").item(0);
            newWorld.maxBounces = Integer.parseInt(bounce.getAttribute("n"));

            //Background Color Parsing
            Element bc = (Element) document.getElementsByTagName("background_color").item(0);
            newWorld.backgroundColor = new Color(Float.parseFloat(bc.getAttribute("r")),
                    Float.parseFloat(bc.getAttribute("g")),
                    Float.parseFloat(bc.getAttribute("b")));


            NodeList list = document.getElementsByTagName("sphere");

            for (int i = 0; i < list.getLength(); i++) {
                Element sphere = (Element) list.item(i);
                double radius = Double.parseDouble(sphere.getAttribute("radius"));

                //Sphere position
                Element spherePosElem = (Element) sphere.getElementsByTagName("position").item(0);
                Vec3  spherePos = new Vec3(Double.parseDouble(spherePosElem.getAttribute("x")),
                        Double.parseDouble(spherePosElem.getAttribute("y")),
                        Double.parseDouble(spherePosElem.getAttribute("z")));

                //Sphere surface color
                Element shereColorElem = (Element) sphere.getElementsByTagName("color").item(0);
                Color  sphereColor = new Color(Float.parseFloat(shereColorElem.getAttribute("r")),
                        Float.parseFloat(shereColorElem.getAttribute("g")),
                        Float.parseFloat(shereColorElem.getAttribute("b")));

                Element reflectionElem = (Element) sphere.getElementsByTagName("reflectance").item(0);
                double  reflectance = Double.parseDouble(reflectionElem.getAttribute("r"));

                Element tranElem = (Element) sphere.getElementsByTagName("transmittance").item(0);
                double  transmittance = Double.parseDouble(tranElem.getAttribute("t"));

                Element refractionElem = (Element) sphere.getElementsByTagName("refraction").item(0);
                double  refraction = Double.parseDouble(refractionElem.getAttribute("iof"));

                // sphere phong coefficient
                Element phong = (Element) sphere.getElementsByTagName("phong").item(0);
                int  shininess = Integer.parseInt(phong.getAttribute("exponent"));
                double ka = Double.parseDouble(phong.getAttribute("ka"));
                double kd = Double.parseDouble(phong.getAttribute("kd"));
                double ks = Double.parseDouble(phong.getAttribute("ks"));

                newWorld.spheres.add(new Sphere(spherePos, radius, sphereColor, transmittance, reflectance,
                        refraction, shininess, ka, kd, ks));

            }

            //meshes Parsing
            NodeList meshes = document.getElementsByTagName("mesh");
            for (int i = 0; i < meshes.getLength(); i++) {
                Element mesh = (Element) meshes.item(i);
                String name = mesh.getAttribute("name");

                String[] folders = path.split("/");
                String meshPath = "";
                for (int j = 0; j < folders.length-1; j++) {
                    meshPath += folders[j] + "/";
                }

                MeshParser mp = new MeshParser();


                Element reflectionElem = (Element) mesh.getElementsByTagName("reflectance").item(0);
                double  reflectance = Double.parseDouble(reflectionElem.getAttribute("r"));

                Element tranElem = (Element) mesh.getElementsByTagName("transmittance").item(0);
                double  transmittance = Double.parseDouble(tranElem.getAttribute("t"));

                Element refractionElem = (Element) mesh.getElementsByTagName("refraction").item(0);
                double  refraction = Double.parseDouble(refractionElem.getAttribute("iof"));

                // sphere phong coefficient
                Element phong = (Element) mesh.getElementsByTagName("phong").item(0);
                int  shininess = Integer.parseInt(phong.getAttribute("exponent"));
                double ka = Double.parseDouble(phong.getAttribute("ka"));
                double kd = Double.parseDouble(phong.getAttribute("kd"));
                double ks = Double.parseDouble(phong.getAttribute("ks"));

                Mesh newMesh = mp.parse(meshPath, name, reflectance, transmittance, refraction, shininess, ka, kd, ks);

                NodeList meshCol = mesh.getElementsByTagName("color");
                if (meshCol.getLength() > 0) {
                    Element meshColor = (Element) meshCol.item(0);
                    Color  col = new Color(Float.parseFloat(meshColor.getAttribute("r")),
                            Float.parseFloat(meshColor.getAttribute("g")),
                            Float.parseFloat(meshColor.getAttribute("b")));
                    newMesh.surfaceColor = col;
                }

                NodeList text = mesh.getElementsByTagName("texture");
                if (text.getLength() > 0) {
                    Element texture = (Element) text.item(0);
                    String  textName = texture.getAttribute("name");

                    File file = new File(meshPath + textName);
                    BufferedImage image = ImageIO.read(file);
                    newWorld.texture = image;
                }

                newWorld.meshes.add(newMesh);
            }

            //Light parsing
            Element al = (Element) document.getElementsByTagName("ambient_light").item(0);
            Element al_color = (Element) al.getElementsByTagName("color").item(0);
            newWorld.ambient = new Color(Float.parseFloat(al_color.getAttribute("r")),
                    Float.parseFloat(al_color.getAttribute("g")),
                    Float.parseFloat(al_color.getAttribute("b")));

            NodeList lights = document.getElementsByTagName("parallel_light");

            for (int i = 0; i < lights.getLength(); i++) {
                Element light = (Element) lights.item(i);

                //Sphere position
                Element lightPos = (Element) light.getElementsByTagName("direction").item(0);
                Vec3  pos = new Vec3(Double.parseDouble(lightPos.getAttribute("x")),
                        Double.parseDouble(lightPos.getAttribute("y")),
                        Double.parseDouble(lightPos.getAttribute("z")));

                newWorld.lightsPos.add(pos);

                //Sphere surface color
                Element lightColor = (Element) light.getElementsByTagName("color").item(0);
                Color  col = new Color(Float.parseFloat(lightColor.getAttribute("r")),
                        Float.parseFloat(lightColor.getAttribute("g")),
                        Float.parseFloat(lightColor.getAttribute("b")));
                newWorld.lights.add(col);

                newWorld.lightType.add("DIRECTION");
            }

            lights = document.getElementsByTagName("point_light");

            for (int i = 0; i < lights.getLength(); i++) {
                Element light = (Element) lights.item(i);

                //Sphere position
                Element lightPos = (Element) light.getElementsByTagName("position").item(0);
                Vec3  pos = new Vec3(Double.parseDouble(lightPos.getAttribute("x")),
                        Double.parseDouble(lightPos.getAttribute("y")),
                        Double.parseDouble(lightPos.getAttribute("z")));

                newWorld.lightsPos.add(pos);

                //Sphere surface color
                Element lightColor = (Element) light.getElementsByTagName("color").item(0);
                Color  col = new Color(Float.parseFloat(lightColor.getAttribute("r")),
                        Float.parseFloat(lightColor.getAttribute("g")),
                        Float.parseFloat(lightColor.getAttribute("b")));
                newWorld.lights.add(col);

                newWorld.lightType.add("POINT");
            }

            return newWorld;
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}

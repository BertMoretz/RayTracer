import math.Mesh;
import math.Vec3;

import java.io.*;
import java.util.ArrayList;

public class MeshParser {

    public MeshParser() {

    }

    public Mesh parse(String path, String name, double reflectance, double transmittance,
                      double refraction, int shininess, double ka, double kd, double ks) {
        ArrayList<Vec3> normals = new ArrayList<>();
        ArrayList<Vec3> vertices = new ArrayList<>();
        ArrayList<Vec3> texels = new ArrayList<>();
        ArrayList<Vec3> faces = new ArrayList<>();
        ArrayList<Vec3> faceNormals = new ArrayList<>();
        ArrayList<Vec3> texelF = new ArrayList<>();

        try {
            File obj = new File(path+name);
            BufferedReader reader = new BufferedReader(new FileReader(obj));
            String r;
            while ((r = reader.readLine()) != null) {
                // vertex or vertex normals
                if (r.charAt(0) == 'v') {
                    String[] splitted = r.split(" ");
                    // if normal
                    if (r.charAt(1) == 'n') {
                        normals.add(new Vec3(Double.parseDouble(splitted[1]),
                                Double.parseDouble(splitted[2]),Double.parseDouble(splitted[3])));
                    }
                    if (r.charAt(1) == ' ') {
                        vertices.add(new Vec3(Double.parseDouble(splitted[1]),
                                Double.parseDouble(splitted[2]),Double.parseDouble(splitted[3])));
                    }

                    if (r.charAt(1) == 't') {
                        texels.add(new Vec3(Double.parseDouble(splitted[1]),
                                Double.parseDouble(splitted[2]),0));
                    }
                }

                if (r.charAt(0) == 'f') {
                    String[]  facesSplitted = r.split(" ");
                    for (int i = 1; i < facesSplitted.length; i++) {

                        // if we already have this face in savedItems (helps us to avoid repetitions)
                        String[] temp = facesSplitted[i].split("/");

                        int index = (Integer.parseInt(temp[0]) - 1);
                        faces.add(vertices.get(index));
//                        faces.add(vertices.get(index+1));
//                        faces.add(vertices.get(index+2));
                        //We don't take the second element, because we don't have textures in our models
                        index = (Integer.parseInt(temp[2]) - 1);
                        faceNormals.add(normals.get(index));
//                        savedItems[faces[j]] = indCount;
//                        indices.push(indCount);
//                        indCount++;
                        index = (Integer.parseInt(temp[1]) - 1);
                        texelF.add(texels.get(index));
                    }
                }
            }
            Mesh m = new Mesh(transmittance, reflectance, refraction, shininess, ka, kd, ks, faceNormals, faces, texelF);
            return m;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

### How to Run:
1. Go to src folder of the RayTracer

2. Run the following command
    ````
   $ javac RayTracer.java
   ````
3. After the build
     ````
       $ java RayTracer "PUT_HERE_PATH_TO_XML_FILE"
     ````
4. Output file be created ahead of the directory as **you are now** (src folder).   
   
## Important Notice:
Document Builder will try to search for **scene.dtd**; code searches .png textures and .obj files that should be in the same directory as xml file

Example of usage:
````
       $ java RayTracer "/Users/albert/Desktop/example4.xml"
 ````
Therefore, scene.dtd, .png textures and .obj files should be in "/Users/albert/Desktop", otherwise DocumentBuilder will not parse the xml.

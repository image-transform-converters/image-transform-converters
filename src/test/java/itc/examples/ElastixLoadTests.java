package itc.examples;

import itc.transforms.elastix.ElastixTransform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ElastixLoadTests {

    public static void main( String[] args )
    {
        ArrayList<String> fName = new ArrayList<>();
        fName.add("TransformParameters.0.txt");
        fName.add("TransformParameters.1.txt");
        fName.add("TransformParameters.2.txt");

        fName.add("TransformParameters.Affine3D.txt");
        fName.add("TransformParameters.BSpline3D.txt");
        fName.add("TransformParameters.Euler2D.txt");
        fName.add("TransformParameters.Similarity3D.txt");

        fName.forEach( fileName -> {
            try {
                System.out.print(fileName +" > ");
                ElastixTransform et =  ElastixTransform.load(
                        //new File(path+fileName)
                        new File( ElastixLoadEulerFromFile.class.getResource(
                        "/elastix/"+fileName ).getFile() )
                );
                //System.out.println(et);
                System.out.println(et.getClass().getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

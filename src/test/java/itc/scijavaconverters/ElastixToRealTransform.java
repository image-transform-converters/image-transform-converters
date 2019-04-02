package itc.scijavaconverters;

import itc.examples.ElastixLoadEulerFromFile;
import itc.transforms.elastix.ElastixAffineTransform;
import itc.transforms.elastix.ElastixTransform;
import net.imagej.ImageJ;
import net.imglib2.realtransform.RealTransform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ElastixToRealTransform {
    public static void main (String... args) {

        ArrayList<String> fName = new ArrayList<>();
        fName.add("TransformParameters.0.txt");
        fName.add("TransformParameters.1.txt");
        fName.add("TransformParameters.2.txt");

        fName.add("TransformParameters.Affine3D.txt");
        fName.add("TransformParameters.BSpline3D.txt");
        fName.add("TransformParameters.Euler2D.txt");
        fName.add("TransformParameters.Similarity3D.txt");


        final ImageJ ij = new ImageJ();

        fName.forEach( fileName -> {
            try {
                ElastixTransform et =  ElastixTransform.load(
                        new File( ElastixToRealTransform.class.getResource(
                                "/elastix/"+fileName ).getFile() )
                );
                RealTransform rt = ij.convert().convert(et, RealTransform.class);
                System.out.println(et.getClass().getName()+">"+RealTransform.class.getName()+":");
                if (ij.convert().supports(et, RealTransform.class)) {
                    System.out.println("\t Conversion result : \n" + rt);
                } else {
                    System.out.println("\t Unsupported conversion");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}

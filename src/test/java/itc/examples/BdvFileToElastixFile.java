package itc.examples;

import itc.converters.BdvTransformToElastixAffine3D;
import itc.transforms.bdv.BdvTransform;
import itc.transforms.elastix.ElastixAffineTransform3D;
import itc.transforms.elastix.ElastixTransform;

import java.io.File;
import java.io.IOException;

public class BdvFileToElastixFile
{

    public static void main( String[] args ) throws IOException
    {

        final File file = new File(
                AffineFromBdvXml.class
                        .getResource( "../../bdv/bdv.xml" ).getFile() );

        final BdvTransform bdvTransform = BdvTransform.load( file );


        // TODO: calibration issue
        // TODO: ResultImage Size

//        final BdvTransformToElastixAffine3D converter
//                = new BdvTransformToElastixAffine3D(
//                        ElastixTransform.FINAL_LINEAR_INTERPOLATOR,
//                        ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_CHAR );
//
//        final ElastixAffineTransform3D elastixAffineTransform3D
//                = converter.convert( bdvTransform );
//
//        System.out.printf( elastixAffineTransform3D.toString() );
//
//        final ElastixTransform elastixTransform = ElastixTransform.load(
//                new File( ElastixLoadAffineFromFile.class.getResource(
//                        "/elastix/TransformParameters.Affine3D.txt" ).getFile() ) );
//
//        System.out.println("Output the elastix file as a String ");
//
//        System.out.println( elastixTransform );
//
//        File f =  elastixTransform.toFile();
//
//        System.out.println("The elastix transform file is saved is the standard temporary directory : "+f.getAbsolutePath());
//
//        elastixTransform.CompressResultImage=!elastixTransform.CompressResultImage; // modifying a parameter of the transform
//
//        System.out.println("Now saving the file to the user standard folder");
//
//        String path = System.getProperty("user.home")+File.separator+"TransformParameters.Affine3D.txt";
//
//        System.out.println("Default path:"+path);
//
//        elastixTransform.save(path);

    }

}

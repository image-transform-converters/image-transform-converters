/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2024 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
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

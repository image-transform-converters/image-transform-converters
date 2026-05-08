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

import itc.transforms.elastix.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class contains tests to check that Elastix Transform files are loaded
 * and cast to their correct class. No other check is done.
 *
 * Class to be tested :
 *     - {@link ElastixEulerTransform2D}
 *     - {@link ElastixEulerTransform3D} // TODO!!
 *     - {@link ElastixSimilarityTransform2D} // TODO !!
 *     - {@link ElastixSimilarityTransform3D}
 *     - {@link ElastixAffineTransform2D}
 *     - {@link ElastixAffineTransform3D}
 *     - {@link ElastixBSplineTransform2D}
 *     - {@link ElastixBSplineTransform3D}
 *
 * @author Nicolas Chiaruttini
 */

public class ElastixLoadTests {

    public static void main( String[] args )
    {
        ArrayList<String> fName = new ArrayList<>();
        fName.add("TransformParameters.Euler2D.Sequence0.txt");
        fName.add("TransformParameters.Affine2D.Sequence1.txt");
        fName.add("TransformParameters.BSpline2D.Sequence2.txt");

        fName.add("TransformParameters.Affine3D.txt");
        fName.add("TransformParameters.BSpline3D.txt");
        fName.add("TransformParameters.Euler2D.txt");
        fName.add("TransformParameters.Similarity3D.txt");
        fName.add("TransformParameters_from_elastix_5.2.0.Affine.txt");

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

    public static ElastixTransform getElastixTransformFromTestResources(String fileName) throws Exception {
        return ElastixTransform.load(
                new File( ElastixLoadEulerFromFile.class.getResource(
                        "/elastix/"+fileName ).getFile() )
        );
    }

    @Test
    public void testLoadElastixEuler2DClass() throws Exception {
        Assert.assertEquals(
                getElastixTransformFromTestResources(
                        "TransformParameters.Euler2D.Sequence0.txt"
                ).getClass(),
                ElastixEulerTransform2D.class);

        Assert.assertEquals(
                getElastixTransformFromTestResources(
                        "TransformParameters.Euler2D.txt"
                ).getClass(),
                ElastixEulerTransform2D.class);
    }

    // TODO : put an example of ElastixEuler3D transform in the test resources
    /*@Test
    public void testLoadElastixEuler3DClass() throws Exception {
        Assert.assertEquals(
                getElastixTransformFromTestResources(
                ??
                ).getClass(),
                ElastixEulerTransform2D.class);
    }*/


    // TODO : put an example of Similarity2D transform in the test resources
    /*@Test
    public void testLoadElastixSimilarity2DClass() throws Exception {
        Assert.assertEquals(
                getElastixTransformFromTestResources(
                        ??
                ).getClass(),
                ElastixBSplineTransform2D.class);
    }*/

    @Test
    public void testLoadElastixSimilarity3DClass() throws Exception {
        Assert.assertEquals(
                getElastixTransformFromTestResources(
                        "TransformParameters.Similarity3D.txt"
                ).getClass(),
                ElastixSimilarityTransform3D.class);
    }

    @Test
    public void testLoadElastixAffine2DClass() throws Exception {
        Assert.assertEquals(
                getElastixTransformFromTestResources(
                        "TransformParameters.Affine2D.Sequence1.txt"
                ).getClass(),
                ElastixAffineTransform2D.class);
    }

    @Test
    public void testLoadElastixAffine3DClass() throws Exception {
        Assert.assertEquals(
                getElastixTransformFromTestResources(
                        "TransformParameters.Affine3D.txt"
                ).getClass(),
                ElastixAffineTransform3D.class);
    }

    @Test
    public void testLoadElastixBSpline2DClass() throws Exception {
        Assert.assertEquals(
                getElastixTransformFromTestResources(
                        "TransformParameters.BSpline2D.Sequence2.txt"
                ).getClass(),
                ElastixBSplineTransform2D.class);
    }

    @Test
    public void testLoadElastixBSpline3DClass() throws Exception {
        Assert.assertEquals(
                getElastixTransformFromTestResources(
                        "TransformParameters.BSpline3D.txt"
                ).getClass(),
                ElastixBSplineTransform3D.class);
    }

}

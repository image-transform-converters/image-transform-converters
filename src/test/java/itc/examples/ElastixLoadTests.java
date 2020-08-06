/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2020 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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

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

import itc.converters.ElastixAffine2DToAffineTransform2D;
import itc.transforms.elastix.ElastixAffineTransform2D;
import itc.transforms.elastix.ElastixTransform;
import net.imglib2.realtransform.AffineTransform2D;

import java.io.File;
import java.io.IOException;

public class ElastixLoadAffine2DFromFileAndConvertToAffineTransform2D
{
    public static void main( String[] args ) throws IOException
    {
        final ElastixTransform elastixTransform = ElastixTransform.load(
                new File( ElastixLoadEulerFromFileAndConvertToAffineTransform3D.class.getResource(
                        "/elastix/TransformParameters.Affine2D.Sequence1.txt" ).getFile() ) );

        if ( elastixTransform instanceof ElastixAffineTransform2D)
        {
            System.out.println( "I am a ElastixAffineTransform2D\n");
        }

        AffineTransform2D convertedTransform2D = new ElastixAffine2DToAffineTransform2D().convert( ( ElastixAffineTransform2D ) elastixTransform );

        System.out.println( elastixTransform );
        System.out.println( convertedTransform2D );

    }
}

/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2023 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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

import itc.converters.AffineTransform3DToElastixAffine3D;
import itc.converters.AmiraEulerToAffineTransform3D;
import itc.transforms.elastix.ElastixAffineTransform3D;
import itc.transforms.elastix.ElastixTransform;
import net.imglib2.realtransform.AffineTransform3D;

public class AmiraEulerToElastixAffineFile
{
	public static void main( String[] args )
	{

		// Manually determined parameters using Amira
		double[] translationInMicrometer = new double[]{ 147.9, 48.13, 103.0661 };
		double[] rotationAxis = new double[]{ 0.064, 0.762, 0.643 };
		double[] rotationCentreInMicrometer = new double[]{ 22.75, 20.0, 36.25 };
		double rotationAngleInDegrees = 237.0;

		final AffineTransform3D affineTransform3DInMillimeter =
				AmiraEulerToAffineTransform3D.convert(
						rotationAxis,
						rotationAngleInDegrees,
						translationInMicrometer,
						rotationCentreInMicrometer );

		final AffineTransform3DToElastixAffine3D affineTransform3DToElastixAffine3D
				= new AffineTransform3DToElastixAffine3D(
					ElastixTransform.FINAL_LINEAR_INTERPOLATOR,
					ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_CHAR,
					new Double[]{ 0.0005, 0.0005, 0.0005 },
					new Integer[]{ 550, 518, 570 }
		);

		// invert, because elastix transform goes from output to input
		final AffineTransform3D inverse = affineTransform3DInMillimeter.inverse();

		final ElastixAffineTransform3D elastixAffineTransform3D
				= affineTransform3DToElastixAffine3D.convert( inverse );

		elastixAffineTransform3D.save( "/Users/tischer/Desktop/elastix-affine-transform.txt" );


	}
}

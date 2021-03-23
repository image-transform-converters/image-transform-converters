/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2021 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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
package itc.converters;

import itc.transforms.elastix.ElastixSimilarityTransform3D;
import net.imglib2.realtransform.AffineTransform3D;

public class ElastixSimilarity3DToAffineTransform3D
{

	/**
	 * From elastix-4.9.0 manual:
	 *
	 * Similarity: (SimilarityTransform)
	 *
	 * A similarity transformation is defined as Tμ(x) = sR(x − c) + t + c, (2.13)
	 * with s a scalar and R a rotation matrix.
	 * This means that the image is treated as an object, which can translate, rotate, and scale isotropically.
	 * The rotation matrix is parameterised by an angle in 2D, and by a so-called “versor” in 3D
	 * (Euler angles could have been used as well). The parameter vector μ consists of the angle/versor,
	 * the translation vector, and the isotropic scaling factor.
	 *
	 * In 2D, this gives a vector of length 4: μ = (s,θz,tx,ty)T.
	 *
	 * In 3D, this gives a vector of length 7: μ = (q1,q2,q3,tx,ty,tz,s)T,
	 * where q1, q2, and q3 are the elements of the versor.
	 *
	 * There are few cases when you need this transform.
	 *
	 * Note: Elastix units are always millimeters
	 *
	 * @param elastixSimilarityTransform3D
	 *
	 * @return an affine transform performing the Euler transform, in millimeter units
	 */
	public static AffineTransform3D convert( ElastixSimilarityTransform3D elastixSimilarityTransform3D )
	{
		// fetch values from elastix transform
		//
		final double[][] rotationMatrix = elastixSimilarityTransform3D.getRotationMatrix();
		final double scalingFactor = elastixSimilarityTransform3D.getScalingFactor();
		final double[] rotationCenterInMillimeters = elastixSimilarityTransform3D.getRotationCenterInMillimeters();
		final double[] translationInMillimeters = elastixSimilarityTransform3D.getTranslationInMillimeters();


		// convert
		//
		final double[] rotationCentrePositive = new double[ 3 ];
		final double[] rotationCentreNegative = new double[ 3 ];

		for ( int d = 0; d < 3; ++d )
		{
			rotationCentrePositive[ d ] = rotationCenterInMillimeters[ d ];
			rotationCentreNegative[ d ] = - rotationCenterInMillimeters[ d ];
		}

		final AffineTransform3D transform3D = new AffineTransform3D();

		// rotate and scale
		//

		// translate to rotation centre
		transform3D.translate( rotationCentreNegative );

		// rotate
		final AffineTransform3D rotate = new AffineTransform3D();
		for ( int row = 0; row < 3; row++ )
		{
			for ( int col = 0; col < 3; col++ )
			{
				rotate.set( rotationMatrix[row][col], row, col );
			}
		}

		transform3D.preConcatenate( rotate );

		// translate back from rotation centre
		final AffineTransform3D translateBackFromRotationCentre = new AffineTransform3D();
		translateBackFromRotationCentre.translate( rotationCentrePositive );

		transform3D.preConcatenate( translateBackFromRotationCentre );

		// TODO - where should scaling go?
		// scale
		transform3D.scale( scalingFactor );

		// translate
		//
		transform3D.translate( translationInMillimeters );

		return transform3D;
	}
}

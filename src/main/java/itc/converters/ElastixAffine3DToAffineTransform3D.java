/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2022 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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

import itc.transforms.elastix.ElastixAffineTransform3D;
import net.imglib2.realtransform.AffineTransform3D;

public class ElastixAffine3DToAffineTransform3D
{

	/**
	 * From elastix-4.9.0 manual:
	 * Affine: (AffineTransform) An affine transformation is defined as:
	 * Tμ(x) = A(x − c) + t + c, (2.14), where the matrix A has no restrictions.
	 * This means that the image can be translated, rotated, scaled,
	 * and sheared.
	 * The parameter vector μ is formed by the matrix elements aij and the
	 * translation vector.
	 * In 2D, this gives a vector of length 6:
	 * μ = (a11,a12,a21,a22,tx,ty)T.
	 * In 3D, this gives a vector of length 12.
	 *
	 * Note: Elastix transformations are always in millimeter units.
	 *
	 */
	public static AffineTransform3D convert( ElastixAffineTransform3D elastixAffineTransform3D )
	{
		// fetch values from elastix transform
		//
		final double[][] matrix = elastixAffineTransform3D.getMatrix();
		final double[] rotationCenterInMillimeters = elastixAffineTransform3D.getRotationCenterInMillimeters();
		final double[] translationInMillimeters = elastixAffineTransform3D.getTranslationInMillimeters();


		// convert
		//
		final double[] rotationCenterPositive = new double[ 3 ];
		final double[] rotationCenterNegative = new double[ 3 ];

		for ( int d = 0; d < 3; ++d )
		{
			rotationCenterPositive[ d ] = rotationCenterInMillimeters[ d ];
			rotationCenterNegative[ d ] = - rotationCenterInMillimeters[ d ];
		}

		final AffineTransform3D transform3D = new AffineTransform3D();

		// rotate and scale
		//

		// translate to rotation center
		transform3D.translate( rotationCenterNegative );

		// rotate and scale
		final AffineTransform3D rotateAndScale = new AffineTransform3D();
		for ( int row = 0; row < 3; row++ )
		{
			for ( int col = 0; col < 3; col++ )
			{
				rotateAndScale.set( matrix[row][col], row, col );
			}
		}

		transform3D.preConcatenate( rotateAndScale );

		// translate back from rotation center
		final AffineTransform3D translateBackFromRotationCenter = new AffineTransform3D();
		translateBackFromRotationCenter.translate( rotationCenterPositive );

		transform3D.preConcatenate( translateBackFromRotationCenter );

		// translate
		//
		transform3D.translate( translationInMillimeters );

		return transform3D;
	}


}

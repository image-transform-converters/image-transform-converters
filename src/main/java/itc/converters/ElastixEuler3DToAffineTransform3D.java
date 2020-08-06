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
package itc.converters;

import itc.transforms.elastix.ElastixEulerTransform3D;
import net.imglib2.realtransform.AffineTransform3D;

public class ElastixEuler3DToAffineTransform3D
{

	/**
	 * From elastix-4.9.0 manual:
	 *
	 * Rigid: (EulerTransform) A rigid transformation is defined as:
	 * Tμ(x) = R(x − c) + t + c,
	 * with the matrix R a rotation matrix (i.e. orthonormal and proper),
	 * c the centre of rotation, and t translation again.
	 * The image is treated as a rigid body, which can translate and rotate,
	 * but cannot be scaled/stretched.
	 * The rotation matrix is parameterised by the Euler angles (one in 2D, three in 3D).
	 * The parameter vector μ consists of the Euler angles (in rad)
	 * and the translation vector.
	 * In 2D, this gives a vector of length 3: μ = (θz , tx , ty )T ,
	 * where θz denotes the rotation around the axis normal to the image.
	 * In 3D, this gives a vector of length 6: μ = (θx,θy,θz,tx,ty,tz)T .
	 * The centre of rotation is not part of μ; it is a fixed setting,
	 * usually the centre of the image.
	 *
	 * Note: Elastix units are always millimeters
	 *
	 * @param elastixEulerTransform3D
	 *
	 * @return an affine transform performing the Euler transform, in millimeter units
	 */
	public static AffineTransform3D convert( ElastixEulerTransform3D elastixEulerTransform3D )
	{
		// fetch values from elastix transform
		//
		final double[] angles = elastixEulerTransform3D.getRotationAnglesInRadians();
		final double[] rotationCenterInMillimeters = elastixEulerTransform3D.getRotationCenterInMillimeters();
		final double[] translationInMillimeters = elastixEulerTransform3D.getTranslationInMillimeters();

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

		// rotate around rotation centre
		//

		// make rotation centre the image centre
		transform3D.translate( rotationCentreNegative );

		// rotate
		for ( int d = 0; d < 3; ++d )
			transform3D.rotate( d, angles[ d ]);

		// move image centre back
		final AffineTransform3D translateBackFromRotationCentre = new AffineTransform3D();
		translateBackFromRotationCentre.translate( rotationCentrePositive );
		transform3D.preConcatenate( translateBackFromRotationCentre );

		// translate
		//
		transform3D.translate( translationInMillimeters );

		return transform3D;
	}
}

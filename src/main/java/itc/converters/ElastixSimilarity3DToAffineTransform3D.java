package itc.converters;

import itc.transforms.elastix.ElastixEulerTransform3D;
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
	 * @param elastixEulerTransform3D
	 *
	 * @return an affine transform performing the Euler transform, in millimeter units
	 */
	public static AffineTransform3D convert( ElastixEulerTransform3D elastixEulerTransform3D )
	{
		final double[] angles = elastixEulerTransform3D.getRotationAnglesInRadians();

		final double[] translationInMillimeters = elastixEulerTransform3D.getTranslationInMillimeters();
		final double[] translationInPixels = new double[ 3 ];
		for ( int d = 0; d < 3; ++d )
		{
			translationInPixels[ d ] = translationInMillimeters[ d ];
		}

		final double[] rotationCenterInMillimeters = elastixEulerTransform3D.getRotationCenterInMillimeters();
		final double[] rotationCentreVectorInPixelsPositive = new double[ 3 ];
		final double[] rotationCentreVectorInPixelsNegative = new double[ 3 ];

		for ( int d = 0; d < 3; ++d )
		{
			rotationCentreVectorInPixelsPositive[ d ] = rotationCenterInMillimeters[ d ];
			rotationCentreVectorInPixelsNegative[ d ] = - rotationCenterInMillimeters[ d ];
		}


		final AffineTransform3D transform3D = new AffineTransform3D();

		// rotate around rotation centre
		//

		// make rotation centre the image centre
		transform3D.translate( rotationCentreVectorInPixelsNegative );

		// rotate
		for ( int d = 0; d < 3; ++d )
			transform3D.rotate( d, angles[ d ]);

		// move image centre back
		final AffineTransform3D translateBackFromRotationCentre = new AffineTransform3D();
		translateBackFromRotationCentre.translate( rotationCentreVectorInPixelsPositive );
		transform3D.preConcatenate( translateBackFromRotationCentre );

		// translate
		//
		transform3D.translate( translationInPixels );

		return transform3D;
	}
}

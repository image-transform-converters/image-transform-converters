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

		// translate back from rotation centre
		final AffineTransform3D translateBackFromRotationCentre = new AffineTransform3D();
		translateBackFromRotationCentre.translate( rotationCentrePositive );

		transform3D.preConcatenate( translateBackFromRotationCentre );

		// translate
		//
		transform3D.translate( translationInMillimeters );

		return transform3D;
	}


}

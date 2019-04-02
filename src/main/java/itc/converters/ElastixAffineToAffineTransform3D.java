package itc.converters;

import itc.transforms.ElastixEulerTransform3D;
import itc.transforms.elastix.ElastixAffineTransform;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineTransform;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.realtransform.AffineTransform3D;

public class ElastixAffineToAffineTransform3D
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
	 * @param affineTransform3D
	 * @param voxelSizeInMillimeter
	 * @return string as it appears in the Transformation.txt output of elastix
	 */
	public static AffineGet getAffineTransform3D(
			ElastixAffineTransform elastixAffineTransform,
			double voxelSizeInMillimeter )
	{
		switch ( elastixAffineTransform.FixedImageDimension )
		{
			case 2:
				return from2D( elastixAffineTransform );
			case 3:
				break;
			default:
				System.err.println("ElastixAffineTransform: Unsupported FixedImageDimension");
				return null;
		}

		return null;
	}

	private static AffineTransform2D from2D(
			ElastixAffineTransform elastixAffineTransform )
	{
		return null;
	}
}

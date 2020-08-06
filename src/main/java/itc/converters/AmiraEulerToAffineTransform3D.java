package itc.converters;

import net.imglib2.realtransform.AffineTransform3D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import static itc.utilities.TransformUtils.rotationAroundImageCenterTransform;
import static itc.utilities.TransformUtils.rotationMatrix;

public class AmiraEulerToAffineTransform3D
{
	/**
	 * Amira allows the user to interactively specify
	 * an Euler (rotation & translation) transformation.
	 * The user can read the transformation parameters on the screen.
	 * The units in Amira could be anything(?),
	 * but here, currently, micrometer units are required
	 * for the conversion.
	 *
	 * @param rotationAxis
	 * @param rotationAngleInDegrees
	 * @param translationVectorInMicrometer
	 * @param rotationCenterInMicrometer
	 * @return AffineTransform3D in millimeter units, as required by elastix
	 */
	public static AffineTransform3D convert(
			double[] rotationAxis,
			double rotationAngleInDegrees,
			double[] translationVectorInMicrometer,
			double[] rotationCenterInMicrometer )
	{
		// rotate around rotation centre
		//
		final Vector3D axis = new Vector3D(
				rotationAxis[ 0 ],
				rotationAxis[ 1 ],
				rotationAxis[ 2 ] );

		double angle = rotationAngleInDegrees / 180.0 * Math.PI;

		final double[][] rotationMatrix = rotationMatrix( axis, angle );

		double[] rotationCentreInMillimeter = microToMillimeter( rotationCenterInMicrometer );

		final AffineTransform3D transform3D =
				rotationAroundImageCenterTransform(
						rotationMatrix,
						rotationCentreInMillimeter );

		// translate
		//
		double[] translationInMillimeters = microToMillimeter( translationVectorInMicrometer );

		transform3D.translate( translationInMillimeters );

		return transform3D;
	}

	private static double[] microToMillimeter( double[] rotationCenterInMicrometer )
	{
		double[] rotationCentreInMillimeter = new double[ 3 ];
		for ( int d = 0; d < 3; ++d )
			rotationCentreInMillimeter[ d ] = rotationCenterInMicrometer[ d ] / 1000.0;
		return rotationCentreInMillimeter;
	}

}

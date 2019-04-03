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

		final ElastixAffineTransform3D elastixAffineTransform3D
				= affineTransform3DToElastixAffine3D.convert( affineTransform3DInMillimeter );

		elastixAffineTransform3D.save( "/Users/tischer/Desktop/transform.txt" );


	}
}

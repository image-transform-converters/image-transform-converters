package itc.transforms.elastix;

public class ElastixAffineTransform3D extends ElastixAffineTransform {

	public double[][] getMatrix()
	{
		final double[][] matrix = new double[3][3];

		matrix[ 0 ] = new double[]{ TransformParameters[ 0 ], TransformParameters[ 1 ], TransformParameters[ 2 ] };
		matrix[ 1 ] = new double[]{ TransformParameters[ 3 ], TransformParameters[ 4 ], TransformParameters[ 5 ] };
		matrix[ 2 ] = new double[]{ TransformParameters[ 6 ], TransformParameters[ 7 ], TransformParameters[ 8 ] };

		return matrix;
	}

	public double[] getTranslationInMillimeters()
	{
		final double[] translation = { TransformParameters[ 9 ], TransformParameters[ 10 ], TransformParameters[ 11 ] };
		return translation;
	}

	public double[] getRotationCenterInMillimeters()
	{
		final double[] rotationCenter = { CenterOfRotationPoint[ 0 ], CenterOfRotationPoint[ 1 ], CenterOfRotationPoint[ 2 ] };
		return rotationCenter;
	}
}

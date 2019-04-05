package itc.transforms.elastix;

public class ElastixEulerTransform3D extends ElastixEulerTransform {

	public double[] getRotationAnglesInRadians()
	{
		final double[] angles = { TransformParameters[ 0 ], TransformParameters[ 1 ], TransformParameters[ 2 ] };
		return angles;
	}

	public double[] getTranslationInMillimeters()
	{
		final double[] translation = { TransformParameters[ 3 ], TransformParameters[ 4 ], TransformParameters[ 5 ] };
		return translation;
	}

	public double[] getRotationCenterInMillimeters()
	{
		final double[] rotationCenter = { CenterOfRotationPoint[ 0 ], CenterOfRotationPoint[ 1 ], CenterOfRotationPoint[ 2 ] };
		return rotationCenter;
	}
}

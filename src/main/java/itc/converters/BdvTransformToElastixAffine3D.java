package itc.converters;

import itc.transforms.bdv.BdvTransform;
import itc.transforms.elastix.ElastixAffineTransform3D;
import itc.transforms.elastix.ElastixTransform;

public class BdvTransformToElastixAffine3D
{

	private final String interpolator;
	private final String resultImagePixelType;
	private final Integer[] resultImageDimensions;

	public BdvTransformToElastixAffine3D(
			String interpolator,
			String resultImagePixelType,
			Integer[] resultImageDimensions )
	{
		this.interpolator = interpolator;
		this.resultImagePixelType = resultImagePixelType;
		this.resultImageDimensions = resultImageDimensions;
	}

	public  ElastixAffineTransform3D convert( BdvTransform bdvTransform )
	{
		final ElastixAffineTransform3D elastixAffineTransform3D =
				new ElastixAffineTransform3D();

		setVoxelSpacing( bdvTransform, elastixAffineTransform3D );
		setAffineTransform( bdvTransform, elastixAffineTransform3D );

		elastixAffineTransform3D.Size = resultImageDimensions;
		elastixAffineTransform3D.ResampleInterpolator = interpolator;
		elastixAffineTransform3D.ResultImageFormat =
				ElastixTransform.RESULT_IMAGE_FORMAT_MHD;
		elastixAffineTransform3D.ResultImagePixelType = resultImagePixelType;

		setOtherParameters( elastixAffineTransform3D );

		return elastixAffineTransform3D;
	}

	private static void setOtherParameters(
			ElastixAffineTransform3D elastixAffineTransform3D )
	{

		elastixAffineTransform3D.Transform = ElastixTransform.AFFINE_TRANSFORM;
		elastixAffineTransform3D.FixedImageDimension = 3;
		elastixAffineTransform3D.MovingImageDimension = 3;
		elastixAffineTransform3D.NumberOfParameters = 12;

		elastixAffineTransform3D.InitialTransformParametersFileName = null;
		elastixAffineTransform3D.HowToCombineTransforms = "Compose";
		elastixAffineTransform3D.FixedInternalImagePixelType = "float";
		elastixAffineTransform3D.MovingInternalImagePixelType = "float";

		elastixAffineTransform3D.Index  = new Integer[3];
		elastixAffineTransform3D.Origin = new Double[3];
		elastixAffineTransform3D.Direction = new Double[]{
				1.0, 0.0, 0.0,
				0.0, 1.0, 0.0,
				0.0, 0.0, 1.0};
		elastixAffineTransform3D.UseDirectionCosines = false;
		elastixAffineTransform3D.CenterOfRotationPoint = new Double[3];
		elastixAffineTransform3D.Resampler = ElastixTransform.DEFAULT_RESAMPLER;
		elastixAffineTransform3D.DefaultPixelValue = 0.0;
		elastixAffineTransform3D.CompressResultImage = false;
	}

	private static void setAffineTransform( BdvTransform bdvTransform, ElastixAffineTransform3D elastixAffineTransform3D )
	{
		elastixAffineTransform3D.TransformParameters = new Double[ 12 ];
		final double[] rowPackedCopy = bdvTransform.affineTransform3D.getRowPackedCopy();
		for ( int i = 0; i < 12; i++ )
			elastixAffineTransform3D.TransformParameters[ i ] = rowPackedCopy[ i ];
	}

	private static void setVoxelSpacing( BdvTransform bdvTransform, ElastixAffineTransform3D elastixAffineTransform3D )
	{
		elastixAffineTransform3D.Spacing = new Double[3];
		for ( int i = 0; i < 3; i++ )
			elastixAffineTransform3D.Spacing[ i ] = bdvTransform.voxelSizes[ i ];
	}

}

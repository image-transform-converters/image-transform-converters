package itc.converters;

import itc.transforms.elastix.ElastixAffineTransform3D;
import itc.transforms.elastix.ElastixTransform;
import itc.utilities.TransformUtils;
import net.imglib2.realtransform.AffineTransform3D;

import static itc.utilities.Units.*;

public class BigWarpAffineToElastixAffineTransform3D
{
	public ElastixAffineTransform3D convert(
			AffineTransform3D bigWarpAffine,
			Double[] targetImageVoxelSpacingMillimeter,
			Integer[] targetImageDimensionsPixels,
			int targetImageBitDepth,
			String interpolator,
			String affineTransformUnit )
	{
		final String resultImagePixelType = getPixelTypeString( targetImageBitDepth );

		final AffineTransform3DToElastixAffine3D converter = new AffineTransform3DToElastixAffine3D(
				interpolator,
				resultImagePixelType,
				targetImageVoxelSpacingMillimeter,
				targetImageDimensionsPixels
		);

		// convert big warp affine transform to elastix
		//

		// the big warp affine transform already is from fixed to moving, thus no inversion is needed


		// elastix works in millimeters, thus we need to convert the big warp affine to millimeters
		if ( affineTransformUnit.equals( MILLIMETER ) )
			bigWarpAffine = bigWarpAffine;
		else if ( affineTransformUnit.equals( MICROMETER ) )
			bigWarpAffine = TransformUtils.scaleAffineTransform3DUnits( bigWarpAffine, new double[]{ 1.0 / 1000, 1.0 / 1000, 1.0 / 1000 } );
		else if ( affineTransformUnit.equals( NANOMETER ) )
			bigWarpAffine = TransformUtils.scaleAffineTransform3DUnits( bigWarpAffine, new double[]{ 1.0 / 1000000, 1.0 / 1000000, 1.0 / 1000000 } );

		// convert and save to file
		return converter.convert( bigWarpAffine );
	}

	private String getPixelTypeString( int targetImageBitDepth )
	{
		final String resultImagePixelType;
		if ( targetImageBitDepth == 8 )
			resultImagePixelType = ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_CHAR;
		else if ( targetImageBitDepth == 16 )
			resultImagePixelType = "unsigned short"; // TODO: put into itc-transforms
		else
			throw new UnsupportedOperationException( "No support for bit depth " + targetImageBitDepth );
		return resultImagePixelType;
	}

}

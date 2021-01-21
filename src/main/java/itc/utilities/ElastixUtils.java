package itc.utilities;

import itc.transforms.elastix.ElastixTransform;

public class ElastixUtils
{
	public static String getPixelTypeString( int targetImageBitDepth )
	{
		final String resultImagePixelType;
		if ( targetImageBitDepth == 8 )
			resultImagePixelType = ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_CHAR;
		else if ( targetImageBitDepth == 16 )
			resultImagePixelType = ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_SHORT;
		else
			throw new UnsupportedOperationException( "No support for bit depth " + targetImageBitDepth );
		return resultImagePixelType;
	}
}

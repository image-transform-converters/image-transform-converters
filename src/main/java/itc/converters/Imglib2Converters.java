package itc.converters;

import net.imglib2.realtransform.AffineTransform3D;

public class Imglib2Converters {

	public static String imglib2AffineTransform3DtoString( AffineTransform3D transform )
	{
		return transform.toString();
	}
}

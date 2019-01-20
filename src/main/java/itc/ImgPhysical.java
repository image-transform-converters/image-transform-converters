package itc;

import net.imglib2.FinalInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineTransform3D;

public class ImgPhysical
{
	public static final String MICROMETER = "micrometer";

	public final RealRandomAccessible img;
	public final RealInterval interval;
	public final String unit;

	public ImgPhysical( RealRandomAccessible img, RealInterval interval )
	{
		this( img, interval, MICROMETER);
	}

	public ImgPhysical( RealRandomAccessible img, RealInterval interval, String unit )
	{
		this.img = img;
		this.interval = interval;
		this.unit = unit;
	}

}

package itc.physicalimg;

import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccessible;

/**
 * A continuous image in a physical coordinate system.
 * 
 * Provides an image as a {@link RealRandomAccessible}, and spatial extents as a {@link RealInterval}, 
 * in specified physical units.
 *
 */
public class PhysicalImg<T>
{
	public static final String MICROMETER = "micrometer";

	public final RealRandomAccessible<T> img;
	public final RealInterval interval;
	public final String unit;

	public PhysicalImg( RealRandomAccessible<T> img, RealInterval interval )
	{
		this( img, interval, MICROMETER);
	}

	public PhysicalImg( RealRandomAccessible<T> img, RealInterval interval, String unit )
	{
		this.img = img;
		this.interval = interval;
		this.unit = unit;
	}

}

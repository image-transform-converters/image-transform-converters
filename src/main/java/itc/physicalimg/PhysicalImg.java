package itc.physicalimg;

import itc.utilities.CopyUtils;
import itc.utilities.TransformUtils;
import net.imglib2.*;
import net.imglib2.interpolation.randomaccess.ClampingNLinearInterpolatorFactory;
import net.imglib2.realtransform.InverseRealTransform;
import net.imglib2.realtransform.RealTransformRandomAccessible;
import net.imglib2.realtransform.RealViews;
import net.imglib2.realtransform.Scale;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.RandomAccessibleOnRealRandomAccessible;
import net.imglib2.view.Views;

import java.util.stream.DoubleStream;

/**
 * A continuous image in a physical coordinate system.
 * 
 * Provides an image as a {@link RealRandomAccessible}, and spatial extents as a {@link RealInterval}, 
 * in specified physical units.
 *
 */
public class PhysicalImg < T extends RealType< T > & NativeType< T > >
{
	public static final String MICROMETER = "micrometer";

	private final RealRandomAccessible<T> rra;
	private final RealInterval interval;
	private final String unit;

	public PhysicalImg( RealRandomAccessible<T> rra, RealInterval interval )
	{
		this( rra, interval, MICROMETER);
	}

	public PhysicalImg( RealRandomAccessible<T> rra, RealInterval interval, String unit )
	{
		this.rra = rra;
		this.interval = interval;
		this.unit = unit;
	}

	public RandomAccessibleInterval< T > raiView( double... resolution )
	{
		final FinalInterval interval = interval( resolution );
		final RandomAccessible< T > ra = raView( resolution );

		return Views.interval( ra, interval );
	}

	public FinalInterval interval( double... resolution )
	{
		final Scale scale = getScale( resolution );
		final FinalInterval interval = TransformUtils.transformRealIntervalExpand( this.interval, scale );
		return interval;
	}

	public Scale getScale( double... resolution )
	{
		return new Scale( DoubleStream.of( resolution ).map( r -> 1.0 / r ).toArray() );
	}

	public RandomAccessible< T > raView( double... resolution  )
	{
		final Scale scale = getScale( resolution );
		final RealRandomAccessible< T > scaledRRA = RealViews.transform( rra, scale );
		final RandomAccessibleOnRealRandomAccessible< T > raster = Views.raster( scaledRRA );

		return raster;
	}

	public PhysicalImg< T > copy( double... resolution )
	{
		final PhysicalImgFromDiscrete< T > copy = new PhysicalImgFromDiscrete<>(
				CopyUtils.copyAsArrayImg( raiView( resolution ) ),
				new Scale( resolution ),
				unit );

		return copy;
	}

	public RealRandomAccessible< T > getRRA()
	{
		return rra;
	}

	public RealInterval getInterval()
	{
		return interval;
	}

	public String getUnit()
	{
		return unit;
	}
}

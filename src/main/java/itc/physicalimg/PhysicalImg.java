package itc.physicalimg;

import itc.utilities.CopyUtils;
import itc.utilities.TransformUtils;
import net.imglib2.*;
import net.imglib2.realtransform.RealViews;
import net.imglib2.realtransform.Scale;
import net.imglib2.realtransform.Scale3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
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

	private final RandomAccessibleInterval<T> wrappedRAI;
	private final RealRandomAccessible<T> rra;
	private final RealInterval interval;
	private final String unit;

	public PhysicalImg( RealRandomAccessible<T> rra, RealInterval interval )
	{
		this( rra, interval, MICROMETER, null );
	}

	public PhysicalImg( RealRandomAccessible<T> rra,
						RealInterval interval,
						String unit,
						RandomAccessibleInterval<T> wrappedRAI )
	{
		this.rra = rra;
		this.interval = interval;
		this.unit = unit;
		this.wrappedRAI = wrappedRAI;
	}

	public RandomAccessibleInterval< T > raiView( double... spacing )
	{
		assert spacing.length == 3: "Input dimensions do not match or are not 3.";

		final FinalInterval interval = interval( spacing );
		final RandomAccessible< T > ra = raView( spacing );

		return Views.interval( ra, interval );
	}

//	public RandomAccessibleInterval< T > emptyArrayImg( double... spacing )
//	{
//		assert spacing.length == 3: "Input dimensions do not match or are not 3.";
//
//		final FinalInterval interval = interval( spacing );
//
//		final ArrayImg arrayImg = new ArrayImgFactory( rra.realRandomAccess().get() ).create( interval );
//
//		return arrayImg;
//	}

	private FinalInterval interval( double... spacing )
	{
		final Scale3D scale = getScaleTransform( spacing );
		final FinalInterval interval = TransformUtils.transformRealIntervalExpand( this.interval, scale );
		return interval;
	}

	private Scale3D getScaleTransform( double... spacing )
	{
		assert spacing.length == 3: "Input dimensions do not match or are not 3.";

		return new Scale3D( DoubleStream.of( spacing ).map( r -> 1.0 / r ).toArray() );
	}

	public RandomAccessible< T > raView( double... spacing  )
	{
		assert spacing.length == 3: "Input dimensions do not match or are not 3.";

		final Scale3D scale = getScaleTransform( spacing );
		final RealRandomAccessible< T > scaledRRA = RealViews.transform( rra, scale );
		final RandomAccessibleOnRealRandomAccessible< T > raster = Views.raster( scaledRRA );

		return raster;
	}

	public PhysicalImg< T > copy( double... spacing )
	{
		final PhysicalImgFromDiscrete< T > copy = new PhysicalImgFromDiscrete<>(
				CopyUtils.copyAsArrayImg( raiView( spacing ) ),
				new Scale( spacing ),
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

	public RandomAccessibleInterval< T > getWrappedRAI()
	{
		return wrappedRAI;
	}
}

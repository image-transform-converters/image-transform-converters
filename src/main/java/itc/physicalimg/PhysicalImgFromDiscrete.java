package itc.physicalimg;

import itc.utilities.TransformUtils;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.ClampingNLinearInterpolatorFactory;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.RealViews;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * An {@link PhysicalImg} derived from a discrete raster image, an interpolator
 * created by an {@link InterpolatorFactory} and an
 * {@link AffineGet} transformation from pixel to physical coordinates.
 * 
 */
public class PhysicalImgFromDiscrete < T extends RealType< T > & NativeType< T > >
		extends PhysicalImg<T> {

	private final AffineGet pixelToPhysical;

	public PhysicalImgFromDiscrete(
			final RandomAccessibleInterval<T> rai,
			final AffineGet pixelToPhysical,
			final String unit)
	{
		this( rai, pixelToPhysical, new ClampingNLinearInterpolatorFactory(), unit );
	}


	public PhysicalImgFromDiscrete(
			final RandomAccessibleInterval<T> rai,
			final AffineGet pixelToPhysical,
			final InterpolatorFactory<T, RandomAccessible<T>> interpolatorFactory,
			final String unit)
	{
		super( RealViews.transform( Views.interpolate( Views.extendZero(  rai ), interpolatorFactory), pixelToPhysical ) ,
				TransformUtils.transformRealInterval( rai, pixelToPhysical ),
				unit,
				rai );

		this.pixelToPhysical = pixelToPhysical;
	}

	public PhysicalImgFromDiscrete(
			final RandomAccessible<T> ra,
			final Interval interval,
			final AffineGet pixelToPhysical,
			final InterpolatorFactory<T, RandomAccessible<T>> interpolatorFactory,
			final String unit)
	{
		super(  RealViews.transform( Views.interpolate( ra, interpolatorFactory), pixelToPhysical ),
				TransformUtils.transformRealInterval( interval, pixelToPhysical ),
				unit,
				Views.interval(ra, interval) );

		this.pixelToPhysical = pixelToPhysical;
	}
	
}

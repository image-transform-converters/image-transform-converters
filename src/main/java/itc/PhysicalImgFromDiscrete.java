package itc;

import itc.utilities.TransformUtils;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.view.Views;

/**
 * An {@link PhysicalImg} derived from a discrete raster image, an interpolator
 * created by an {@link InterpolatorFactory} and an
 * {@link AffineGet} transformation from pixel to physical coordinates.
 * 
 */
public class PhysicalImgFromDiscrete<T> extends PhysicalImg<T> {

	public final AffineGet pixelToPhysical;
	public final RandomAccessibleInterval<T> imageRaw;

	public PhysicalImgFromDiscrete(final RandomAccessibleInterval<T> rai, final AffineGet pixelToPhysical,
			final InterpolatorFactory<T, RandomAccessible<T>> interpolatorFactory, final String unit) {

		super(Views.interpolate(rai, interpolatorFactory), TransformUtils.transformRealInterval(rai, pixelToPhysical),
				unit);

		this.imageRaw = rai;
		this.pixelToPhysical = pixelToPhysical;
	}

	public PhysicalImgFromDiscrete(final RandomAccessible<T> ra, final Interval interval, final AffineGet pixelToPhysical,
			final InterpolatorFactory<T, RandomAccessible<T>> interpolatorFactory, final String unit) {

		super(Views.interpolate(ra, interpolatorFactory),
				TransformUtils.transformRealInterval(interval, pixelToPhysical), unit);

		this.imageRaw = Views.interval(ra, interval);
		this.pixelToPhysical = pixelToPhysical;
	}
	
}

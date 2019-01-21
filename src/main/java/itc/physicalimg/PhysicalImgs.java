package itc.physicalimg;

import ij.ImagePlus;
import itc.utilities.TransformUtils;
import net.imglib2.FinalRealInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.ClampingNLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.InverseRealTransform;
import net.imglib2.realtransform.RealTransformRandomAccessible;
import net.imglib2.realtransform.RealViews;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * An {@link PhysicalImg} derived from a discrete raster image, an interpolator
 * created by an {@link InterpolatorFactory} and an {@link AffineGet}
 * transformation from pixel to physical coordinates.
 * 
 */
public class PhysicalImgs {

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends RealType<T> & NativeType<T>> PhysicalImg<T> fromImagePlus( ImagePlus ip )
	{

		PhysicalImgBuilder<?> builder;
		if (ip.getNSlices() < 2)
			builder = new PhysicalImgBuilder( ImageJFunctions.wrapReal( ip ) );
		else
			builder = new PhysicalImgBuilder( ImageJFunctions.wrapReal( ip ) );
		
		builder.spacing(
				ip.getCalibration().pixelWidth,
				ip.getCalibration().pixelHeight,
				ip.getCalibration().pixelDepth ) // safe to have pixelDepth even for 3d
			.origin(
				ip.getCalibration().xOrigin,
				ip.getCalibration().yOrigin,
				ip.getCalibration().zOrigin );
		
		return (PhysicalImg<T>) builder.wrap();
		
	}
	
	/**
	 * Build a {@link PhysicalImage} by zero extending and interpolating with the given interplator.
	 * 
	 * @param rai
	 * @param pixelToPhysical
	 * @param interpolatorFactory
	 * @param unit
	 * @return
	 */
	public static <T extends RealType<T> & NativeType<T>> PhysicalImg<T> zeroExtended(
			final RandomAccessibleInterval<T> rai, final AffineGet pixelToPhysical,
			final InterpolatorFactory<T, RandomAccessible<T>> interpolatorFactory, final String unit) {

		RealTransformRandomAccessible<T, InverseRealTransform> rra = RealViews
				.transform(Views.interpolate(Views.extendZero(rai), interpolatorFactory), pixelToPhysical);

		FinalRealInterval interval = TransformUtils.transformRealInterval(rai, pixelToPhysical);

		return new PhysicalImg<T>(rra, interval, pixelToPhysical, unit, rai);
	}

	/**
	 * Build a {@link PhysicalImage} by zero extending and (clamped) linear interpolation.
	 * @param rai
	 * @param pixelToPhysical
	 * @param unit
	 * @return
	 */
	public static <T extends RealType<T> & NativeType<T>> PhysicalImg<T> zeroLinear(
			final RandomAccessibleInterval<T> rai, final AffineGet pixelToPhysical, final String unit) {

		ClampingNLinearInterpolatorFactory<T> interpolatorFactory = new ClampingNLinearInterpolatorFactory<>();
		return zeroExtended( rai, pixelToPhysical, interpolatorFactory, unit );
	}

	/**
	 * Build a {@link PhysicalImage} by zero extending and nearest neighbor interpolation.
	 * 
	 * @param rai
	 * @param pixelToPhysical
	 * @param unit
	 * @return
	 */
	public static <T extends RealType<T> & NativeType<T>> PhysicalImg<T> zeroNearest(
			final RandomAccessibleInterval<T> rai, final AffineGet pixelToPhysical, final String unit) {

		NearestNeighborInterpolatorFactory<T> interpolatorFactory = new NearestNeighborInterpolatorFactory<>();
		return zeroExtended( rai, pixelToPhysical, interpolatorFactory, unit );
	}
	
}

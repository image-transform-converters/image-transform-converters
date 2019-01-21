package itc.physicalimg;

import java.util.function.Function;

import itc.utilities.TransformUtils;
import net.imglib2.FinalRealInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.ClampingNLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineTransform;
import net.imglib2.realtransform.Scale;
import net.imglib2.realtransform.Translation;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;


/**
 * A helper for creating {@link PhysicalImg}s. 
 * 
 * Chain method calls on this object to set parameters, then 'wrap'
 * a {@link RandomAccessibleInterval}. For example:
 * 
 * RandomAccessibleInterval<T> rai = ...
 * PhysicalImg phi = PhysicalImgBuilder
 * 		.extendValue( -1 )
 * 		.spacing( 0.1, 0.1, 0.5 )
 * 		.interpolateLinear().
 * 		.wrap( rai );
 *
 */
public class PhysicalImgBuilder<T extends RealType<T> & NativeType<T>> {

	/* Don't actually need these, at the moment, but maybe we will later */
//	public static final String EXTEND_NONE = "EXTEND_NONE";
//	public static final String EXTEND_ZERO = "EXTEND_ZERO";
//	public static final String EXTEND_VALUE = "EXTEND_VALUE";
//	public static final String EXTEND_BORDER = "EXTEND_BORDER";
//	public static final String EXTEND_MIRROR = "EXTEND_MIRROR";

//	private String extension = "zero";
//	private T extendValue;

	private InterpolatorFactory<T, RandomAccessible<T>> interpolator;
	private AffineGet pixelToPhysical;
	private String unit = PhysicalImg.MICROMETER;
	private Function<RandomAccessibleInterval<T>,RandomAccessible<T>> extender;
	private RandomAccessibleInterval<T> rai;

	private int nd;
	private AffineTransform tmpAffine;

	// TODO add axis permutation
	
	/**
	 * Generates a new PhysicalImg builder using ClampingLinearInterplation,
	 * pixelToPhysical as identity, and zero extension by default.
	 * 
	 */
	public PhysicalImgBuilder( RandomAccessibleInterval<T> rai ) {
		this( rai.numDimensions(), Util.getTypeFromInterval( rai ));
		this.rai = rai;
	}

	/**
	 * Generates a new PhysicalImg builder using ClampingLinearInterplation,
	 * pixelToPhysical as identity, and zero extension by default.
	 * 
	 */
	public PhysicalImgBuilder(int nd, T t ) {
		interpolator = new ClampingNLinearInterpolatorFactory<T>();

		this.nd = nd;
		if (nd == 2)
			tmpAffine = new AffineTransform( 2 );
		else if (nd == 3)
			tmpAffine = new AffineTransform( 3 );
		
		extendZero();
	}

	public PhysicalImgBuilder<T> extendValue(final T value) {
		extender = (x) -> Views.extendValue( x, value );
		return this;
	}

	public PhysicalImgBuilder<T> extendBorder( final T t) {
		extender = (x) -> Views.extendBorder( x );
		return this;
	}

	public PhysicalImgBuilder<T> extendZero() {
		extender = (x) -> Views.extendZero( x );
		return this;
	}

	public PhysicalImgBuilder<T> extendMirrorDouble() {
		extender = (x) -> Views.extendMirrorDouble( x );
		return this;
	}

	public PhysicalImgBuilder<T> extendMirrorSingle() {
		extender = (x) -> Views.extendMirrorSingle( x );
		return this;
	}

	public PhysicalImgBuilder<T> interpolateLinear() {
		interpolator = new ClampingNLinearInterpolatorFactory<T>();
		return this;
	}

	public PhysicalImgBuilder<T> interpolateNearest() {
		interpolator = new NearestNeighborInterpolatorFactory<T>();
		return this;
	}

	public PhysicalImgBuilder<T> interpolated(InterpolatorFactory<T, RandomAccessible<T>> interpolator) {
		this.interpolator = interpolator;
		return this;
	}

	public PhysicalImgBuilder<T> unit(final String unit) {
		this.unit = unit;
		return this;
	}

	public PhysicalImgBuilder<T> spacing(final double... spacing) {
		Scale xlate = new Scale( spacing );
		tmpAffine.preConcatenate(xlate);
		return this;
	}

//	/**
//	 * Indicates the origin in pixel space of this {@link PhysicalImg} goes to
//	 * the specified offset point in physical space. Overrides any translation
//	 * part of the current transform.
//	 * 
//	 * @param offset the location of the pixel origin in physical space
//	 * @return this
//	 */
//	public PhysicalImgBuilder<T> offset(final double... offset) {
//		for (int d = 0; d < nd; d++)
//			tmpAffine.set(offset[d], d, nd);
//
//		return this;
//	}

	/**
	 * Indicates the origin in pixel space of this {@link PhysicalImg} goes to
	 * the specified offset point in physical space. This is concatenated to any
	 * existing transformation.
	 *  
	 * @param origin the location of the pixel origin in physical space
	 * @return this
	 */
	public PhysicalImgBuilder<T> origin(final double... origin) {
		Translation xlate = new Translation( origin );
		tmpAffine.preConcatenate(xlate);
		return this;
	}

	/**
	 * Set the pixelToPhysicalTransformation. Overrides any existing part of the
	 * transform.
	 * 
	 * @param pixelToPhysical
	 * @return this
	 */
	public PhysicalImgBuilder<T> pixelToPhysical(final AffineGet pixelToPhysical) {

		for (int i = 0; i < nd - 1; i++)
			for (int j = 0; j < nd; j++)
				tmpAffine.set(pixelToPhysical.get(i, j), i, i);

		return this;
	}

	public PhysicalImg<T> wrap(final RandomAccessibleInterval<T> rai) {
		this.rai = rai;
		return wrap();
	}

	public PhysicalImg<T> wrap() {

		pixelToPhysical = TransformUtils.simplifyAffineGet( tmpAffine );

		RandomAccessible<T> extended = rai;
		if ( extender != null )
			extended = extender.apply( rai );
	
		RealRandomAccessible<T> interpolated = Views.interpolate( extended, interpolator );
		FinalRealInterval interval = TransformUtils.transformRealInterval(rai, pixelToPhysical);
	
		return new PhysicalImg<T>(interpolated, interval, pixelToPhysical, unit, rai);
	}

}

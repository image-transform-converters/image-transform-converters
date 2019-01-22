package itc.physicalimg;

import itc.utilities.*;
import net.imglib2.*;
import net.imglib2.realtransform.*;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.RandomAccessibleOnRealRandomAccessible;
import net.imglib2.view.Views;

/**
 * A continuous image in a physical coordinate system.
 * 
 * Provides an image as a {@link RealRandomAccessible}, and spatial extents as a {@link RealInterval}, 
 * in specified physical units.
 *
 */
public class PhysicalImg < T extends RealType< T > & NativeType< T > > implements RealRandomAccessible<T>
{
	public static final String MICROMETER = "micrometer";

	private final RandomAccessibleInterval<T> wrappedRAI;
	private final RealRandomAccessible<T> rra;
	private final RealInterval interval;
	private final AffineGet pixelToPhysical;
	private final String unit;
	
	private final PhysicalImgBuilder<T> builder; // builder used to create this PhysicalImage, if it exists

	public PhysicalImg( RealRandomAccessible<T> rra, RealInterval interval )
	{
		this( rra, interval, null, MICROMETER, null );
	}

	public PhysicalImg( RealRandomAccessible<T> rra, RealInterval interval, String unit )
	{
		this( rra, interval, null, unit, null );
	}

	public PhysicalImg( final RealRandomAccessible<T> rra,
						final RealInterval interval,
						final AffineGet pixelToPhysical,
						final String unit,
						final RandomAccessibleInterval<T> wrappedRAI,
						final PhysicalImgBuilder<T> builder )
	{
		this.rra = rra;
		this.interval = interval;
		this.pixelToPhysical = pixelToPhysical;
		this.unit = unit;
		this.wrappedRAI = wrappedRAI;
		this.builder = builder;
	}

	public PhysicalImg( final RealRandomAccessible<T> rra,
						final RealInterval interval,
						final AffineGet pixelToPhysical,
						final String unit,
						final RandomAccessibleInterval<T> wrappedRAI )
	{
		this.rra = rra;
		this.interval = interval;
		this.pixelToPhysical = pixelToPhysical;
		this.unit = unit;
		this.wrappedRAI = wrappedRAI;
		this.builder = null;
	}

	public RandomAccessibleInterval< T > raiView( )
	{
		return raiView( 1.0, 1.0, 1.0 );
	}
	
	/**
	 * Transform a point or scale in physical units to the discrete pixel units.
	 * 
	 * Useful when you need to write 
	 * 
	 * @param physicalPoint the physical point
	 * @return the point at the scale of pixels
	 */
	public double[] getInPixelUnits( double... physicalPoint )
	{
		// TODO this is wrong in general, 
		// but is fine for the tests i have right now
		// replace with detecting scale VERY SOON  -JB
		AffineGet scale = pixelToPhysical;

		double[] result = new double[ physicalPoint.length ];
		scale.applyInverse( result, physicalPoint );
		return result;
	}

	public RandomAccessibleInterval< T > raiView( double... spacing )
	{
		assert spacing.length == 3: "Input dimensions do not match or are not 3.";

		final FinalInterval pixelInterval = interval( spacing );
		final RandomAccessible< T > ra = raViewPixel( spacing );

		return Views.interval( ra, pixelInterval );
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

	/**
	 * Returns a discrete interval that describes the extents of this PhysicalImg
	 * in pixels, at the given spacing.
	 * 
	 * @param spacing the pixel spacing
	 * @return
	 */
	private FinalInterval interval( double... spacing )
	{
		final Scale3D scale = TransformUtils.getPhysicalToPixelScaleTransform3D( spacing );

		final FinalInterval pixelInterval =
				TransformUtils.transformRealIntervalExpand(
						this.interval,
						scale );

		return pixelInterval;
	}

	/**
	 * Returns a {@link RandomAccessible} in physical space.
	 * 
	 * @param spacing
	 * 			the spacing at which the output should be sampled
	 * @return an image in pixel space
	 */
	public RandomAccessible< T > raView()
	{
		return Views.raster( this );
	}

	/**
	 * Returns a {@link RandomAccessible} in pixel space, that samples this
	 * PhyiscalImg at the provided pixel spacing.
	 *  
	 * @param spacing the spacing at which the output should be sampled
	 * @return an image in pixel space
	 */
	public RandomAccessible< T > raViewPixel( double... spacing  )
	{
		assert spacing.length >= rra.numDimensions(): "Must provide at least as many spacing values as number of dimensions (" + rra.numDimensions() +")";

		final Scale3D scale = TransformUtils.getScaleTransform3D( spacing );
		final RealRandomAccessible< T > scaledRRA = RealViews.transform( rra, scale );
		final RandomAccessibleOnRealRandomAccessible< T > raster = Views.raster( scaledRRA );

		return raster;
	}

	/**
	 * Resamples this PhysicalImg over its interval at the specified pixel spacing 
	 * and returns a new PhysicalImg.  Creates a new {@link RandomAccessibleInterval}, 
	 * which can be easily written into.
	 * 
	 * @param spacing
	 * 			the pixel spacing of the output PhysicalImg
	 * @return the new PhysicalImg
	 */
	public PhysicalImg< T > copy( double... spacing )
	{
		RandomAccessibleInterval<T> rai = CopyUtils.copyAsArrayImg( raiView( spacing ) );
		
		if( builder != null )
			return builder.wrap(rai);
	
		// this is right if its correct to concatenate
		return new PhysicalImgBuilder<>( rai )
				.pixelToPhysical(pixelToPhysical)
				.unit( unit )
				.spacing( spacing )
				.wrap();
	}
	
	public PhysicalImg< T > emptyCopy( double... spacing )
	{
		RandomAccessibleInterval<T> rai = CopyUtils.createArrayImg( raiView( spacing ));
		
		if( builder != null )
			return builder.wrap(rai);
		
		// this is right if its correct to concatenate
		return new PhysicalImgBuilder<>( rai )
				.pixelToPhysical(pixelToPhysical)
				.unit( unit )
				.spacing( spacing )
				.wrap();
	}

	/**
	 * Copies the contents of this PhysicalImg into the  
	 * 
	 * @param other destination PhysicalImg
	 * @return
	 */
	public <S extends RealType<S> & NativeType<S>> void copyInto( PhysicalImg<S> other )
	{
		RealTransformRealRandomAccessible<T, InverseRealTransform>.RealTransformRealRandomAccess source = RealViews.transform( this, other.pixelToPhysical.inverse() ).realRandomAccess();
		Cursor<S> destinationCursor = Views.flatIterable( other.wrappedRAI ).cursor();
		while( destinationCursor.hasNext() )
		{
			destinationCursor.fwd();
			source.setPosition(destinationCursor);
			destinationCursor.get().setReal( source.get().getRealDouble());
		}
	}

	/**
	 * @return the real interval spanned by this PhysicalImg
	 */
	public RealInterval getInterval()
	{
		return interval;
	}

	/**
	 * @return the units for this PhysicalImg space
	 */
	public String getUnit()
	{
		return unit;
	}

	/**
	 * @return the {@link RandomAccessibleInterval} from which this PhysicalImg gets its intensities
	 */
	public RandomAccessibleInterval< T > getWrappedRAI()
	{
		return wrappedRAI;
	}

	@Override
	public int numDimensions() {
		return rra.numDimensions();
	}

	@Override
	public RealRandomAccess<T> realRandomAccess() {
		return rra.realRandomAccess();
	}

	@Override
	public RealRandomAccess<T> realRandomAccess(RealInterval interval) {
		return rra.realRandomAccess( interval );
	}
}

package itc.utilities;

import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.RealInterval;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.realtransform.RealViewsSimplifyUtils;
import net.imglib2.realtransform.Scale;
import net.imglib2.realtransform.Scale2D;
import net.imglib2.realtransform.Scale3D;
import net.imglib2.realtransform.ScaleAndTranslation;
import net.imglib2.realtransform.Translation;
import net.imglib2.realtransform.Translation2D;
import net.imglib2.realtransform.Translation3D;

import java.util.stream.DoubleStream;

public class TransformUtils {

	public static FinalRealInterval transformRealInterval(
			final RealInterval interval,
			final RealTransform xfm ) {

		int nd = interval.numDimensions();
		double[] pt = new double[nd];
		double[] min = new double[nd];
		double[] max = new double[nd];

		// transform min
		for (int d = 0; d < nd; d++)
			pt[d] = interval.realMin(d);

		xfm.apply(pt, min);

		// transform max
		for (int d = 0; d < nd; d++) {
			pt[d] = interval.realMax(d);
		}

		xfm.apply(pt, max);

		return new FinalRealInterval(min, max);
	}

	/**
	 * Returns the smallest discrete interval that contains 
	 * the given {@link RealInterval} after it is transformed by the give
	 * {@link RealTransform}.
	 * 
	 * @param interval the real interval
	 * @param xfm the transformation 
	 * @return the discrete interval
	 */
	public static FinalInterval transformRealIntervalExpand(
			final RealInterval interval,
			final RealTransform xfm ) {
		
		// TODO this could result in sub-pixel errors. Should address this
		
		// TODO make a similar method to this that returns the largest interval that is contained
		// 		by the transformed real interval?
		
		int nd = interval.numDimensions();
		FinalRealInterval realTransformedInterval = transformRealInterval(interval, xfm );

		long[] min = new long[ nd ];
		long[] max = new long[ nd ];
		for( int i = 0; i < nd; i++ )
		{
			min[ i ] = (long) Math.floor( realTransformedInterval.realMin( i ));
			max[ i ] = (long) Math.ceil( realTransformedInterval.realMax( i ));
		}
			
		return new FinalInterval( min, max );
	}

	public static FinalInterval transformInterval( final Interval interval, final RealTransform xfm ) {

		int nd = interval.numDimensions();
		double[] pt = new double[nd];
		double[] ptxfm = new double[nd];

		long[] min = new long[nd];
		long[] max = new long[nd];

		// transform min
		for (int d = 0; d < nd; d++)
			pt[d] = interval.min(d);

		xfm.apply(pt, ptxfm);
		copyToLongFloor(ptxfm, min);

		// transform max
		for (int d = 0; d < nd; d++) {
			pt[d] = interval.max(d);
		}

		xfm.apply(pt, ptxfm);
		copyToLongCeil(ptxfm, max);

		return new FinalInterval(min, max);
	}

	public static void copyToLongFloor(final double[] src, final long[] dst) {

		for (int d = 0; d < src.length; d++)
			dst[d] = (long) Math.floor(src[d]);
	}

	public static void copyToLongCeil(final double[] src, final long[] dst) {
		
		for (int d = 0; d < src.length; d++)
			dst[d] = (long) Math.ceil(src[d]);
	}

	public static Scale3D getScaleTransform3D( double... spacing )
	{
		assert spacing.length == 3: "Input dimensions do not match or are not 3.";

		return new Scale3D( spacing );
	}

	public static Scale3D getPhysicalToPixelScaleTransform3D( double... spacing )
	{
		assert spacing.length == 3: "Input dimensions do not match or are not 3.";

		return new Scale3D( DoubleStream.of( spacing ).map( s -> 1.0 / s ).toArray() );
	}

	/**
	 * Copied from {@link RealViewsSimplifyUtils} because it's private
	 * 
	 * @param affineGet a simplified affine
	 * @return
	 */
	public static AffineGet simplifyAffineGet( final AffineGet affineGet )
	{
		final int n = affineGet.numDimensions();

		if ( RealViewsSimplifyUtils.isExlusiveTranslation( affineGet ) )
		{
			final double[] translations = new double[ n ];

			for ( int d = 0; d < n; d++ )
			{
				translations[ d ] = affineGet.get( d, n );
			}

			if ( n == 2 )
			{
				return new Translation2D( translations );
			}
			else if ( n == 3 )
			{
				return new Translation3D( translations );
			}
			else
			{
				return new Translation( translations );
			}
		}
		else if ( RealViewsSimplifyUtils.isExclusiveScale( affineGet ) )
		{

			final double[] scalings = new double[ n ];

			for ( int d = 0; d < n; d++ )
			{
				scalings[ d ] = affineGet.get( d, d );
			}

			if ( n == 2 )
			{
				return new Scale2D( scalings );
			}
			else if ( n == 3 )
			{
				return new Scale3D( scalings );
			}
			else
			{
				return new Scale( scalings );
			}
		}
		else if ( RealViewsSimplifyUtils.isExclusiveScaleAndTranslation( affineGet ) )
		{
			final double[] s = new double[ n ];
			final double[] t = new double[ n ];
			for ( int d = 0; d < n; d++ )
			{
				t[ d ] = affineGet.get( d, n );
				s[ d ] = affineGet.get( d, d );
			}

			return new ScaleAndTranslation( t, s );

		}
		return ( AffineGet ) affineGet.copy();
	}
}

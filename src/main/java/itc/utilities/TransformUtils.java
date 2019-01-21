package itc.utilities;

import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.RealInterval;
import net.imglib2.realtransform.RealTransform;

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

	public static FinalInterval transformRealIntervalExpand(
			final RealInterval interval,
			final RealTransform xfm ) {
		
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
}

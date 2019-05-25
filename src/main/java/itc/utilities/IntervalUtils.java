package itc.utilities;

import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.RealInterval;
import net.imglib2.util.Intervals;

import java.util.Arrays;

public class IntervalUtils
{
	public static RealInterval toCalibratedRealInterval(
			Interval interval,
			double calibration )
	{
		final double[] calibrations = new double[ interval.numDimensions() ];
		Arrays.fill( calibrations, calibration);

		return toCalibratedRealInterval( interval, calibrations )
	}

	public static RealInterval toCalibratedRealInterval(
			Interval interval,
			double[] calibrations )
	{
		final double[] min = Intervals.minAsDoubleArray( interval );
		final double[] max = Intervals.maxAsDoubleArray( interval );

		final int numDimensions = min.length;
		for ( int d = 0; d < numDimensions; d++ )
		{
			min[ d ] *= calibrations[ d ];
			max[ d ] *= calibrations[ d ];
		}

		return new FinalRealInterval( min, max );
	}


	public static RealInterval scale(
			RealInterval realInterval,
			double scale )
	{
		final double[] scales = new double[ realInterval.numDimensions() ];
		Arrays.fill( scales, scale);

		return scale( realInterval, scales );
	}

	public static RealInterval scale(
			RealInterval realInterval,
			double[] scale )
	{
		final double[] min = Intervals.minAsDoubleArray( realInterval );
		final double[] max = Intervals.maxAsDoubleArray( realInterval );

		final int numDimensions = min.length;
		for ( int d = 0; d < numDimensions; d++ )
		{
			min[ d ] *= scale[ d ];
			max[ d ] *= scale[ d ];
		}

		return new FinalRealInterval( min, max );
	}

}

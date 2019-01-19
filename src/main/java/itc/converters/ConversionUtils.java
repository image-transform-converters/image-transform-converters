package itc.converters;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.util.Intervals;

import java.util.stream.LongStream;

public class ConversionUtils
{
	public static double[] imageCenter( RandomAccessibleInterval image )
	{
		final long[] dimensions = Intervals.dimensionsAsLongArray( image );
		return LongStream.of( dimensions ).mapToDouble( l -> l / 2.0 ).toArray();
	}
}

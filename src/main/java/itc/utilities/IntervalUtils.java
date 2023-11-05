/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2023 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package itc.utilities;

import mpicbg.spim.data.sequence.VoxelDimensions;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.RealInterval;
import net.imglib2.util.Intervals;

import java.util.Arrays;

import static net.imglib2.util.LinAlgHelpers.rows;

public class IntervalUtils
{
	public static RealInterval toCalibratedRealInterval(
			Interval interval,
			double voxelSpacing )
	{
		final double[] calibrations = new double[ interval.numDimensions() ];
		Arrays.fill( calibrations, voxelSpacing);

		return toCalibratedRealInterval( interval, calibrations );
	}

	public static RealInterval toCalibratedRealInterval(
			Interval interval,
			double[] voxelSpacings )
	{
		final double[] min = Intervals.minAsDoubleArray( interval );
		final double[] max = Intervals.maxAsDoubleArray( interval );

		final int numDimensions = min.length;
		for ( int d = 0; d < numDimensions; d++ )
		{
			min[ d ] *= voxelSpacings[ d ];
			max[ d ] *= voxelSpacings[ d ];
		}

		return new FinalRealInterval( min, max );
	}

	public static RealInterval toCalibratedRealInterval(
			Interval interval,
			VoxelDimensions voxelDimensions )
	{
		final double[] min = Intervals.minAsDoubleArray( interval );
		final double[] max = Intervals.maxAsDoubleArray( interval );

		final int numDimensions = min.length;
		for ( int d = 0; d < numDimensions; d++ )
		{
			min[ d ] *= voxelDimensions.dimension( d );
			max[ d ] *= voxelDimensions.dimension( d );
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

	public static String toString( RealInterval realInterval )
	{
		int n = realInterval.numDimensions();
		double[] min = Intervals.minAsDoubleArray( realInterval );
		double[] max = Intervals.maxAsDoubleArray( realInterval );

		final StringBuilder sb = new StringBuilder();

		final String className = realInterval.getClass().getSimpleName();
//		sb.append( className );

		sb.append( " (" );
		for ( int d = 0; d < n; d++ )
		{
			sb.append( min[ d ] );
			if ( d < n - 1 )
				sb.append( ", " );
		}
		sb.append( ") -- (" );
		for ( int d = 0; d < n; d++ )
		{
			sb.append( max[ d ] );
			if ( d < n - 1 )
				sb.append( ", " );
		}
		sb.append( ")" );

		return sb.toString();
	}

}

package itc.transforms.imglib2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.algorithm.interpolation.randomaccess.BSplineInterpolatorFactory;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

/**
 * Displacement field transform using ImgLib2's BSplineInterpolatorFactory.
 */
public class BSplineDisplacementFieldImgLib2BSpline implements RealTransform
{
	private final int numDimensions;
	private final List< net.imglib2.RandomAccessibleInterval< DoubleType > > coefficients;
	private final double[] gridSpacing;
	private final double[] gridOffset;
	private final int splineOrder;
	private final List< RealRandomAccessible< DoubleType > > interpolated;

	public BSplineDisplacementFieldImgLib2BSpline(
			final int numDimensions,
			final List< net.imglib2.RandomAccessibleInterval< DoubleType > > coefficients,
			final double[] gridSpacing,
			final double[] gridOffset,
			final int splineOrder )
	{
		this.numDimensions = numDimensions;
		this.coefficients = new ArrayList<>( coefficients );
		this.gridSpacing = gridSpacing.clone();
		this.gridOffset = gridOffset.clone();
		this.splineOrder = splineOrder;
		this.interpolated = new ArrayList<>( coefficients.size() );
		for ( final net.imglib2.RandomAccessibleInterval< DoubleType > coefficient : coefficients )
		{
			final RealRandomAccessible< DoubleType > interpolator = Views.interpolate(
					Views.extendBorder( coefficient ),
					new BSplineInterpolatorFactory<>( splineOrder )
			);
			interpolated.add( interpolator );
		}
	}

	@Override
	public int numSourceDimensions()
	{
		return numDimensions;
	}

	@Override
	public int numTargetDimensions()
	{
		return numDimensions;
	}

	@Override
	public void apply( final double[] source, final double[] target )
	{
		final double[] indexCoordinates = new double[ numDimensions ];
		for ( int d = 0; d < numDimensions; d++ )
			indexCoordinates[ d ] = ( source[ d ] - gridOffset[ d ] ) / gridSpacing[ d ];

		for ( int c = 0; c < numDimensions; c++ )
		{
			final RealRandomAccess< DoubleType > access = interpolated.get( c ).realRandomAccess();
			access.setPosition( indexCoordinates );
			target[ c ] = source[ c ] + access.get().getRealDouble();
		}
	}

	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		final double[] sourceArray = new double[ numDimensions ];
		source.localize( sourceArray );
		final double[] targetArray = new double[ numDimensions ];
		apply( sourceArray, targetArray );
		target.setPosition( targetArray );
	}

	@Override
	public RealTransform copy()
	{
		return new BSplineDisplacementFieldImgLib2BSpline(
				numDimensions,
				coefficients,
				Arrays.copyOf( gridSpacing, gridSpacing.length ),
				Arrays.copyOf( gridOffset, gridOffset.length ),
				splineOrder
		);
	}
}


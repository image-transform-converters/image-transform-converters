package itc.transforms.imglib2;

import java.util.List;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.realtransform.*;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * A displacement field stored as BSpline coefficients.
 *
 * @author John Bogovic
 *
 * @param <T>
 */
public class BSplineDisplacementField< T extends RealType<T> > implements RealTransform
{
	private final int numDimensions;

	private final double[] gridOffset;

	private final double[] gridSpacing;

	private final DisplacementFieldTransform dfield;

	public BSplineDisplacementField(
			final int numDimensions,
			final List<RandomAccessibleInterval<T>> coefficients,
			final double[] gridSpacing,
			final double[] gridOffset )
	{
		assert ( numDimensions == coefficients.size() );
		this.numDimensions = numDimensions;

		this.gridOffset = gridOffset;
		this.gridSpacing = gridSpacing;

		// Stack coefficient images and move the new stack dimension to 0 so
		// components are interleaved as required by DisplacementFieldTransform.
		final RandomAccessibleInterval< T > interleavedCoefficients =
				Views.moveAxis( Views.stack( coefficients ), numDimensions, 0 );

		if( gridSpacing != null && gridOffset != null )
		{
			dfield = new DisplacementFieldTransform( interleavedCoefficients, gridSpacing, gridOffset );
		}
		else if( gridSpacing != null )
		{
			dfield = new DisplacementFieldTransform( interleavedCoefficients, gridSpacing );
		}
		else if( gridOffset != null )
		{
			dfield = new DisplacementFieldTransform( interleavedCoefficients, new Translation( gridOffset ) );
		}
		else
		{
			dfield = new DisplacementFieldTransform( interleavedCoefficients );
		}
	}

	public BSplineDisplacementField(
			final int numDimensions,
			DisplacementFieldTransform dfield )
	{
		this.numDimensions = numDimensions;
		this.gridOffset = null;
		this.gridSpacing = null;
		this.dfield = dfield;
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
	public void apply( double[] source, double[] target )
	{
		dfield.apply( source, target );
	}

	@Override
	public void apply( RealLocalizable source, RealPositionable target )
	{
		dfield.apply( source, target );
	}

	@Override
	public RealTransform copy()
	{
		return new BSplineDisplacementField<>( numDimensions, ( DisplacementFieldTransform ) dfield.copy() );
	}

}

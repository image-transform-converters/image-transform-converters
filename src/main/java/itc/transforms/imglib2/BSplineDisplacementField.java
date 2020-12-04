package itc.transforms.imglib2;

import java.util.List;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.algorithm.interpolation.randomaccess.BSplineCoefficientsInterpolatorFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.DeformationFieldTransform;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.realtransform.RealViews;
import net.imglib2.realtransform.Scale;
import net.imglib2.realtransform.ScaleAndTranslation;
import net.imglib2.realtransform.Translation;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
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
	// TODO migrate to imglib2-algorithm or imglib2-realtransform?

	private final int numDimensions;

	private final double[] gridOffset;

	private final double[] gridSpacing;

	private final DeformationFieldTransform< T > dfield;

	@SuppressWarnings( "unchecked" )
	public BSplineDisplacementField( 
			final int numDimensions, 
			final int order, 
			final List<RandomAccessibleInterval<T>> coefficients,
			final boolean clipping,
			final OutOfBoundsFactory<? extends RealType<?>, ?> oobFactory,
			final double[] gridSpacing,
			final double[] gridOffset )
	{
		assert ( numDimensions == coefficients.size() );
		this.numDimensions = numDimensions;

		this.gridOffset = gridOffset;
		this.gridSpacing = gridSpacing;

		@SuppressWarnings( "rawtypes" )
		RealRandomAccessible[] defAccesses = new RealRandomAccessible[ numDimensions ];
		for( int i = 0; i < numDimensions; i++ )
		{
			AffineGet pixelToPhysical = null;
			if( gridSpacing != null && gridOffset != null )
			{
				pixelToPhysical = new ScaleAndTranslation( gridSpacing, gridOffset );
			}
			else if( gridSpacing != null )
			{
				pixelToPhysical = new Scale( gridSpacing ); 
			}
			else if( gridOffset != null )
			{
				pixelToPhysical = new Translation( gridOffset ); 
			}

			BSplineCoefficientsInterpolatorFactory<T,T> interp = new BSplineCoefficientsInterpolatorFactory<>( 
					 coefficients.get( i ), order, clipping, oobFactory );

			if( pixelToPhysical != null )
			{
				defAccesses[ i ] = RealViews.affine( 
						Views.interpolate( coefficients.get( i ), interp ),
						pixelToPhysical );
			}
			else
			{
				defAccesses[ i ] = Views.interpolate( coefficients.get( i ), interp );
			}
		}

		dfield = new DeformationFieldTransform<T>( defAccesses );
	}

	public BSplineDisplacementField(
			final int numDimensions,
			DeformationFieldTransform<T> dfield )
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

	@SuppressWarnings( "unchecked" )
	@Override
	public RealTransform copy()
	{
		return new BSplineDisplacementField<>( numDimensions, ( DeformationFieldTransform< T > ) dfield.copy() );
	}

}

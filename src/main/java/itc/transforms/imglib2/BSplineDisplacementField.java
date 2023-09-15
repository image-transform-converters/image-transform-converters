package itc.transforms.imglib2;

import java.util.List;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.algorithm.interpolation.randomaccess.BSplineCoefficientsInterpolatorFactory;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.DisplacementFieldTransform;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.realtransform.RealViews;
import net.imglib2.realtransform.Scale;
import net.imglib2.realtransform.ScaleAndTranslation;
import net.imglib2.realtransform.Translation;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import net.imglib2.view.composite.Composite;
import net.imglib2.view.composite.CompositeIntervalView;
import net.imglib2.view.composite.RealComposite;
import net.imglib2.view.composite.RealStackCompositeView;

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

	private final DisplacementFieldTransform dfield;

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

		final RealRandomAccessible<RealComposite<T>> vectors = new RealStackCompositeView<T>( defAccesses );
		dfield = new DisplacementFieldTransform( vectors );
	}

	public BSplineDisplacementField(
			final int numDimensions,
			final DisplacementFieldTransform dfield )
	{
		this.numDimensions = numDimensions;
		this.gridOffset = null;
		this.gridSpacing = null;
		this.dfield = dfield;
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
		return new BSplineDisplacementField<>( numDimensions, ( DisplacementFieldTransform ) dfield.copy() );
	}
	
	private static < T extends RealType< T > > RealRandomAccessible< ? extends RealLocalizable > convertToComposite(
			final RandomAccessibleInterval< T > position )
	{
		final CompositeIntervalView< T, RealComposite< T > > collapsedFirst =
				Views.collapseReal(
						Views.moveAxis( position, 0, position.numDimensions() - 1 ) );

		return Views.interpolate(
				Views.extendBorder( collapsedFirst ),
				new NLinearInterpolatorFactory<>() );
	}
	
	public static class BSplineCompositeInterpolator<T extends RealType<T>, S extends Composite<T>> implements InterpolatorFactory< T, RandomAccessible< S > > {

		@Override
		public RealRandomAccess<T> create(RandomAccessible<S> f) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public RealRandomAccess<T> create(RandomAccessible<S> f, RealInterval interval) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}


}

package itc.converters;

import java.util.ArrayList;
import java.util.Arrays;

import itc.transforms.elastix.ElastixBSplineTransform;
import itc.transforms.imglib2.BSplineDisplacementField;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.type.numeric.real.DoubleType;

public class ElastixBSplineToBSplineRealTransform
{

	public static BSplineDisplacementField<DoubleType> convert( final ElastixBSplineTransform elastixBSplineTransform )
	{
		final int nd = elastixBSplineTransform.FixedImageDimension;

		// this factor compensates for the fact that the bspline kernel used by the bspline implementation in
		// imglib2-algorithm uses normalized kernels and elastix outputs coefficients expecting use of
		// un-normalized kernels

		// these factors are specific to third-order bsplines
		final double factor;
		if( nd == 3 )
			factor = 1 / ( 6.0 * 6.0 * 6.0 );
		else if( nd == 2 )
			factor = 1 / ( 6.0 * 6.0 );
		else
		{
			System.err.println( "bspline transforms only implemeneted for 2d or 3d.");
			return null;
		}

		ArrayList<RandomAccessibleInterval<DoubleType>> coefficients = new ArrayList<>( nd );
		for( int i = 0; i < nd; i++ )
			coefficients.add(elastixBSplineTransform.getBSplineCoefficients( i, factor ));

		BSplineDisplacementField<DoubleType> transform = new BSplineDisplacementField<>( 
				nd, 
				elastixBSplineTransform.BSplineTransformSplineOrder.intValue(),
				coefficients,
				true, 
				new OutOfBoundsConstantValueFactory<DoubleType, RandomAccessibleInterval<DoubleType>>( new DoubleType( 0 )),
				Arrays.stream( elastixBSplineTransform.GridSpacing ).mapToDouble( x -> x ).toArray(),
				Arrays.stream( elastixBSplineTransform.GridOrigin ).mapToDouble( x -> x ).toArray());

		return transform;
	}

}
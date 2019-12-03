package itc.converters;

import java.util.ArrayList;
import java.util.Arrays;

import itc.transforms.elastix.ElastixBSplineTransform;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.realtransform.BSplineRealTransform;
import net.imglib2.type.numeric.real.DoubleType;

public class ElastixBSplineToBSplineRealTransform
{

	public static BSplineRealTransform<DoubleType> convert( final ElastixBSplineTransform elastixBSplineTransform ) {
		final int nd = elastixBSplineTransform.FixedImageDimension;
		ArrayList<RandomAccessibleInterval<DoubleType>> coefficients = new ArrayList<>( 3 );
		for( int i = 0; i < nd; i++ )
			coefficients.add(elastixBSplineTransform.getBSplineCoefficients(i));

		BSplineRealTransform<DoubleType> transform = new BSplineRealTransform<DoubleType>(
				nd, 
				elastixBSplineTransform.BSplineTransformSplineOrder.intValue(),
				coefficients,
				Arrays.stream( elastixBSplineTransform.GridSpacing ).mapToDouble( x -> x ).toArray(),
				Arrays.stream( elastixBSplineTransform.GridOrigin ).mapToDouble( x -> x ).toArray());

		return transform;
	}

}
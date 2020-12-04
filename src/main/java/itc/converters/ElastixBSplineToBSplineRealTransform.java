package itc.converters;

import java.util.ArrayList;
import java.util.Arrays;

import itc.transforms.elastix.ElastixBSplineTransform;
import itc.transforms.imglib2.BSplineDisplacementField;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.DoubleType;

public class ElastixBSplineToBSplineRealTransform
{

	public static BSplineDisplacementField<DoubleType> convert( final ElastixBSplineTransform elastixBSplineTransform ) {
		final int nd = elastixBSplineTransform.FixedImageDimension;
		ArrayList<RandomAccessibleInterval<DoubleType>> coefficients = new ArrayList<>( 3 );
		for( int i = 0; i < nd; i++ )
			coefficients.add(elastixBSplineTransform.getBSplineCoefficients(i));

		BSplineDisplacementField<DoubleType> transform = new BSplineDisplacementField<>( 
				nd, 
				elastixBSplineTransform.BSplineTransformSplineOrder.intValue(),
				coefficients,
				true, 
				null,
				Arrays.stream( elastixBSplineTransform.GridSpacing ).mapToDouble( x -> x ).toArray(),
				Arrays.stream( elastixBSplineTransform.GridOrigin ).mapToDouble( x -> x ).toArray());

		return transform;
	}

}
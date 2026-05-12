package itc.converters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import itc.transforms.elastix.ElastixBSplineTransform;
import itc.transforms.elastix.ElastixTransform;
import itc.transforms.imglib2.BSplineDisplacementField;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.DoubleType;

public class ElastixBSplineToBSplineRealTransform
{
	public static BSplineDisplacementField< DoubleType > loadAndConvert( final File transformParametersFile ) throws IOException
	{
		final ElastixTransform elastixTransform = ElastixTransform.load( transformParametersFile );
		return convert( elastixTransform );
	}

	public static BSplineDisplacementField< DoubleType > convert( final ElastixTransform elastixTransform )
	{
		if ( ! ( elastixTransform instanceof ElastixBSplineTransform ) )
			throw new UnsupportedOperationException( "Expected BSplineTransform but got: " + elastixTransform.Transform );

		return convert( ( ElastixBSplineTransform ) elastixTransform );
	}

	public static BSplineDisplacementField<DoubleType> convert( final ElastixBSplineTransform elastixBSplineTransform )
	{
		final int nd = elastixBSplineTransform.FixedImageDimension;
		if ( nd != 2 && nd != 3 )
		{
			System.err.println( "bspline transforms only implemented for 2d or 3d." );
			return null;
		}

		ArrayList<RandomAccessibleInterval<DoubleType>> coefficients = new ArrayList<>( nd );
		for( int i = 0; i < nd; i++ )
			coefficients.add( elastixBSplineTransform.getBSplineCoefficients( i ) );

		BSplineDisplacementField<DoubleType> transform = new BSplineDisplacementField<>(
				nd,
				coefficients,
				Arrays.stream( elastixBSplineTransform.GridSpacing ).mapToDouble( x -> x ).toArray(),
				Arrays.stream( elastixBSplineTransform.GridOrigin ).mapToDouble( x -> x ).toArray());

		return transform;
	}

}
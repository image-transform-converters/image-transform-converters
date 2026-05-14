package itc.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import itc.transforms.elastix.ElastixBSplineTransform;
import itc.transforms.imglib2.BSplineDisplacementField;
import itc.transforms.imglib2.BSplineDisplacementFieldCustomBSpline;
import itc.transforms.imglib2.BSplineDisplacementFieldImgLib2BSpline;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.type.numeric.real.DoubleType;

public class ElastixBSplineToBSplineRealTransform
{
	public enum InterpolationMode
	{
		LINEAR,
		IMGLIB2_BSPLINE,
		CUSTOM_BSPLINE
	}

	public static BSplineDisplacementField< DoubleType > convert( final ElastixBSplineTransform elastixBSplineTransform )
	{
		return convertLinear( elastixBSplineTransform );
	}

	public static RealTransform convert( final ElastixBSplineTransform elastixBSplineTransform, final InterpolationMode mode )
	{
		switch ( mode )
		{
			case LINEAR:
				return convertLinear( elastixBSplineTransform );
			case IMGLIB2_BSPLINE:
				return convertImgLib2BSpline( elastixBSplineTransform );
			case CUSTOM_BSPLINE:
				return convertCustomBSpline( elastixBSplineTransform );
			default:
				throw new IllegalArgumentException( "Unsupported interpolation mode: " + mode );
		}
	}

	private static BSplineDisplacementField< DoubleType > convertLinear( final ElastixBSplineTransform elastixBSplineTransform )
	{
		final int nd = elastixBSplineTransform.FixedImageDimension;
		return new BSplineDisplacementField<>( nd, coefficients( elastixBSplineTransform ), spacing( elastixBSplineTransform ), origin( elastixBSplineTransform ) );
	}

	private static RealTransform convertImgLib2BSpline( final ElastixBSplineTransform elastixBSplineTransform )
	{
		final int nd = elastixBSplineTransform.FixedImageDimension;
		return new BSplineDisplacementFieldImgLib2BSpline(
				nd,
				coefficients( elastixBSplineTransform ),
				spacing( elastixBSplineTransform ),
				origin( elastixBSplineTransform ),
				splineOrder( elastixBSplineTransform )
		);
	}

	private static RealTransform convertCustomBSpline( final ElastixBSplineTransform elastixBSplineTransform )
	{
		final int nd = elastixBSplineTransform.FixedImageDimension;
		return new BSplineDisplacementFieldCustomBSpline(
				nd,
				coefficients( elastixBSplineTransform ),
				spacing( elastixBSplineTransform ),
				origin( elastixBSplineTransform ),
				splineOrder( elastixBSplineTransform )
		);
	}

	private static List< RandomAccessibleInterval< DoubleType > > coefficients( final ElastixBSplineTransform elastixBSplineTransform )
	{
		final int nd = elastixBSplineTransform.FixedImageDimension;
		final ArrayList< RandomAccessibleInterval< DoubleType > > coefficients = new ArrayList<>( nd );
		for ( int i = 0; i < nd; i++ )
			coefficients.add( elastixBSplineTransform.getBSplineCoefficients( i ) );
		return coefficients;
	}

	private static double[] spacing( final ElastixBSplineTransform elastixBSplineTransform )
	{
		return Arrays.stream( elastixBSplineTransform.GridSpacing ).mapToDouble( x -> x ).toArray();
	}

	private static double[] origin( final ElastixBSplineTransform elastixBSplineTransform )
	{
		return Arrays.stream( elastixBSplineTransform.GridOrigin ).mapToDouble( x -> x ).toArray();
	}

	private static int splineOrder( final ElastixBSplineTransform elastixBSplineTransform )
	{
		return elastixBSplineTransform.BSplineTransformSplineOrder == null ? 3 : elastixBSplineTransform.BSplineTransformSplineOrder;
	}
}
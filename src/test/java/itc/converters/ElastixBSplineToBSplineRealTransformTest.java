package itc.converters;

import itc.converters.ElastixBSplineToBSplineRealTransform.InterpolationMode;
import itc.transforms.elastix.ElastixBSplineTransform;
import itc.transforms.elastix.ElastixTransform;
import junit.framework.TestCase;
import net.imglib2.realtransform.RealTransform;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

public class ElastixBSplineToBSplineRealTransformTest extends TestCase
{
	public void testTransformixReferencePointFromTransformParametersBSpline3DTest() throws Exception
	{
		final URL transformUrl = ElastixBSplineToBSplineRealTransformTest.class
				.getResource( "/elastix/TransformParameters.BSpline3D.Test.txt" );
		assertNotNull( "Transform resource not found", transformUrl );

		final ElastixTransform elastixTransform = ElastixTransform.load( new File( transformUrl.toURI() ) );
		assertTrue( "Expected ElastixBSplineTransform", elastixTransform instanceof ElastixBSplineTransform );
		final ElastixBSplineTransform bsplineTransform = ( ElastixBSplineTransform ) elastixTransform;

		final double[][] inputPoints = new double[][] {
				{ 3034.000000, 3679.000000, 758.000000 },
				{ 4000.000000, 4000.000000, 800.000000 }
		};
		final double[][] expectedOutputs = new double[][] {
				{ 3020.242585, 3730.345593, 707.269575 },
				{ 4083.332369, 3889.026534, 798.189004 }
		};

		for ( final InterpolationMode mode : InterpolationMode.values() )
		{
			final RealTransform transform = ElastixBSplineToBSplineRealTransform.convert( bsplineTransform, mode );
			assertNotNull( "Converted transform should not be null", transform );
			assertEquals( 3, transform.numSourceDimensions() );
			assertEquals( 3, transform.numTargetDimensions() );

			for ( int p = 0; p < inputPoints.length; p++ )
			{
				final double[] inputPoint = inputPoints[ p ];
				final double[] expectedOutput = expectedOutputs[ p ];
				final double[] outputPoint = new double[ 3 ];
				transform.apply( inputPoint, outputPoint );

				final double[] delta = new double[ 3 ];
				double maxAbsDelta = 0.0;
				for ( int d = 0; d < 3; d++ )
				{
					delta[ d ] = expectedOutput[ d ] - outputPoint[ d ];
					maxAbsDelta = Math.max( maxAbsDelta, Math.abs( delta[ d ] ) );
				}

				System.out.println( "Mode: " + mode + ", pointIndex=" + p );
				System.out.println( "Input: " + Arrays.toString( inputPoint ) );
				System.out.println( "Expected output: " + Arrays.toString( expectedOutput ) );
				System.out.println( "Actual output: " + Arrays.toString( outputPoint ) );
				System.out.println( "Delta: " + Arrays.toString( delta ) );
				System.out.println( "Max abs delta: " + maxAbsDelta );

				assertTrue( "Reference-point error should stay bounded for " + mode + " at point " + p, maxAbsDelta < 80.0 );
			}
		}
	}
}
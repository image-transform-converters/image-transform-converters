package itc.converters;

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

		final RealTransform transform = ElastixBSplineToBSplineRealTransform.convert( ( ElastixBSplineTransform ) elastixTransform );
		assertNotNull( "Converted transform should not be null", transform );
		assertEquals( 3, transform.numSourceDimensions() );
		assertEquals( 3, transform.numTargetDimensions() );

		final double[] inputPoint = new double[] { 3034.000000, 3679.000000, 758.000000 };
		final double[] outputPoint = new double[ 3 ];
		transform.apply( inputPoint, outputPoint );

		final double[] expectedOutput = new double[] { 3020.242585, 3730.345593, 707.269575 };

        System.out.println( "Input: " + Arrays.toString( inputPoint ) );
        System.out.println( "Expected output: " + Arrays.toString( expectedOutput ) );
        System.out.println( "Actual output: " + Arrays.toString( outputPoint ) );

        assertEquals( expectedOutput[ 0 ], outputPoint[ 0 ], 1e-3 );
		assertEquals( expectedOutput[ 1 ], outputPoint[ 1 ], 1e-3 );
		assertEquals( expectedOutput[ 2 ], outputPoint[ 2 ], 1e-3 );
	}
}
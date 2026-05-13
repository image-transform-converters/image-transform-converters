package itc.examples;

import itc.converters.ElastixBSplineToBSplineRealTransform;
import itc.transforms.elastix.ElastixBSplineTransform;
import itc.transforms.elastix.ElastixBSplineTransform2D;
import itc.transforms.elastix.ElastixBSplineTransform3D;
import itc.transforms.elastix.ElastixTransform;
import net.imglib2.realtransform.RealTransform;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TransformPointsWithElastixBSplineTest
{
	@Test
	public void testConstant2DTranslationFromResources() throws Exception
	{
		final URL transformUrl = TransformPointsWithElastixBSplineTest.class
				.getResource( "/elastix/TransformParameters.BSpline2D.TranslationX.txt" );
		Assert.assertNotNull( "Transform resource not found", transformUrl );

		final ElastixTransform elastixTransform = ElastixTransform.load( new File( transformUrl.toURI() ) );
		Assert.assertTrue( elastixTransform instanceof ElastixBSplineTransform2D );

		final RealTransform transform = ElastixBSplineToBSplineRealTransform
				.convert( ( ElastixBSplineTransform ) elastixTransform );
		Assert.assertNotNull( "Converted transform should not be null", transform );
		Assert.assertEquals( 2, transform.numSourceDimensions() );
		Assert.assertEquals( 2, transform.numTargetDimensions() );

		final List< double[] > points = readPointsResource( "/elastix/Points.2D.txt", 2 );
		final double[] target = new double[ 2 ];
		for ( final double[] point : points )
		{
			transform.apply( point, target );
			Assert.assertEquals( point[ 0 ] + 10.0, target[ 0 ], 1e-6 );
			Assert.assertEquals( point[ 1 ], target[ 1 ], 1e-6 );
		}
	}

	@Test
	public void testConstant3DTranslationFromResources() throws Exception
	{
		final URL transformUrl = TransformPointsWithElastixBSplineTest.class
				.getResource( "/elastix/TransformParameters.BSpline3D.TranslationX.txt" );
		Assert.assertNotNull( "Transform resource not found", transformUrl );

		final ElastixTransform elastixTransform = ElastixTransform.load( new File( transformUrl.toURI() ) );
		Assert.assertTrue( elastixTransform instanceof ElastixBSplineTransform3D );

		final RealTransform transform = ElastixBSplineToBSplineRealTransform
				.convert( ( ElastixBSplineTransform ) elastixTransform );
		Assert.assertNotNull( "Converted transform should not be null", transform );
		Assert.assertEquals( 3, transform.numSourceDimensions() );
		Assert.assertEquals( 3, transform.numTargetDimensions() );

		final List< double[] > points = readPointsResource( "/elastix/Points.3D.txt", 3 );
		final double[] target = new double[ 3 ];
		for ( final double[] point : points )
		{
			transform.apply( point, target );
			Assert.assertEquals( point[ 0 ] + 10.0, target[ 0 ], 1e-6 );
			Assert.assertEquals( point[ 1 ], target[ 1 ], 1e-6 );
			Assert.assertEquals( point[ 2 ], target[ 2 ], 1e-6 );
		}
	}

	@Test
	public void testTransformixReferencePoint3DFromResources() throws Exception
	{
		final URL transformUrl = TransformPointsWithElastixBSplineTest.class
				.getResource( "/elastix/TransformParameters.BSpline3D.TransformixReference.noInitial.txt" );
		Assert.assertNotNull( "Transform resource not found", transformUrl );

		final ElastixTransform elastixTransform = ElastixTransform.load( new File( transformUrl.toURI() ) );
		Assert.assertTrue( elastixTransform instanceof ElastixBSplineTransform3D );

		final RealTransform transform = ElastixBSplineToBSplineRealTransform
				.convert( ( ElastixBSplineTransform ) elastixTransform );
		Assert.assertNotNull( "Converted transform should not be null", transform );

		final double[] source = new double[] { 2905.0, 3501.0, 750.0 };
		final double[] target = new double[ 3 ];
		transform.apply( source, target );

		// transformix reference deformation:
		// [ -8.563298, 61.231346, -30.695219 ]
		final double[] expectedDisp = new double[] { -8.563298, 61.231346, -30.695219 };
		final double[] actualDisp = new double[] {
				target[ 0 ] - source[ 0 ],
				target[ 1 ] - source[ 1 ],
				target[ 2 ] - source[ 2 ]
		};

		Assert.assertEquals( expectedDisp[ 0 ], actualDisp[ 0 ], 5.0 );
		Assert.assertEquals( expectedDisp[ 1 ], actualDisp[ 1 ], 5.0 );
		Assert.assertEquals( expectedDisp[ 2 ], actualDisp[ 2 ], 5.0 );
	}

	private static List< double[] > readPointsResource( final String resourcePath, final int dimensions ) throws Exception
	{
		final List< double[] > points = new ArrayList<>();
		final java.io.InputStream stream = TransformPointsWithElastixBSplineTest.class
				.getResourceAsStream( resourcePath );
		Assert.assertNotNull( "Points resource not found", stream );
		try ( final BufferedReader reader = new BufferedReader( new InputStreamReader( stream, StandardCharsets.UTF_8 ) ) )
		{
			String line;
			while ( ( line = reader.readLine() ) != null )
			{
				final String trimmed = line.trim();
				if ( trimmed.isEmpty() || "point".equals( trimmed ) )
					continue;

				final String[] split = trimmed.split( "\\s+" );
				if ( split.length < dimensions )
					continue;

				final double[] point = new double[ dimensions ];
				for ( int d = 0; d < dimensions; d++ )
					point[ d ] = Double.parseDouble( split[ d ] );
				points.add( point );
			}
		}
		return points;
	}
}

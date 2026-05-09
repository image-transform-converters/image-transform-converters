package itc.transforms.imglib2;

import itc.converters.ElastixBSplineToBSplineRealTransform;
import itc.transforms.elastix.ElastixBSplineTransform;
import itc.transforms.elastix.ElastixTransform;
import net.imglib2.realtransform.RealTransform;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class BSplineDisplacementFieldCopyTest
{
	@Test
	public void copyShouldBeUsableForSampling() throws Exception
	{
		final File transformFile = new File(
				BSplineDisplacementFieldCopyTest.class
						.getResource( "/elastix/TransformParameters.BSpline3D.TranslationX.txt" )
						.toURI() );

		final ElastixTransform elastixTransform = ElastixTransform.load( transformFile );
		final RealTransform transform = ElastixBSplineToBSplineRealTransform
				.convert( ( ElastixBSplineTransform ) elastixTransform );
		final RealTransform copy = transform.copy();

		final double[] source = new double[] { 10, 20, 30 };
		final double[] target = new double[ 3 ];
		copy.apply( source, target );

		Assert.assertEquals( 11.0, target[ 0 ], 1e-6 );
		Assert.assertEquals( 20.0, target[ 1 ], 1e-6 );
		Assert.assertEquals( 30.0, target[ 2 ], 1e-6 );
	}
}


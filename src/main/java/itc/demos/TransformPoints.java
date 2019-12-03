package itc.demos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import itc.converters.ElastixBSplineToBSplineRealTransform;
import itc.transforms.elastix.ElastixBSplineTransform;
import itc.transforms.elastix.ElastixTransform;
import net.imglib2.realtransform.RealTransform;

public class TransformPoints {

	public static void main(String[] args) throws UnsupportedOperationException, IOException {

		String bSplineTransformFile = args[ 0 ];
		String pointsFile = args[ 1 ];
		
		// read the transform
		RealTransform transform = null;
		final ElastixTransform elastixTransform = ElastixTransform.load( new File( bSplineTransformFile ));
		if( elastixTransform.Transform.equals( "BSplineTransform" ))
		{
			transform = ElastixBSplineToBSplineRealTransform.convert( (ElastixBSplineTransform)elastixTransform );
		}
		else
		{
			System.out.println( "transform " + elastixTransform.Transform + " not supported yet" );
			return;
		}

		// read the file
		List< String > lines = null;
		try
		{
			lines = Files.readAllLines( Paths.get( pointsFile ) );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
			return;
		}

		// parse all points, transform, and print to stdout
		double[] transformedPoint = new double[ transform.numSourceDimensions() ];
		for( String line : lines )
		{
			String[] splitLine = line.split(" ");

			if( line.equals("point"))
				continue;
			else if( splitLine.length < transform.numSourceDimensions() )
				continue;

			double[] point = Arrays.stream( line.split(" ") )
					.mapToDouble(Double::parseDouble)
					.toArray();

			transform.apply( point, transformedPoint );
			String s = Arrays.stream( transformedPoint ).mapToObj( Double::toString )
				.collect(Collectors.joining( " " ));

			System.out.println( s );
		}

	}

}

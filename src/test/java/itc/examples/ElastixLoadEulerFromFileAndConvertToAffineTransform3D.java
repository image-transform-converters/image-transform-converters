package itc.examples;

import itc.converters.ElastixEuler3DToAffineTransform3D;
import itc.transforms.elastix.ElastixEulerTransform3D;
import itc.transforms.elastix.ElastixTransform;

import java.io.File;
import java.io.IOException;

public class ElastixLoadEulerFromFileAndConvertToAffineTransform3D
{
	public static void main( String[] args ) throws IOException
	{
		final ElastixTransform elastixTransform = ElastixTransform.load(
				new File( ElastixLoadEulerFromFileAndConvertToAffineTransform3D.class.getResource(
						"/elastix/TransformParameters.Euler2D.txt" ).getFile() ) );

		if ( elastixTransform instanceof ElastixEulerTransform3D )
		{
			System.out.println( "I am a ElastixEulerTransform3D\n");
		}

		new ElastixEuler3DToAffineTransform3D().convert( ( ElastixEulerTransform3D ) elastixTransform );

		System.out.println( elastixTransform );
	}
}

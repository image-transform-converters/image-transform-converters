package itc.examples;

import itc.transforms.elastix.ElastixEulerTransform;
import itc.transforms.elastix.ElastixTransform;

import java.io.File;
import java.io.IOException;

public class ElastixLoadEulerFromFile
{
	public static void main( String[] args ) throws IOException
	{
		final ElastixTransform elastixTransform = ElastixTransform.load(
				new File( ElastixLoadEulerFromFile.class.getResource(
						"/elastix/TransformParameters.Euler2D.txt" ).getFile() ) );

		if ( elastixTransform instanceof ElastixEulerTransform )
		{
			System.out.println( "I am a ElastixEulerTransform\n");
		}

		System.out.println( elastixTransform );
	}
}

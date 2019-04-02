package itc.examples;

import itc.transforms.elastix.ElastixAffineTransform;
import itc.transforms.elastix.ElastixTransform;

import java.io.File;
import java.io.IOException;

public class ElastixLoadAffineFromFile
{
	public static void main( String[] args ) throws IOException
	{
		final ElastixTransform elastixTransform = ElastixTransform.load(
				new File( ElastixLoadAffineFromFile.class.getResource(
						"/elastix/TransformParameters.Affine3D.txt" ).getFile() ) );

		System.out.println( elastixTransform );
	}
}

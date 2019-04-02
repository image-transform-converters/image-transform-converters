package itc.examples;

import itc.transforms.bdv.BdvTransform;

import java.io.File;
import java.io.IOException;

public class AffineFromBdvXml
{
	public static void main( String[] args ) throws IOException
	{
		final File file = new File(
				AffineFromBdvXml.class.getResource( "../../bdv/bdv.xml" ).getFile() );

		BdvTransform.load( file );
	}
}

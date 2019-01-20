import org.junit.Test;

import itc.converters.AffineTransform3DToFlatString;
import itc.converters.FlatStringToAffineTransform3D;
import itc.transforms.AffineAsFlatString;
import net.imglib2.realtransform.AffineTransform3D;

import static org.junit.Assert.*;

import java.util.Arrays;

public class TestAffineTransform3DToString {

	@Test
	public void test()
	{
		AffineTransform3DToFlatString toStringConverter = new AffineTransform3DToFlatString();
		FlatStringToAffineTransform3D fromStringConverter = new FlatStringToAffineTransform3D();

		ChainConversions< AffineTransform3D, AffineAsFlatString > chainTester = new ChainConversions<>( 
				toStringConverter, fromStringConverter );

		chainTester.setEqualityForward( 
				 (a,b) -> { return Arrays.equals(a.getRowPackedCopy(), b.getRowPackedCopy()); });

		AffineAsFlatString affineAsString = new AffineAsFlatString( 
				"1 0.1 0.1 100 0.2 0.2 2.0 -100 0.3 0.3 4.0 200",
				" " );

		ChainConversions.ChainConversionResult result = chainTester.test( affineAsString );
	
		System.out.println( new AffineTransform3D().equals( new AffineTransform3D()));
	
		//assertTrue("first circle", result.firstCircleSuccess);
		assertTrue("second circle", result.secondCircleSuccess);
	}
}

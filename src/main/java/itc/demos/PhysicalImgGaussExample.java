package itc.demos;

import bdv.util.BdvFunctions;
import itc.physicalimg.PhysicalImg;
import itc.physicalimg.PhysicalImgFromDiscrete;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;

import java.util.Random;

public class PhysicalImgGaussExample
{
	public static < T extends RealType< T > & NativeType< T > > void main( String[] args )
	{
		final RandomAccessibleInterval< IntType > ints = ArrayImgs.ints( 100, 100, 10 );
		final Cursor< IntType > cursor = Views.iterable( ints ).cursor();
		final Random random = new Random();
		while (cursor.hasNext() ) cursor.next().set( random.nextInt( 65535 ) );


		BdvFunctions.show( ints, "ints" );

		final PhysicalImgFromDiscrete< T > img = new PhysicalImgFromDiscrete(
				ints,
				new AffineTransform3D(),
				PhysicalImg.MICROMETER );

		BdvFunctions.show( img.raiView(1.0), "input" );

//		final PhysicalImg< T > gauss = img.copy( 1.0 );
//
//		Gauss3.gauss( 3.0, img.raView(), gauss.raiView( 1.0 ) );



	}
}

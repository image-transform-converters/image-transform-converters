package itc.demos;

import bdv.util.BdvFunctions;
import itc.physicalimg.PhysicalImg;
import itc.physicalimg.PhysicalImgFromDiscrete;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;

import java.util.Random;

public class PhysicalImgGaussExample
{
	public static final double[] ONE_MICROMETER_3D = { 1.0, 1.0, 1.0 };

	public static < T extends RealType< T > & NativeType< T > > void main( String[] args )
	{
		final RandomAccessibleInterval< IntType > ints = ArrayImgs.ints( 100, 100, 10 );
		final Cursor< IntType > cursor = Views.iterable( ints ).cursor();
		final Random random = new Random();
		while (cursor.hasNext() ) cursor.next().set( random.nextInt( 65535 ) );

		final PhysicalImgFromDiscrete< T > img = new PhysicalImgFromDiscrete(
				ints,
				new AffineTransform3D(),
				PhysicalImg.MICROMETER );

//		BdvFunctions.show( img.raiView( ONE_MICROMETER_3D ), "input" );

		final PhysicalImg< T > gauss = img.copy( ONE_MICROMETER_3D );

		// this does not work, I think because one cannot write into the raiView :-(
		Gauss3.gauss( new double[]{3.0,3.0,3.0}, img.raView(), gauss.raiView( ONE_MICROMETER_3D ) );

		BdvFunctions.show( gauss.raiView( ONE_MICROMETER_3D ), "gauss" );

	}
}

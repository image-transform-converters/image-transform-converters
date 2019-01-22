package itc.demos;

import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;
import bdv.util.BdvStackSource;
import itc.physicalimg.PhysicalImg;
import itc.physicalimg.PhysicalImgBuilder;
import itc.utilities.CopyUtils;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;

import java.util.Random;

public class PhysicalImgGaussExample
{
	public static final double[] ONE_MICROMETER_3D = { 1.0, 1.0, 1.0 };

	public static void main( String[] args )
	{
		final RandomAccessibleInterval< IntType > ints = ArrayImgs.ints( 100, 100, 20 );
		final Cursor< IntType > cursor = Views.iterable( ints ).cursor();
		final Random random = new Random();
		while (cursor.hasNext() ) cursor.next().set( random.nextInt( 65535 ) );

		PhysicalImgBuilder<IntType> builder = new PhysicalImgBuilder<>( ints )
				.spacing( ONE_MICROMETER_3D )
				.unit( PhysicalImg.MICROMETER );

		final PhysicalImg< IntType > img = builder.wrap( ints );

		// show the original image
		BdvStackSource<IntType> bdv = BdvFunctions.show( img.raiView(), "input" );


		// prepare a new PhysicalImg for the output of filtering
		// allocates new data
		final PhysicalImg< IntType > gauss = builder
				.spacing( 2, 2, 2 )
				.wrap( CopyUtils.copyAsArrayImg( img.raiView( 1, 1, 1)));
	
		
		Gauss3.gauss(
				gauss.getInPixelUnits( 3.0, 3.0, 3.0),
				img.raViewPixel( 1, 1, 1 ),
				gauss.getWrappedRAI() // Here one has to know that the wrappedRAI has the correction spacing
		);
		
		// show the smoothed image
		BdvFunctions.show( gauss.raiView(), "gauss", BdvOptions.options().addTo(bdv) );
	}
}

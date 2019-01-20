import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.interpolation.randomaccess.ClampingNLinearInterpolatorFactory;
import net.imglib2.realtransform.*;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;

public class BdvAffineExample01
{
	public static void main( String[] args )
	{

		// What is actually the good default to define an image in imglib2?
		// Maybe, as John suggested, it should be a RealRandomAccessible,
		// with the convention that it has isotropic coordinate axes (of a given unit)
		
		final RandomAccessibleInterval< BitType > raiOriginal = createInputImage( 100, 100, 100 );

		RealRandomAccessible< BitType > rraOriginal = Views.interpolate(
				Views.extendBorder( raiOriginal ),
				new ClampingNLinearInterpolatorFactory<>() );

		final BdvHandle bdv = BdvFunctions.show( rraOriginal, raiOriginal, "original" ).getBdvHandle();

		final AffineTransform3D translationTransform = new AffineTransform3D();
		translationTransform.translate( new double[]{50,50,0} );

		final RealRandomAccessible< BitType > translated =
				RealViews.transform( rraOriginal, translationTransform );




	}

	private static RandomAccessibleInterval< BitType > createInputImage(
			int nx, int ny, int nz )
	{
		final ArrayImg< BitType, LongArray > original = ArrayImgs.bits( nx, ny, nz );
		final Cursor< BitType > cursor = Views.iterable( original ).cursor();
		while( cursor.hasNext() ) cursor.next().set( true );
		return original;
	}
}

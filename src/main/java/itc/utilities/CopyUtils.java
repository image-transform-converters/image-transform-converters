package itc.utilities;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

public class CopyUtils
{
	public static < T extends RealType< T > & NativeType< T > >
	RandomAccessibleInterval< T > createArrayImg( Interval interval, T type )
	{
		final ArrayImg<T,?> arrayImg = new ArrayImgFactory<T>( type ).create( interval );

		final RandomAccessibleInterval< T > copy = Views.translate( arrayImg, Intervals.minAsLongArray( interval ) );

		return copy;
	}

	public static < T extends RealType< T > & NativeType< T > >
	RandomAccessibleInterval< T > createArrayImg( RandomAccessibleInterval< T > orig )
	{
		return createArrayImg( orig, Util.getTypeFromInterval( orig ));
	}

	public static < T extends RealType< T > & NativeType< T > >
	RandomAccessibleInterval< T > copyAsArrayImg( RandomAccessibleInterval< T > orig )
	{
		final RandomAccessibleInterval<T> copy = createArrayImg( orig, Util.getTypeFromInterval( orig ));

		LoopBuilder.setImages( copy, orig ).forEachPixel( Type::set );

		return copy;
	}

}

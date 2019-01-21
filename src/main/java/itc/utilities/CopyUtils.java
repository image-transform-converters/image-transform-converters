package itc.utilities;

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
	RandomAccessibleInterval< T > copyAsArrayImg( RandomAccessibleInterval< T > orig )
	{
		final ArrayImg arrayImg = new ArrayImgFactory( Util.getTypeFromInterval( orig ) ).create( orig );

		final RandomAccessibleInterval< T > copy = Views.translate( arrayImg, Intervals.minAsLongArray( orig ) );

		LoopBuilder.setImages( copy, orig ).forEachPixel( Type::set );

		return copy;
	}

}

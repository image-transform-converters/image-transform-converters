package itc.utilities;

import static net.imglib2.util.LinAlgHelpers.rows;

public class VectorUtils
{
	public static String toString( final double[] a )
	{
		return toString( a, "%6.3f " );
	}

	public static String toString( final double[] a, final String format )
	{
		final int rows = rows( a );

		String result = "";
		for ( int i = 0; i < rows; ++i )
			result += String.format( format, a[ i ] );
		return result;
	}
}

package itc.physicalimg;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SimplePhysicalImg< T extends RealType< T > & NativeType< T > >
{
	public static final int C = 3;
	public static final int T = 4;

	final private RandomAccessibleInterval< T > rai;
	final private double[] voxelSpacing;
	final private String unit;

	public SimplePhysicalImg(
			RandomAccessibleInterval< T > raiXYZCT,
			double[] voxelSpacingXYZ,
			String unit )
	{
		this.rai = raiXYZCT;
		this.voxelSpacing = voxelSpacingXYZ;
		this.unit = unit;

		if ( ! isWritable() )
		{
			// throw Error or Warning;
		}

	}

	/**
	 * Tests whether one can write into the image.
	 * Takes the first pixel and replaces it by another value.
	 * Checks whether this value was really altered.
	 * Then it puts the value back to the original value.
	 *
	 * @return boolean indicating whether is image really is writeable
	 */
	public boolean isWritable( )
	{

		// TODO

		return true;
	}

	/**
	 * Converts physical to pixel units.
	 *
	 * This method is useful, e.g. to figure out the correct
	 * sigmas in pixels for Gaussian smoothing.
	 *
	 * @param xyz
	 * @return 3D array with the location in pixels
	 */
	public long[] toPixels( double[] xyz )
	{
		final long[] pixels = new long[ 3 ];
		for ( int d = 0; d < 3; d++ )
		{
			pixels[ d ] = (long) ( xyz[ d ] / voxelSpacing[ d ] );
		}
		return pixels;
	}

	/**
	 * Returns a 3D rai for the requested channel and time-point.
	 * As only Views.hyperslice is used the returned rai should be writeable;
	 * such that changing values in the rai will change values in the
	 * SimplePhysicalImg
	 *
	 * @param c
	 * @param t
	 * @return 3D RAI
	 */
	public RandomAccessibleInterval< T > getRai( long c, long t )
	{
		return Views.hyperSlice( Views.hyperSlice( rai, T, t ), C, c );
	}

	/**
	 * Returns the value at the requested location.
	 *
	 * Comment:
	 * This feels easier than using an RRA where one would have
	 * to write more code to achieve the same:
	 * access = rra.randomAccess();
	 * access.setPosition( xyzct );
	 * access.get().getRealDouble();
	 *
	 * @param xyz
	 * @param c
	 * @param t
	 * @return double pixel value
	 */
	public double valueAt( double[] xyz, long c, long t )
	{
		final long[] pixels = toPixels( xyz );
		final RandomAccess< T > access = getRai( c, t ).randomAccess();
		access.setPosition( pixels );

		return access.get().getRealDouble();
	}

}

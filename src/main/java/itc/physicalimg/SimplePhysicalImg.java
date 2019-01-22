package itc.physicalimg;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SimplePhysicalImg< T extends RealType< T > & NativeType< T > >
{
	public static final int C = 3;
	public static final int T = 4;
	public static final int Z = 2;

	final private RandomAccessibleInterval< T > rai;
	final private double[] voxelSpacing;
	final private String unit;


	/**
	 *
	 *
	 *
	 *
	 * Notes:
	 *
	 * If performance is critical one could consider implementing
	 * different internal data structures for input RAIs of different dimensionality.
	 * Essentially one could save a bunch of Views.addDimension during constructing
	 * the internal representation and save a bunch of Views.hyperslice
	 * during providing access (getRAI methods).
	 * This could be achieved via different constructors for
	 * input RAIs of different dimensionality.
	 *
	 *
	 * @param raiXYZCT
	 * @param voxelSpacingXYZ
	 * @param unit
	 */
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
			// throw an Error and crash!
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
	 * Returns a writeable 3D rai for the requested channel and time-point.
	 *
	 * @param c
	 * @param t
	 * @return 3D RAI
	 */
	public RandomAccessibleInterval< T > get3DRAI( long c, long t )
	{
		return Views.hyperSlice( Views.hyperSlice( rai, T, t ), C, c );
	}


	/**
	 * Returns a writeable 2D rai for the requested z-position,
	 * channel and time-point.
	 *
	 * The z position is specified in physical units!
	 * It will return the plane closest to the requested position.
	 *
	 * This method is useful to extract z-slices from a volume.
	 * This method also makes it possible to use
	 * this class for 2D (+channel +time) data, in which case the
	 * requested z-coordinate would be 0.
	 *
	 * @param c
	 * @param t
	 * @return 2D RAI
	 */
	public RandomAccessibleInterval< T > get2DRAI( double z, long c, long t )
	{
		final double physicalOffset = rai.min( Z ) / voxelSpacing[ Z ];

		long zPlane = (int) ( z / voxelSpacing[ Z ] - physicalOffset + 0.5 );

		final IntervalView< T > timepoint = Views.hyperSlice( rai, T, t );
		final IntervalView< T > channel = Views.hyperSlice( timepoint, C, c );
		final IntervalView< T > plane = Views.hyperSlice( channel, Z, zPlane );

		return plane;
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
		final RandomAccess< T > access = get3DRAI( c, t ).randomAccess();
		access.setPosition( pixels );

		return access.get().getRealDouble();
	}

}

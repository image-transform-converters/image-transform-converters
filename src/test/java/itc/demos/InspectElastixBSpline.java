package itc.demos;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import itc.converters.ElastixBSplineToBSplineRealTransform;
import itc.transforms.elastix.ElastixBSplineTransform3D;
import itc.transforms.elastix.ElastixTransform;
import net.imagej.ImageJ;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.type.numeric.real.FloatType;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class InspectElastixBSpline
{
	private static final String DEFAULT_TRANSFORM_PATH =
			"/Users/tischer/Desktop/hitt2t/Tischi-Mobie-Bspline/TransformParameters.2.noInitial.txt";

	private static class Sample
	{
		final double[] source;
		final double[] displacement;
		final double magnitude;

		Sample( final double[] source, final double[] displacement, final double magnitude )
		{
			this.source = source;
			this.displacement = displacement;
			this.magnitude = magnitude;
		}
	}

	public static void main( final String[] args ) throws Exception
	{
		final String transformPath = args.length > 0 ? args[ 0 ] : DEFAULT_TRANSFORM_PATH;
		final int samplesPerDim = args.length > 1 ? Integer.parseInt( args[ 1 ] ) : 100;
		final double threshold = args.length > 2 ? Double.parseDouble( args[ 2 ] ) : 1e-6;

		final File transformFile = new File( transformPath );
		if ( !transformFile.exists() )
			throw new IllegalArgumentException( "Transform file not found: " + transformFile.getAbsolutePath() );

		final ElastixBSplineTransform3D elastixTransform = ( ElastixBSplineTransform3D ) ElastixTransform.load( transformFile );
		final RealTransform transform = ElastixBSplineToBSplineRealTransform.convert( elastixTransform );

		final int n = transform.numSourceDimensions();
		if ( n < 2 || n > 3 )
			throw new UnsupportedOperationException( "Only 2D/3D BSpline supported here, got: " + n + "D" );

		System.out.println( "Transform: " + transformPath );
		System.out.println( "Dimensions: " + n );
		System.out.println( "Samples per dimension: " + samplesPerDim );
		System.out.println( "Change threshold (physical units): " + threshold );
		System.out.println();

		final double[] minCoord = new double[ n ];
		final double[] maxCoord = new double[ n ];
		for ( int d = 0; d < n; d++ )
		{
			minCoord[ d ] = elastixTransform.GridOrigin[ d ];
			maxCoord[ d ] = elastixTransform.GridOrigin[ d ] +
					( elastixTransform.GridSize[ d ] - 1 ) * elastixTransform.GridSpacing[ d ];
		}

		System.out.println( "Sampling bounds (from Origin/Size/Spacing):" );
		System.out.println( "  min = " + Arrays.toString( minCoord ) );
		System.out.println( "  max = " + Arrays.toString( maxCoord ) );
		System.out.println();
		printPointDisplacement( transform, new double[]{ 2905.0, 3501.0, 750.0 } );
		printPointDisplacement( transform, new double[]{ 3034, 3679, 758 } );
		System.out.println();

//		final Stats stats = new Stats( n, threshold );
//		sampleGridRecursive( transform, minCoord, maxCoord, samplesPerDim, 0, new int[ n ], stats );
//
//		stats.print();
//		createAndShowDisplacementMagnitudeVolume( transform, minCoord, maxCoord, samplesPerDim, n );
	}

	private static void printPointDisplacement( final RealTransform transform, final double[] source )
	{
		if ( source.length != transform.numSourceDimensions() )
		{
			System.out.println( "Requested point has wrong dimensionality: " + Arrays.toString( source ) );
			return;
		}

		final double[] target = new double[ source.length ];
		transform.apply( source, target );

		double sq = 0.0;
		final double[] disp = new double[ source.length ];
		for ( int d = 0; d < source.length; d++ )
		{
			disp[ d ] = target[ d ] - source[ d ];
			sq += disp[ d ] * disp[ d ];
		}

		System.out.println( "Displacement at requested point:" );
		System.out.println( "  source = " + Arrays.toString( source ) );
		System.out.println( "  target = " + Arrays.toString( target ) );
		System.out.println( "  disp   = " + Arrays.toString( disp ) );
		System.out.println( "  |disp| = " + Math.sqrt( sq ) );
	}

	private static void createAndShowDisplacementMagnitudeVolume(
			final RealTransform transform,
			final double[] minCoord,
			final double[] maxCoord,
			final int samplesPerDim,
			final int n )
	{
		final int sizeX = samplesPerDim;
		final int sizeY = samplesPerDim;
		final int sizeZ = n == 3 ? samplesPerDim : 1;

		final Img< FloatType > magnitudeImage = ArrayImgs.floats( sizeX, sizeY, sizeZ );
		final RandomAccess< FloatType > ra = magnitudeImage.randomAccess();

		final double[] source = new double[ n ];
		final double[] target = new double[ n ];
		for ( int z = 0; z < sizeZ; z++ )
		{
			for ( int y = 0; y < sizeY; y++ )
			{
				for ( int x = 0; x < sizeX; x++ )
				{
					final double tx = sizeX == 1 ? 0.0 : ( double ) x / ( sizeX - 1 );
					final double ty = sizeY == 1 ? 0.0 : ( double ) y / ( sizeY - 1 );
					source[ 0 ] = minCoord[ 0 ] + tx * ( maxCoord[ 0 ] - minCoord[ 0 ] );
					source[ 1 ] = minCoord[ 1 ] + ty * ( maxCoord[ 1 ] - minCoord[ 1 ] );
					if ( n == 3 )
					{
						final double tz = sizeZ == 1 ? 0.0 : ( double ) z / ( sizeZ - 1 );
						source[ 2 ] = minCoord[ 2 ] + tz * ( maxCoord[ 2 ] - minCoord[ 2 ] );
					}

					transform.apply( source, target );

					double sq = 0.0;
					for ( int d = 0; d < n; d++ )
					{
						final double disp = target[ d ] - source[ d ];
						sq += disp * disp;
					}
					final float mag = ( float ) Math.sqrt( sq );

					ra.setPosition( new long[]{ x, y, z } );
					ra.get().setReal( mag );
				}
			}
		}

		final ImagePlus imp = ImageJFunctions.wrap( magnitudeImage, "BSpline displacement magnitude" );
		imp.setDimensions( 1, imp.getNSlices(), 1 );
		final Calibration cal = imp.getCalibration();
		cal.pixelWidth = sizeX > 1 ? ( maxCoord[ 0 ] - minCoord[ 0 ] ) / ( sizeX - 1 ) : 1.0;
		cal.pixelHeight = sizeY > 1 ? ( maxCoord[ 1 ] - minCoord[ 1 ] ) / ( sizeY - 1 ) : 1.0;
		cal.pixelDepth = n == 3 && sizeZ > 1 ? ( maxCoord[ 2 ] - minCoord[ 2 ] ) / ( sizeZ - 1 ) : 1.0;
		cal.xOrigin = -minCoord[ 0 ] / cal.pixelWidth;
		cal.yOrigin = -minCoord[ 1 ] / cal.pixelHeight;
		cal.zOrigin = n == 3 ? -minCoord[ 2 ] / cal.pixelDepth : 0.0;
		cal.setUnit( "physical" );
		IJ.save( imp, "/Users/tischer/Desktop/hitt2t/Tischi-Mobie-Bspline/bspline.tif" );
	}

	private static class Stats
	{
		final int n;
		final double threshold;
		long total = 0;
		long changed = 0;
		final double[] minDisp;
		final double[] maxDisp;
		double maxMagnitude = Double.NEGATIVE_INFINITY;
		final double[] maxMagnitudePoint;
		final double[] changedMinCoord;
		final double[] changedMaxCoord;
		final PriorityQueue< Sample > top5;

		Stats( final int n, final double threshold )
		{
			this.n = n;
			this.threshold = threshold;
			this.minDisp = fill( n, Double.POSITIVE_INFINITY );
			this.maxDisp = fill( n, Double.NEGATIVE_INFINITY );
			this.maxMagnitudePoint = new double[ n ];
			this.changedMinCoord = fill( n, Double.POSITIVE_INFINITY );
			this.changedMaxCoord = fill( n, Double.NEGATIVE_INFINITY );
			this.top5 = new PriorityQueue<>( 5, Comparator.comparingDouble( s -> s.magnitude ) );
		}

		private static double[] fill( final int n, final double v )
		{
			final double[] a = new double[ n ];
			Arrays.fill( a, v );
			return a;
		}

		void add( final double[] source, final double[] target )
		{
			total++;
			final double[] disp = new double[ n ];
			double sq = 0.0;
			for ( int d = 0; d < n; d++ )
			{
				disp[ d ] = target[ d ] - source[ d ];
				if ( disp[ d ] < minDisp[ d ] ) minDisp[ d ] = disp[ d ];
				if ( disp[ d ] > maxDisp[ d ] ) maxDisp[ d ] = disp[ d ];
				sq += disp[ d ] * disp[ d ];
			}
			final double mag = Math.sqrt( sq );

			if ( mag > maxMagnitude )
			{
				maxMagnitude = mag;
				System.arraycopy( source, 0, maxMagnitudePoint, 0, n );
			}

			if ( mag > threshold )
			{
				changed++;
				for ( int d = 0; d < n; d++ )
				{
					if ( source[ d ] < changedMinCoord[ d ] ) changedMinCoord[ d ] = source[ d ];
					if ( source[ d ] > changedMaxCoord[ d ] ) changedMaxCoord[ d ] = source[ d ];
				}

				top5.add( new Sample( source.clone(), disp, mag ) );
				if ( top5.size() > 5 ) top5.poll();
			}
		}

		void print()
		{
			System.out.println( "Global displacement ranges (target - source):" );
			for ( int d = 0; d < n; d++ )
				System.out.println( "  d" + d + ": [" + minDisp[ d ] + ", " + maxDisp[ d ] + "]" );

			System.out.println();
			System.out.println( "Max displacement magnitude: " + maxMagnitude );
			System.out.println( "  at source: " + Arrays.toString( maxMagnitudePoint ) );

			System.out.println();
			System.out.println( "Changed samples (> threshold): " + changed + " / " + total );
			if ( changed > 0 )
			{
				System.out.println( "Approx. source-region where transform does something:" );
				System.out.println( "  min = " + Arrays.toString( changedMinCoord ) );
				System.out.println( "  max = " + Arrays.toString( changedMaxCoord ) );
			}
			else
			{
				System.out.println( "No sampled point exceeded threshold. Try denser sampling or lower threshold." );
			}

			System.out.println();
			System.out.println( "Top changed sample points:" );
			final Sample[] top = top5.toArray( new Sample[ 0 ] );
			Arrays.sort( top, ( a, b ) -> Double.compare( b.magnitude, a.magnitude ) );
			for ( int i = 0; i < top.length; i++ )
			{
				System.out.println(
					"  #" + ( i + 1 ) +
					" |source=" + Arrays.toString( top[ i ].source ) +
					" |disp=" + Arrays.toString( top[ i ].displacement ) +
					" |mag=" + top[ i ].magnitude );
			}
		}
	}

	private static void sampleGridRecursive(
			final RealTransform transform,
			final double[] minCoord,
			final double[] maxCoord,
			final int samplesPerDim,
			final int dim,
			final int[] index,
			final Stats stats )
	{
		if ( dim == index.length )
		{
			final double[] source = new double[ index.length ];
			final double[] target = new double[ index.length ];
			for ( int d = 0; d < index.length; d++ )
			{
				final double t = samplesPerDim == 1 ? 0.0 : (double) index[ d ] / ( samplesPerDim - 1 );
				source[ d ] = minCoord[ d ] + t * ( maxCoord[ d ] - minCoord[ d ] );
			}
			transform.apply( source, target );
			stats.add( source, target );
			return;
		}

		for ( int i = 0; i < samplesPerDim; i++ )
		{
			index[ dim ] = i;
			sampleGridRecursive( transform, minCoord, maxCoord, samplesPerDim, dim + 1, index, stats );
		}
	}
}


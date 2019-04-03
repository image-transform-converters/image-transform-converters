package itc.examples;

import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;
import ij.IJ;
import ij.ImagePlus;
import itc.converters.AmiraEulerToAffineTransform3D;
import itc.converters.ElastixEuler3DToAffineTransform3D;
import itc.transforms.elastix.ElastixEulerTransform3D;
import itc.transforms.elastix.ElastixTransform;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.realtransform.Scale;
import net.imglib2.realtransform.Scale3D;
import net.imglib2.type.numeric.RealType;

import java.io.File;
import java.io.IOException;

public class AmiraEuler3DAndElastixEuler3DToBdvTransform
{
	public static < R extends RealType< R > > void main( String[] args )  throws IOException
	{

		/**
		 * Amira Euler to AffineTransform3D
		 *
		 */
		double[] translationInMicrometer = new double[]{ 147.9, 48.13, 103.0661 };
		double[] rotationAxis = new double[]{ 0.064, 0.762, 0.643 };
		double[] rotationCentreInMicrometer = new double[]{ 22.75, 20.0, 36.25 };
		double rotationAngleInDegrees = 237.0;

		final AffineTransform3D amiraEulerMillimeter =
				AmiraEulerToAffineTransform3D.convert(
						rotationAxis,
						rotationAngleInDegrees,
						translationInMicrometer,
						rotationCentreInMicrometer );

		/**
		 * Elastix Euler to AffineTransform3D
		 *
		 */

		final ElastixTransform elastixTransform = ElastixTransform.load(
				new File( "/Users/tischer/Documents/rachel-mellwig-em-prospr-registration/data/ganglion-segmentation/elastix-euler.txt" ) );

		final AffineTransform3D elastixEulerMillimeter =
				ElastixEuler3DToAffineTransform3D.convert( ( ElastixEulerTransform3D ) elastixTransform );


		/**
		 * Scaling to voxel size
		 *
		 */

		double voxelSpacingMillimeter = 0.0005;
		final AffineTransform3D voxelToMillimeter = new AffineTransform3D();
		for ( int i = 0; i < 3; i++ )
			voxelToMillimeter.set( voxelSpacingMillimeter, i , i );


		final AffineTransform3D milliToMicrometer = new AffineTransform3D();
		for ( int i = 0; i < 3; i++ )
			milliToMicrometer.set( 1000.0, i , i );


		final AffineTransform3D affineTransform3D =
				voxelToMillimeter
						.preConcatenate( amiraEulerMillimeter )
						.preConcatenate( elastixEulerMillimeter.inverse() )
						.preConcatenate( milliToMicrometer );

		final ImagePlus fibSemImp = IJ.openImage( "/Users/tischer/Documents/rachel-mellwig-em-prospr-registration/data/ganglion-segmentation/fib-sem-seg-ganglion.tif" );

		final RandomAccessibleInterval< R > fibSem = ImageJFunctions.wrapReal( fibSemImp );
		BdvFunctions.show( fibSem, "fib-sem-ganglion",
				BdvOptions.options().sourceTransform( affineTransform3D ) );



	}
}

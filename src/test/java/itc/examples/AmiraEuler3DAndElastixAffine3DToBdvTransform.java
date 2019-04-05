package itc.examples;

import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;
import bdv.util.BdvStackSource;
import ij.IJ;
import ij.ImagePlus;
import itc.converters.AmiraEulerToAffineTransform3D;
import itc.converters.ElastixAffine3DToAffineTransform3D;
import itc.converters.ElastixEuler3DToAffineTransform3D;
import itc.transforms.elastix.ElastixAffineTransform3D;
import itc.transforms.elastix.ElastixEulerTransform3D;
import itc.transforms.elastix.ElastixTransform;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

import java.io.File;
import java.io.IOException;

import static itc.utilities.TransformUtils.asStringBdvStyle;

public class AmiraEuler3DAndElastixAffine3DToBdvTransform
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

		final ElastixTransform elastixTransform =
				ElastixTransform.load(
					new File( "/Users/tischer/Desktop/elastix-tmp/TransformParameters.0.txt" ) );

		final AffineTransform3D elastixEulerMillimeter =
				ElastixAffine3DToAffineTransform3D.convert( ( ElastixAffineTransform3D ) elastixTransform );


		/**
		 * Scaling to voxel size
		 *
		 */

		final AffineTransform3D affineTransform3D =
				getCombinedBdvTransform( amiraEulerMillimeter, elastixEulerMillimeter,  0.0005 );

		final ImagePlus spemGanglionImp = IJ.openImage( "/Users/tischer/Documents/rachel-mellwig-em-prospr-registration/data/ganglion-segmentation/spem-seg-ganglion.tif" );

		final RandomAccessibleInterval< R > spemGanglion = ImageJFunctions.wrapReal( spemGanglionImp );
		final BdvStackSource< R > spemGanglionSource = BdvFunctions.show( spemGanglion, "spem-ganglion",
				BdvOptions.options()
						.sourceTransform( new double[]{ 0.5, 0.5, 0.5 } ) );

		spemGanglionSource.setColor(
				new ARGBType( ARGBType.rgba( 0, 255, 0, 255 ) ) );

		final ImagePlus fibSemImp = IJ.openImage( "/Users/tischer/Documents/rachel-mellwig-em-prospr-registration/data/ganglion-segmentation/fib-sem-seg-ganglion.tif" );

		final RandomAccessibleInterval< R > fibSemGanglion = ImageJFunctions.wrapReal( fibSemImp );
		final BdvStackSource< R > fibSemSource =
				BdvFunctions.show( fibSemGanglion, "fib-sem-ganglion",
					BdvOptions.options()
						.sourceTransform( affineTransform3D )
						.addTo( spemGanglionSource.getBdvHandle() ) );

		fibSemSource.setColor(
				new ARGBType( ARGBType.rgba( 255, 0, 0, 255 ) ) );


		final AffineTransform3D affineTransform3D =
				getCombinedBdvTransform( amiraEulerMillimeter, elastixEulerMillimeter,  10.0 / 1000000.0 );


	}

	private static AffineTransform3D getCombinedBdvTransform(
			AffineTransform3D amiraEulerMillimeter,
			AffineTransform3D elastixEulerMillimeter,
			double voxelSpacingMillimeter )
	{
		final AffineTransform3D voxelToMillimeter = new AffineTransform3D();
		for ( int i = 0; i < 3; i++ )
			voxelToMillimeter.set( voxelSpacingMillimeter, i , i );


		final AffineTransform3D milliToMicrometer = new AffineTransform3D();
		for ( int i = 0; i < 3; i++ )
			milliToMicrometer.set( 1000.0, i , i );

		final AffineTransform3D affineTransform3D =
				voxelToMillimeter
						.preConcatenate( elastixEulerMillimeter.inverse() )
						.preConcatenate( amiraEulerMillimeter )
						.preConcatenate( milliToMicrometer );

		System.out.println( "Spacing: " + voxelSpacingMillimeter );
		System.out.println( asStringBdvStyle( affineTransform3D ) );

		return affineTransform3D;
	}
}

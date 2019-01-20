package itc.demos;

import bdv.util.BdvFunctions;
import ij.IJ;
import ij.ImagePlus;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.realtransform.InvertibleRealTransform;
import net.imglib2.realtransform.InvertibleRealTransformSequence;
import net.imglib2.realtransform.RealViews;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

/**
 * This class contains a demonstration of using imglib2 to transform "calibrated" images (i.e. in physical units).
 * 
 * @author John Bogovic
 */
public class ImageTransformBestPractices {

	public static void main(String[] args)
	{
		// Load a calibrated image
		ImagePlus imp = loadMrAndCalibrate();

		double[] inputSpacing = new double[]{ 800, 800, 2200 };
		double[] outputSpacing = new double[]{ 400, 400, 400 };

		Img<UnsignedByteType> imgpixel = ImageJFunctions.wrapByte( imp );
		RealRandomAccessible<UnsignedByteType> imgPhysical = wrapAndInterpolateToPhysicalSpace( 
				imgpixel,
				inputSpacing );  // TODO grab calibration from imp
		
		AffineTransform3D shearingTransform = new AffineTransform3D();
		shearingTransform.set( 
				0.8, 0.1, 0.1, 0,
				0.1, 0.8, 0.1, 0,
				0.1, 0.1, 0.8, 0 );
		// 
		RealRandomAccessible<UnsignedByteType> result = transformAndRender( 
				imgPhysical, shearingTransform, outputSpacing );
				
		BdvFunctions.show( result, getOutputInterval(imgpixel, inputSpacing, outputSpacing), "transformed real interval pixel space");
	}
	
	public static Interval getOutputInterval( Interval intervalIn, 
			double[] spacingin,
			double[] spacingout )
	{
		return new FinalInterval( 
				(long)Math.round( intervalIn.dimension(0) * spacingin[ 0 ] / spacingout[ 0 ] ),
				(long)Math.round( intervalIn.dimension(1) * spacingin[ 1 ] / spacingout[ 1 ]),
				(long)Math.round( intervalIn.dimension(2) * spacingin[ 2 ] / spacingout[ 2 ]));
	}
	
	public static <T extends RealType<T> & NativeType<T>> RealRandomAccessible<T> transformAndRender(
			RealRandomAccessible<T> imgPhysical,
			InvertibleRealTransform transform,
			double[] outputPixelSpacing )
	{
		// transform from physicalTo
		AffineTransform3D outputPixelToPhysical = new AffineTransform3D();
		for( int i = 0; i < 3; i++ )
			outputPixelToPhysical.set( outputPixelSpacing[ i ], i, i );
		
		// in this particular case we *could* concatenate the affines,
		// but in general we need a sequence like this
		InvertibleRealTransformSequence totalTransform = new InvertibleRealTransformSequence();
		totalTransform.add(transform);
		totalTransform.add( outputPixelToPhysical.inverse() ); // TODO explain why the inverse

		return RealViews.transform( imgPhysical, totalTransform );
	}
	
	public static ImagePlus loadMrAndCalibrate()
	{
		// sadly this image
		ImagePlus imp = IJ.openImage("http://imagej.nih.gov/ij/images/mri-stack.zip");
		// in microns
		imp.getCalibration().pixelWidth = 800;
		imp.getCalibration().pixelHeight = 800;
		imp.getCalibration().pixelDepth = 2200;
		return imp;
	}
	
	public static <T extends RealType<T> & NativeType<T>> RealRandomAccessible<T> wrapAndInterpolateToPhysicalSpace( 
			final RandomAccessibleInterval<T> imgPixelSpace,
			double[] pixelSpacing )
	{
		// get the calibration and build a transform from pixel to physical units
		AffineTransform3D pixelToPhysical = new AffineTransform3D();
		for( int i = 0; i < 3; i++ )
			pixelToPhysical.set( pixelSpacing[ i ], i, i );

		// 
		return RealViews.affine( 
				Views.interpolate( Views.extendZero( imgPixelSpace ), new NLinearInterpolatorFactory<>()), 
				pixelToPhysical );
	}

}

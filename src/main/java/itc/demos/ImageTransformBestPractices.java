/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2021 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package itc.demos;

import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;
import ij.IJ;
import ij.ImagePlus;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imagej.axis.Axis;
import net.imglib2.*;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.interpolation.randomaccess.ClampingNLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.realtransform.*;
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

	public static < R extends RealType< R > & NativeType< R > > void  main(String[] args)
	{
		// Load a calibrated image
		ImagePlus imp = loadMrAndCalibrate();

		RandomAccessibleInterval< R > imgpixel = ImageJFunctions.wrapReal( imp );

		double[] inputSpacing = new double[]{ 800, 800, 2200 };
		RealRandomAccessible< R > imgPhysical = wrapAndInterpolateToPhysicalSpace(
				imgpixel,
				inputSpacing );

//		ImgPhysical imgPhysicalew =  imgPhysical.emptyCopy(0.1);
//		Gauss3.gauss( 3.5, imgPhysical.copy(0.1), imgPhysicalNew.createRai(0.1) );

		AffineTransform3D shearingTransform = new AffineTransform3D();
		shearingTransform.set( 
				0.8, 0.1, 0.1, 0,
				0.1, 0.8, 0.1, 0,
				0.1, 0.1, 0.8, 0 );



		// You would do this for saving
		double[] outputSpacing = new double[]{ 400, 400, 400 };
		RealRandomAccessible<R> resultPixel = transformAndRender(
				imgPhysical, shearingTransform, outputSpacing );

				
		BdvFunctions.show( resultPixel, getOutputInterval(imgpixel, inputSpacing, outputSpacing), "transformed real interval pixel space");


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
	
	public static <T extends RealType<T> & NativeType<T>> RealRandomAccessible<T>
	transformAndRender(
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
		totalTransform.add( transform );
		totalTransform.add( outputPixelToPhysical.inverse() ); // TODO explain why the inverse

		return RealViews.transform( imgPhysical, totalTransform );
	}
	
	public static ImagePlus loadMrAndCalibrate()
	{
		ImagePlus imp = IJ.openImage("http://imagej.nih.gov/ij/images/mri-stack.zip");
		imp.getCalibration().pixelWidth = 800;
		imp.getCalibration().pixelHeight = 800;
		imp.getCalibration().pixelDepth = 2200;
		imp.getCalibration().setUnit( "micrometer" );
		return imp;
	}
	
	public static <T extends RealType<T> & NativeType<T>> RealRandomAccessible<T>
		wrapAndInterpolateToPhysicalSpace(
			final RandomAccessibleInterval<T> imgPixelSpace,
			double[] pixelSpacing )
	{
		// get the calibration and build a transform from pixel to physical units
		final Scale scale3D = new Scale( pixelSpacing );

//		AffineTransform3D pixelToPhysical = new AffineTransform3D();
//		for( int i = 0; i < 3; i++ )
//			pixelToPhysical.set( pixelSpacing[ i ], i, i ); //

		// 
		return RealViews.affine( 
				Views.interpolate(
						Views.extendZero( imgPixelSpace ),
						new ClampingNLinearInterpolatorFactory<>()),
				scale3D );
	}



	public static <T extends RealType<T> & NativeType<T>> RealRandomAccessible<T>
	wrapAndInterpolateToPhysicalSpace(
			final RandomAccessibleInterval<T> imgPixelSpace,
			double[] pixelSpacing,
			double[] offset )
	{
		// get the calibration and build a transform from pixel to physical units
		final Scale scale3D = new Scale( pixelSpacing );

//		AffineTransform3D pixelToPhysical = new AffineTransform3D();
//		for( int i = 0; i < 3; i++ )
//			pixelToPhysical.set( pixelSpacing[ i ], i, i ); //

		//
		return RealViews.affine(
				Views.interpolate(
						Views.extendZero( imgPixelSpace ),
						new ClampingNLinearInterpolatorFactory<>()),
				scale3D );
	}

}

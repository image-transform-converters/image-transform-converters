/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2023 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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
package itc.converters;

import itc.transforms.bdv.BdvTransform;
import itc.transforms.elastix.ElastixAffineTransform3D;
import itc.transforms.elastix.ElastixTransform;

public class BdvTransformToElastixAffine3D
{

	private final String interpolator;
	private final String resultImagePixelType;
	private final Integer[] resultImageDimensions;

	public BdvTransformToElastixAffine3D(
			String interpolator,
			String resultImagePixelType,
			Integer[] resultImageDimensions )
	{
		this.interpolator = interpolator;
		this.resultImagePixelType = resultImagePixelType;
		this.resultImageDimensions = resultImageDimensions;
	}

	public  ElastixAffineTransform3D convert( BdvTransform bdvTransform )
	{
		final ElastixAffineTransform3D elastixAffineTransform3D =
				new ElastixAffineTransform3D();

		setVoxelSpacing( bdvTransform, elastixAffineTransform3D );
		setAffineTransform( bdvTransform, elastixAffineTransform3D );

		elastixAffineTransform3D.Size = resultImageDimensions;
		elastixAffineTransform3D.ResampleInterpolator = interpolator;
		elastixAffineTransform3D.ResultImageFormat =
				ElastixTransform.RESULT_IMAGE_FORMAT_MHD;
		elastixAffineTransform3D.ResultImagePixelType = resultImagePixelType;

		setOtherParameters( elastixAffineTransform3D );

		return elastixAffineTransform3D;
	}

	private static void setOtherParameters(
			ElastixAffineTransform3D elastixAffineTransform3D )
	{

		elastixAffineTransform3D.Transform = ElastixTransform.AFFINE_TRANSFORM;
		elastixAffineTransform3D.FixedImageDimension = 3;
		elastixAffineTransform3D.MovingImageDimension = 3;
		elastixAffineTransform3D.NumberOfParameters = 12;

		elastixAffineTransform3D.InitialTransformParametersFileName = null;
		elastixAffineTransform3D.HowToCombineTransforms = "Compose";
		elastixAffineTransform3D.FixedInternalImagePixelType = "float";
		elastixAffineTransform3D.MovingInternalImagePixelType = "float";

		elastixAffineTransform3D.Index  = new Integer[3];
		elastixAffineTransform3D.Origin = new Double[3];
		elastixAffineTransform3D.Direction = new Double[]{
				1.0, 0.0, 0.0,
				0.0, 1.0, 0.0,
				0.0, 0.0, 1.0};
		elastixAffineTransform3D.UseDirectionCosines = false;
		elastixAffineTransform3D.CenterOfRotationPoint = new Double[3];
		elastixAffineTransform3D.Resampler = ElastixTransform.DEFAULT_RESAMPLER;
		elastixAffineTransform3D.DefaultPixelValue = 0.0;
		elastixAffineTransform3D.CompressResultImage = false;
	}

	private static void setAffineTransform( BdvTransform bdvTransform, ElastixAffineTransform3D elastixAffineTransform3D )
	{
		elastixAffineTransform3D.TransformParameters = new Double[ 12 ];
		final double[] rowPackedCopy = bdvTransform.affineTransform3D.getRowPackedCopy();
		for ( int i = 0; i < 12; i++ )
			elastixAffineTransform3D.TransformParameters[ i ] = rowPackedCopy[ i ];
	}

	private static void setVoxelSpacing( BdvTransform bdvTransform, ElastixAffineTransform3D elastixAffineTransform3D )
	{
		elastixAffineTransform3D.Spacing = new Double[3];
		for ( int i = 0; i < 3; i++ )
			elastixAffineTransform3D.Spacing[ i ] = bdvTransform.voxelSizes[ i ];
	}

}

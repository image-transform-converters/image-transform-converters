/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2022 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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

import itc.transforms.elastix.ElastixAffineTransform3D;
import itc.transforms.elastix.ElastixTransform;
import itc.utilities.TransformUtils;
import net.imglib2.realtransform.AffineTransform3D;

import static itc.utilities.Units.*;

public class BigWarpAffineToElastixAffineTransform3D
{
	public ElastixAffineTransform3D convert(
			AffineTransform3D bigWarpAffine,
			Double[] targetImageVoxelSpacingMillimeter,
			Integer[] targetImageDimensionsPixels,
			int targetImageBitDepth,
			String interpolator,
			String affineTransformUnit )
	{
		final String resultImagePixelType = getPixelTypeString( targetImageBitDepth );

		final AffineTransform3DToElastixAffine3D converter = new AffineTransform3DToElastixAffine3D(
				interpolator,
				resultImagePixelType,
				targetImageVoxelSpacingMillimeter,
				targetImageDimensionsPixels
		);

		// convert big warp affine transform to elastix
		//

		// the big warp affine transform already is from fixed to moving, thus no inversion is needed


		// elastix works in millimeters, thus we need to convert the big warp affine to millimeters
		if ( affineTransformUnit.equals( MILLIMETER ) )
			bigWarpAffine = bigWarpAffine;
		else if ( affineTransformUnit.equals( MICROMETER ) )
			bigWarpAffine = TransformUtils.scaleAffineTransform3DUnits( bigWarpAffine, new double[]{ 1.0 / 1000, 1.0 / 1000, 1.0 / 1000 } );
		else if ( affineTransformUnit.equals( NANOMETER ) )
			bigWarpAffine = TransformUtils.scaleAffineTransform3DUnits( bigWarpAffine, new double[]{ 1.0 / 1000000, 1.0 / 1000000, 1.0 / 1000000 } );

		// convert and save to file
		return converter.convert( bigWarpAffine );
	}

	private String getPixelTypeString( int targetImageBitDepth )
	{
		final String resultImagePixelType;
		if ( targetImageBitDepth == 8 )
			resultImagePixelType = ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_CHAR;
		else if ( targetImageBitDepth == 16 )
			resultImagePixelType = "unsigned short"; // TODO: put into itc-transforms
		else
			throw new UnsupportedOperationException( "No support for bit depth " + targetImageBitDepth );
		return resultImagePixelType;
	}

}

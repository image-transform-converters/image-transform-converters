/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2024 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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
package itc.commands;

import itc.transforms.elastix.ElastixTransform;
import itc.utilities.ParsingUtils;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.Arrays;

@Plugin(type = Command.class, menuPath = "Plugins>Registration>Elastix>Utils>Amira Euler Transform to Transformix File from Image Dimensions" )
public class AmiraEulerToTransformixFileFromImageDimensionsCommand extends AbstractAmiraEulerToTransformixFileCommand
{
	@Parameter( label = "Input image dimensions ( x, y, z ) [voxels]" )
	public String inputImageVoxelDimensionsString = "100, 200, 50";

	@Parameter( label = "Input image voxel spacing ( x, y, z ) [micrometer]" )
	public String inputImageVoxelSpacingMicrometerString = "100, 200, 50";

	@Parameter( label = "Target image dimensions ( x, y, z ) [voxels]" )
	public String targetImageVoxelDimensionsString = "100, 200, 50";

	@Parameter( label = "Target image voxel spacing ( x, y, z ) [micrometer]" )
	public String targetImageVoxelSpacingMicrometerString = "100, 200, 50";

	@Parameter( label = "Target image data type", choices = { ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_CHAR, ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_SHORT } )
	public String targetImageDataTypeString = ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_SHORT;

	public void run()
	{
		fetchRotationCenter();

		fetchTargetImageProperties();

		convertTransformAndCreateTransformixFile();
	}

	private void fetchTargetImageProperties()
	{
		targetImageVoxelSpacingMicrometer = ParsingUtils.delimitedStringToDoubleArray( targetImageVoxelSpacingMicrometerString, "," );
		targetImageDimensions = Arrays.stream( ParsingUtils.delimitedStringToDoubleArray( targetImageVoxelDimensionsString, "," )).mapToInt( x -> (int) x ).toArray();
		targetImageDataType = targetImageDataTypeString;
	}

	private void fetchRotationCenter()
	{
		final int[] inputImageDimensions = Arrays.stream( ParsingUtils.delimitedStringToDoubleArray( inputImageVoxelDimensionsString, "," ) ).mapToInt( x -> ( int ) x ).toArray();
		double[] inputImageVoxelSpacing = ParsingUtils.delimitedStringToDoubleArray( inputImageVoxelSpacingMicrometerString, "," );

		rotationCentreMicrometer = new double[ 3 ];
		for ( int d = 0; d < 3; d++ )
		{
			rotationCentreMicrometer[ d ] = 0.5 * inputImageDimensions[ d ] * inputImageVoxelSpacing[ d ];
		}
	}
}

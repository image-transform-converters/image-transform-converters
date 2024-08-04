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
package itc.examples;

import itc.commands.AmiraEulerToTransformixFileFromImageDimensionsCommand;
import itc.transforms.elastix.ElastixTransform;

import java.io.File;

public class AmiraEulerToTransformixFile
{
	public static void main( String[] args )
	{
		final AmiraEulerToTransformixFileFromImageDimensionsCommand command = new AmiraEulerToTransformixFileFromImageDimensionsCommand();

		command.inputImageVoxelDimensionsString = "1293, 861, 493";
		command.inputImageVoxelSpacingMicrometerString = "0.325, 0.325, 0.325";

		command.targetImageVoxelDimensionsString = "687, 648, 713";
		command.targetImageVoxelSpacingMicrometerString = "0.4, 0.4, 0.4";

		command.targetImageDataTypeString = ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_SHORT;

		command.translationVectorMicrometerString = "-55.7, 3.44, 54.76";
		command.rotationAngleDegrees = 77.2;
		command.rotationAxisUnitVectorString = "-0.199, 0.82, 0.53";

		command.interpolation = ElastixTransform.FINAL_NEAREST_NEIGHBOR_INTERPOLATOR;

		command.transformationOutputFile = new File( "/Users/tischer/Desktop/TransformParameters.txt");

		command.run();
	}
}

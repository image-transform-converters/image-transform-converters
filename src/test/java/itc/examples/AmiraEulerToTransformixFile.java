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

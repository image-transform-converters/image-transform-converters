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

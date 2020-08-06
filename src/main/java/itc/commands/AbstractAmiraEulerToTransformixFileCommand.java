package itc.commands;

import itc.converters.AffineTransform3DToElastixAffine3D;
import itc.converters.AmiraEulerToAffineTransform3D;
import itc.transforms.elastix.ElastixAffineTransform3D;
import itc.transforms.elastix.ElastixTransform;
import itc.utilities.ParsingUtils;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;

import java.io.File;
import java.util.Arrays;

public abstract class AbstractAmiraEulerToTransformixFileCommand implements Command
{
	@Parameter( label = "Translation ( x, y, z ) [micrometer]" )
	public String translationVectorMicrometerString = "10.0, 20.1, 15.3";

	@Parameter( label = "Rotation axis ( x, y, z ) [unit vector]" )
	public String rotationAxisUnitVectorString = "0.0, 1.0, 0.0";

	@Parameter( label = "Rotation angle [degrees]" )
	public double rotationAngleDegrees = 85.6D;

	@Parameter( label = "Interpolation method", choices = { ElastixTransform.FINAL_LINEAR_INTERPOLATOR, ElastixTransform.FINAL_NEAREST_NEIGHBOR_INTERPOLATOR } )
	public String interpolation = ElastixTransform.FINAL_LINEAR_INTERPOLATOR;

	@Parameter( label = "Transformix transformation output file", style = "save" )
	public File transformationOutputFile;

	protected double[] translationMicrometer;
	protected double[] rotationAxisUnitVector;
	protected double[] rotationCentreMicrometer;
	protected double[] targetImageVoxelSpacingMicrometer;
	protected int[] targetImageDimensions;
	protected String targetImageDataType;

	protected void convertTransformAndCreateTransformixFile()
	{
		fetchTranslationAndRotationAxisAndAngle();

		// Convert Amira transform to AffineTransform3D
		//
		final AffineTransform3D transform3D = AmiraEulerToAffineTransform3D.convert(
				rotationAxisUnitVector,
				rotationAngleDegrees,
				translationMicrometer,
				rotationCentreMicrometer );

		// Compute inverse, because elastix goes from target to source
		//
		final AffineTransform3D inverse = transform3D.inverse();

		// Convert AffineTransform3D to ElastixAffine3D
		//
		final AffineTransform3DToElastixAffine3D affineTransform3DToElastixAffine3D = new AffineTransform3DToElastixAffine3D(
				interpolation,
				targetImageDataType,
				Arrays.stream( targetImageVoxelSpacingMicrometer ).boxed().map( x -> x / 1000.0 ).toArray( Double[]::new ),
				Arrays.stream( targetImageDimensions ).boxed().toArray( Integer[]::new ) );

		final ElastixAffineTransform3D elastixAffineTransform3D = affineTransform3DToElastixAffine3D.convert( inverse );

		// Save to file
		//
		elastixAffineTransform3D.save( transformationOutputFile.getAbsolutePath() );
	}

	private void fetchTranslationAndRotationAxisAndAngle()
	{
		translationMicrometer = ParsingUtils.delimitedStringToDoubleArray( translationVectorMicrometerString, "," );
		rotationAxisUnitVector = ParsingUtils.delimitedStringToDoubleArray( rotationAxisUnitVectorString, "," );
		rotationAngleDegrees = rotationAngleDegrees;
	}
}

package itc.commands;

import itc.converters.AffineTransform3DToElastixAffine3D;
import itc.converters.AmiraEulerToAffineTransform3D;
import itc.converters.BigWarpAffineToElastixAffineTransform3D;
import itc.transforms.elastix.ElastixAffineTransform3D;
import itc.transforms.elastix.ElastixTransform;
import itc.utilities.ParsingUtils;
import loci.common.services.ServiceFactory;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import net.imglib2.realtransform.AffineTransform3D;
import ome.units.UNITS;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.Arrays;

@Plugin(type = Command.class, menuPath = "Plugins>Registration>Elastix>Utils>Amira Euler Transform to Transformix File" )
public class AmiraEulerToTransformixFileCommand implements Command
{
	public static final String MILLIMETER = "millimeter";
	public static final String MICROMETER = "micrometer";
	public static final String NANOMETER = "nanometer";

	@Parameter( label = "Translation ( x, y, z ) [micrometer]" )
	public String translationString = "10.0, 20.1, 15.3";

	@Parameter( label = "Rotation axis ( x, y, z ) [unit vector]" )
	public String rotationAxisString = "0.0, 1.0, 0.0";

	@Parameter( label = "Rotation centre ( x, y, z ) [micrometer]" )
	public String rotationCentreString = "0.0, 1.0, 0.0";

	@Parameter( label = "Rotation angle [degrees]" )
	public double rotationAngle = 85.6D;

	@Parameter( label = "Result image dimensions ( x, y, z ) [voxels]" )
	public String targetImageDimensionsString = "100, 200, 50";

	@Parameter( label = "Result image voxel spacing ( x, y, z ) [micrometer]" )
	public String targetImageVoxelSpacingString = "100, 200, 50";

	@Parameter( label = "Result image data type ", choices = { ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_CHAR, ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_SHORT } )
	public String targetImageDataType = ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_SHORT;

	@Parameter( label = "Transformix interpolation method", choices = { ElastixTransform.FINAL_LINEAR_INTERPOLATOR, ElastixTransform.FINAL_NEAREST_NEIGHBOR_INTERPOLATOR } )
	public String interpolation = ElastixTransform.FINAL_LINEAR_INTERPOLATOR;

	@Parameter( label = "Transformix transformation output file", style = "save" )
	public File transformationOutputFile;

	public void run()
	{

		// Parse input parameters
		//
		double[] translationInMicrometer = ParsingUtils.delimitedStringToDoubleArray( translationString, "," );
		double[] rotationAxisUnitVector = ParsingUtils.delimitedStringToDoubleArray( rotationAxisString, "," );
		double[] rotationCentreInMicrometer = ParsingUtils.delimitedStringToDoubleArray( rotationCentreString, "," );
		double rotationAngleInDegrees = rotationAngle;
		double[] targetImageVoxelSpacing = ParsingUtils.delimitedStringToDoubleArray( targetImageVoxelSpacingString, "," );
		int[] targetImageDimensions = Arrays.stream( ParsingUtils.delimitedStringToDoubleArray( targetImageVoxelSpacingString, "," )).mapToInt( x -> (int) x ).toArray();

		// Convert Amira transform to AffineTransform3D
		//
		final AffineTransform3D transform3D = AmiraEulerToAffineTransform3D.convert(
				rotationAxisUnitVector,
				rotationAngleInDegrees,
				translationInMicrometer,
				rotationCentreInMicrometer );

		// Convert AffineTransform3D to ElastixAffine3D
		//
		final AffineTransform3DToElastixAffine3D affineTransform3DToElastixAffine3D = new AffineTransform3DToElastixAffine3D(
				interpolation,
				targetImageDataType,
				Arrays.stream( targetImageVoxelSpacing ).boxed().toArray( Double[]::new ),
				Arrays.stream( targetImageDimensions ).boxed().toArray( Integer[]::new ) );

		final ElastixAffineTransform3D elastixAffineTransform3D = affineTransform3DToElastixAffine3D.convert( transform3D );

		// Save to file
		//
		elastixAffineTransform3D.save( transformationOutputFile.getAbsolutePath() );
	}
}

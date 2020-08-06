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

@Plugin(type = Command.class, menuPath = "Plugins>Registration>Elastix>Utils>Amira Euler Transform to Transformix File from Image Files" )
public class AmiraEulerToTransformixFileFromImageFilesCommand extends AbstractAmiraEulerToTransformixFileCommand
{
	@Parameter( label = "Input image file" )
	File inputImage;

	@Parameter( label = "Target image file" )
	File targetImage;

	public void run()
	{
		// TODO: fetchRotationCenter();
		// TODO: fetchTargetImageProperties();

		convertTransformAndCreateTransformixFile();
	}
}

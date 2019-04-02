package itc.transforms.bdv;

import itc.transforms.elastix.*;
import javafx.scene.transform.Affine;
import net.imglib2.realtransform.AffineTransform3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The BdvTransform is a 3D affine transform, specifying how to get
 * from voxel coordinates (indices) to physical coordinates.
 *
 * Thus even if there is no rotation the transform will contain the scaling factors
 * (calibration) that relate the voxel indices to the physical coordinates.
 * For example, a calibration of 0.1 micrometer in each dimension will result
 * in an affine transform with all diagonal elements set to 0.1 and all other elements
 * set to zero.
 *
 */
public class BdvTransform
{

	public static final String BDV_DELIM = " ";

	public final AffineTransform3D affineTransform3D;
	public final String unit;
	public final double[] voxelSizes;
	public final long[] imageDimensions;

	public BdvTransform(
			AffineTransform3D affineTransform3D,
			String unit,
			double[] voxelSizes,
			long[] imageDimensions )
	{
		this.affineTransform3D = affineTransform3D;
		this.unit = unit;
		this.voxelSizes = voxelSizes;
		this.imageDimensions = imageDimensions;
	}

	public static BdvTransform load( File f ) throws IOException
	{
		BufferedReader file = new BufferedReader(new FileReader(f));
		String line;
		Pattern affinePattern = Pattern.compile( ".*<affine>(.*)</affine>" );
		Pattern unitPattern = Pattern.compile( ".*<unit>(.*)</unit>" );
		Pattern sizePattern = Pattern.compile( ".*<size>(.*)</size>" );
		Pattern voxelSizeStartPattern = Pattern.compile( ".*<voxelSize>.*" );
		Pattern voxelSizeEndPattern = Pattern.compile( ".*</voxelSize>.*" );

		String affine = null;
		String unit = null;
		String voxelSize = null;
		String imageDimension = null;
		Matcher m;

		boolean voxelSizeGroup = false;

		while ((line = file.readLine()) != null) {


			m = voxelSizeStartPattern.matcher( line );
			if (m.matches())
				voxelSizeGroup = true;

			m = voxelSizeEndPattern.matcher( line );
			if (m.matches())
				voxelSizeGroup = false;

			if ( voxelSizeGroup )
			{
				m = sizePattern.matcher( line );
				if (m.matches())
					voxelSize = m.group(1);

				m = unitPattern.matcher( line );
				if (m.matches())
					unit = m.group(1);
			}
			else
			{
				m = affinePattern.matcher( line );
				if (m.matches())
					affine = m.group(1);

				m = sizePattern.matcher( line );
				if (m.matches())
					imageDimension = m.group(1);
			}
		}

		final AffineTransform3D affineTransform3D = affineTransform3D( affine );

		final double[] voxelSizes =
				Arrays.stream( voxelSize.split( BDV_DELIM ) )
						.mapToDouble( Double::parseDouble )
						.toArray();

		final long[] imageDimensions =
				Arrays.stream( imageDimension.split( BDV_DELIM ) )
						.mapToLong( Long::parseLong )
						.toArray();

		final BdvTransform bdvTransform =
				new BdvTransform(
						affineTransform3D,
						unit.trim(),
						voxelSizes,
						imageDimensions );

		return bdvTransform;
	}

	public static AffineTransform3D affineTransform3D( String affine )
	{
		final double[] affineParams = Arrays.stream( affine.split( BDV_DELIM ) ).mapToDouble( Double::parseDouble ).toArray();

		final AffineTransform3D affineTransform3D = new AffineTransform3D();
		affineTransform3D.set( affineParams );
		return affineTransform3D;
	}

}

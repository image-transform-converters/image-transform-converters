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

public class BdvTransform
{

	public static final String BDV_DELIM = " ";
	AffineTransform3D affineTransform3D;
	String unit;
	double[] voxelSizes;

	public BdvTransform( AffineTransform3D affineTransform3D, String unit, double[] voxelSizes )
	{
		this.affineTransform3D = affineTransform3D;
		this.unit = unit;
		this.voxelSizes = voxelSizes;
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
		String size = null;
		Matcher m;

		boolean voxelSizeGroup = false;

		while ((line = file.readLine()) != null) {

			m = affinePattern.matcher( line );
			if (m.matches())
				affine = m.group(1);

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
					size = m.group(1);

				m = unitPattern.matcher( line );
				if (m.matches())
					unit = m.group(1);
			}
		}

		final AffineTransform3D affineTransform3D = affineTransform3D( affine );

		final double[] voxelSizes = Arrays.stream( size.split( BDV_DELIM ) ).mapToDouble( Double::parseDouble ).toArray();

		final BdvTransform bdvTransform = new BdvTransform( affineTransform3D, unit.trim(), voxelSizes );

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

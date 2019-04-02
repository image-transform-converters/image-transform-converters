package itc.transforms.bdv;

import itc.transforms.elastix.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BdvTransform
{

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


		return null;
	}

}

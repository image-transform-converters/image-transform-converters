package itc.commands;

import net.imagej.ImageJ;

public class RunAmiraEulerToTransformixFileCommand
{
	public static void main( String[] args )
	{
		final ImageJ imageJ = new ImageJ();

		imageJ.command().run( AmiraEulerToTransformixFileFromImageFilesCommand.class, true );
	}
}

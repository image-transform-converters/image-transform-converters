/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2023 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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

/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2020 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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
package itc.converters;

import net.imglib2.realtransform.AffineTransform3D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import static itc.utilities.TransformUtils.rotationAroundImageCenterTransform;
import static itc.utilities.TransformUtils.rotationMatrix;

public class AmiraEulerToAffineTransform3D
{
	/**
	 * Amira allows the user to interactively specify
	 * an Euler (rotation and translation) transformation.
	 * The user can read the transformation parameters on the screen.
	 * The units in Amira could be anything(?),
	 * but here, currently, micrometer units are required
	 * for the conversion.
	 *
	 * @param rotationAxis
	 * @param rotationAngleInDegrees
	 * @param translationVectorInMicrometer
	 * @param rotationCenterInMicrometer
	 * @return AffineTransform3D in millimeter units, as required by elastix
	 */
	public static AffineTransform3D convert(
			double[] rotationAxis,
			double rotationAngleInDegrees,
			double[] translationVectorInMicrometer,
			double[] rotationCenterInMicrometer )
	{
		// rotate around rotation centre
		//
		final Vector3D axis = new Vector3D(
				rotationAxis[ 0 ],
				rotationAxis[ 1 ],
				rotationAxis[ 2 ] );

		double angle = rotationAngleInDegrees / 180.0 * Math.PI;

		final double[][] rotationMatrix = rotationMatrix( axis, angle );

		double[] rotationCentreInMillimeter = microToMillimeter( rotationCenterInMicrometer );

		final AffineTransform3D transform3D =
				rotationAroundImageCenterTransform(
						rotationMatrix,
						rotationCentreInMillimeter );

		// translate
		//
		double[] translationInMillimeters = microToMillimeter( translationVectorInMicrometer );

		transform3D.translate( translationInMillimeters );

		return transform3D;
	}

	private static double[] microToMillimeter( double[] rotationCenterInMicrometer )
	{
		double[] rotationCentreInMillimeter = new double[ 3 ];
		for ( int d = 0; d < 3; ++d )
			rotationCentreInMillimeter[ d ] = rotationCenterInMicrometer[ d ] / 1000.0;
		return rotationCentreInMillimeter;
	}

}

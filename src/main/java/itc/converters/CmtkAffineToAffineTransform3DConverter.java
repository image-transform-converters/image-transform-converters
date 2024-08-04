/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2024 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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

import itc.converterinterfaces.ImageTransformConverterA;
import itc.transforms.CmtkAffineTransform3D;
import net.imglib2.realtransform.AffineTransform3D;

public class CmtkAffineToAffineTransform3DConverter
		implements ImageTransformConverterA<CmtkAffineTransform3D, AffineTransform3D> {

	@Override
	public AffineTransform3D convert(CmtkAffineTransform3D input) {

		AffineTransform3D mtx = new AffineTransform3D();

		// alpha, theta and phi are in radians,
		// expect rotate vector to be in degrees
		final double alpha = Math.toRadians(input.rotate[0]);
		final double theta = Math.toRadians(input.rotate[1]);
		final double phi = Math.toRadians(input.rotate[2]);

		final double cos0 = Math.cos(alpha);
		final double cos1 = Math.cos(theta);
		final double cos2 = Math.cos(phi);
		final double sin0 = Math.sin(alpha);
		final double sin1 = Math.sin(theta);
		final double sin2 = Math.sin(phi);

		final double sin0xsin1 = sin0 * sin1;
		final double cos0xsin1 = cos0 * sin1;

		mtx.set(cos1 * cos2, 0, 0);
		mtx.set(-cos1 * sin2, 0, 1);
		mtx.set(-sin1, 0, 2);

		mtx.set((sin0xsin1 * cos2 + cos0 * sin2), 1, 0);
		mtx.set((-sin0xsin1 * sin2 + cos0 * cos2), 1, 1);
		mtx.set(sin0 * cos1, 1, 2);

		mtx.set((cos0xsin1 * cos2 - sin0 * sin2), 2, 0);
		mtx.set((-cos0xsin1 * sin2 - sin0 * cos2), 2, 1);
		mtx.set(cos0 * cos1, 2, 2);

		AffineTransform3D scaleShear = new AffineTransform3D();
		for (int i = 0; i < 3; ++i) {
			if (input.logScaleFactors)
				scaleShear.set(Math.exp(input.scale[i]), i, i);
			else
				scaleShear.set(input.scale[i], i, i);

			scaleShear.set(input.shear[i], (i / 2) + (i % 2) + 1, i / 2);
		}

		mtx.preConcatenate(scaleShear);
		transposeInPlace(mtx);

		// transform rotation center
		double[] xfmCenter = new double[3];
		mtx.apply(input.center, xfmCenter);

		// set translations
		mtx.set(input.translationXYZ[0] - xfmCenter[0] + input.center[0], 0, 3);
		mtx.set(input.translationXYZ[1] - xfmCenter[1] + input.center[1], 1, 3);
		mtx.set(input.translationXYZ[2] - xfmCenter[2] + input.center[2], 2, 3);

		return mtx;
	}

	public void transposeInPlace(AffineTransform3D mtx) {
		AffineTransform3D copy = mtx.copy();
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				mtx.set(copy.get(i, j), j, i);
			}
	}

	@Override
	public Class<CmtkAffineTransform3D> inputType() {
		return CmtkAffineTransform3D.class;
	}

	@Override
	public Class<AffineTransform3D> outputType() {
		return AffineTransform3D.class;
	}

}

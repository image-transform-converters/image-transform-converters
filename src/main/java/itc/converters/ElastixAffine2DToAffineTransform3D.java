/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2022 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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

import itc.transforms.elastix.ElastixAffineTransform2D;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.realtransform.AffineTransform3D;

public class ElastixAffine2DToAffineTransform3D
{

	/**
	 * From elastix-4.9.0 manual:
	 * Affine: (AffineTransform) An affine transformation is defined as:
	 * Tμ(x) = A(x − c) + t + c, (2.14), where the matrix A has no restrictions.
	 * This means that the image can be translated, rotated, scaled,
	 * and sheared.
	 * The parameter vector μ is formed by the matrix elements aij and the
	 * translation vector.
	 * In 2D, this gives a vector of length 6:
	 * μ = (a11,a12,a21,a22,tx,ty)T.
	 * In 3D, this gives a vector of length 12.
	 *
	 * Note: Elastix transformations are always in millimeter units.
	 *
	 */
	public static AffineTransform3D convert(ElastixAffineTransform2D elastixAffineTransform2D )
	{
		AffineTransform2D transform2D = ElastixAffine2DToAffineTransform2D.convert(elastixAffineTransform2D);

		return AffineTransform2DToAffineTransform3D.convert(transform2D);
	}
}

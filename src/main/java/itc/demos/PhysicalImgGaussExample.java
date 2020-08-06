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
package itc.demos;

import bdv.util.BdvFunctions;
import itc.physicalimg.PhysicalImg;
import itc.physicalimg.PhysicalImgFromDiscrete;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;

import java.util.Random;

public class PhysicalImgGaussExample
{
	public static final double[] ONE_MICROMETER_3D = { 1.0, 1.0, 1.0 };

	public static < T extends RealType< T > & NativeType< T > > void main( String[] args )
	{
		final RandomAccessibleInterval< IntType > ints = ArrayImgs.ints( 100, 100, 10 );
		final Cursor< IntType > cursor = Views.iterable( ints ).cursor();
		final Random random = new Random();
		while (cursor.hasNext() ) cursor.next().set( random.nextInt( 65535 ) );

		final PhysicalImgFromDiscrete< T > img = new PhysicalImgFromDiscrete(
				ints,
				new AffineTransform3D(),
				PhysicalImg.MICROMETER );

		BdvFunctions.show( img.raiView(), "input" );

		final PhysicalImg< T > gauss = img.copy( ONE_MICROMETER_3D );

		Gauss3.gauss(
				new double[]{3.0,3.0,3.0},
				img.raView( ONE_MICROMETER_3D ),
				gauss.getWrappedRAI() // Here one has to know that the wrappedRAI has the correction spacing
		);
		BdvFunctions.show( gauss.raiView(), "gauss" );

	}
}

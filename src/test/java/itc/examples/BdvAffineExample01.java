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
package itc.examples;
import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.interpolation.randomaccess.ClampingNLinearInterpolatorFactory;
import net.imglib2.realtransform.*;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;

public class BdvAffineExample01
{
	public static void main( String[] args )
	{

		// What is actually the good default to define an image in imglib2?
		// Maybe, as John suggested, it should be a RealRandomAccessible,
		// with the convention that it has isotropic coordinate axes (of a given unit)

		final RandomAccessibleInterval< BitType > raiOriginal = createInputImage( 100, 100, 100 );

		RealRandomAccessible< BitType > rraOriginal = Views.interpolate(
				Views.extendBorder( raiOriginal ),
				new ClampingNLinearInterpolatorFactory<>() );

		final BdvHandle bdv = BdvFunctions.show( rraOriginal, raiOriginal, "original" ).getBdvHandle();

		final AffineTransform3D translationTransform = new AffineTransform3D();
		translationTransform.translate( new double[]{50,50,0} );

		final RealRandomAccessible< BitType > translated =
				RealViews.transform( rraOriginal, translationTransform );


	}

	private static RandomAccessibleInterval< BitType > createInputImage(
			int nx, int ny, int nz )
	{
		final ArrayImg< BitType, LongArray > original = ArrayImgs.bits( nx, ny, nz );
		final Cursor< BitType > cursor = Views.iterable( original ).cursor();
		while( cursor.hasNext() ) cursor.next().set( true );
		return original;
	}
}

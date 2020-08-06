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
package itc.physicalimg;

import itc.utilities.CopyUtils;
import itc.utilities.TransformUtils;
import net.imglib2.*;
import net.imglib2.realtransform.RealViews;
import net.imglib2.realtransform.Scale;
import net.imglib2.realtransform.Scale3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.RandomAccessibleOnRealRandomAccessible;
import net.imglib2.view.Views;

/**
 * A continuous image in a physical coordinate system.
 * 
 * Provides an image as a {@link RealRandomAccessible}, and spatial extents as a {@link RealInterval}, 
 * in specified physical units.
 *
 */
public class PhysicalImg < T extends RealType< T > & NativeType< T > >
{
	public static final String MICROMETER = "micrometer";

	private final RandomAccessibleInterval<T> wrappedRAI;
	private final RealRandomAccessible<T> rra;
	private final RealInterval interval;
	private final String unit;

	public PhysicalImg( RealRandomAccessible<T> rra, RealInterval interval )
	{
		this( rra, interval, MICROMETER, null );
	}

	public PhysicalImg( RealRandomAccessible<T> rra,
						RealInterval interval,
						String unit,
						RandomAccessibleInterval<T> wrappedRAI )
	{
		this.rra = rra;
		this.interval = interval;
		this.unit = unit;
		this.wrappedRAI = wrappedRAI;
	}

	public RandomAccessibleInterval< T > raiView( )
	{
		return raiView( 1.0, 1.0, 1.0 );
	}

	public RandomAccessibleInterval< T > raiView( double... spacing )
	{
		assert spacing.length == 3: "Input dimensions do not match or are not 3.";

		final FinalInterval interval = interval( spacing );
		final RandomAccessible< T > ra = raView( spacing );

		return Views.interval( ra, interval );
	}

//	public RandomAccessibleInterval< T > emptyArrayImg( double... spacing )
//	{
//		assert spacing.length == 3: "Input dimensions do not match or are not 3.";
//
//		final FinalInterval interval = interval( spacing );
//
//		final ArrayImg arrayImg = new ArrayImgFactory( rra.realRandomAccess().get() ).create( interval );
//
//		return arrayImg;
//	}

	private FinalInterval interval( double... spacing )
	{
		final Scale3D scale = TransformUtils.getPhysicalToPixelScaleTransform3D( spacing );

		final FinalInterval pixelInterval =
				TransformUtils.transformRealIntervalExpand(
						this.interval,
						scale );

		return pixelInterval;
	}

	public RandomAccessible< T > raView( double... spacing  )
	{
		assert spacing.length == 3: "Input dimensions do not match or are not 3.";

		final Scale3D scale = TransformUtils.getScaleTransform3D( spacing );
		final RealRandomAccessible< T > scaledRRA = RealViews.transform( rra, scale );
		final RandomAccessibleOnRealRandomAccessible< T > raster = Views.raster( scaledRRA );

		return raster;
	}

	public PhysicalImg< T > copy( double... spacing )
	{
		final PhysicalImgFromDiscrete< T > copy = new PhysicalImgFromDiscrete<>(
				CopyUtils.copyAsArrayImg( raiView( spacing ) ),
				new Scale( spacing ),
				unit );

		return copy;
	}

	public RealRandomAccessible< T > getRRA()
	{
		return rra;
	}

	public RealInterval getInterval()
	{
		return interval;
	}

	public String getUnit()
	{
		return unit;
	}

	public RandomAccessibleInterval< T > getWrappedRAI()
	{
		return wrappedRAI;
	}
}

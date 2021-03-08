/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2021 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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

import itc.utilities.TransformUtils;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.ClampingNLinearInterpolatorFactory;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.RealViews;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * An {@link PhysicalImg} derived from a discrete raster image, an interpolator
 * created by an {@link InterpolatorFactory} and an
 * {@link AffineGet} transformation from pixel to physical coordinates.
 * 
 */
public class PhysicalImgFromDiscrete < T extends RealType< T > & NativeType< T > >
		extends PhysicalImg<T> {

	private final AffineGet pixelToPhysical;

	public PhysicalImgFromDiscrete(
			final RandomAccessibleInterval<T> rai,
			final AffineGet pixelToPhysical,
			final String unit)
	{
		this( rai, pixelToPhysical, new ClampingNLinearInterpolatorFactory(), unit );
	}


	public PhysicalImgFromDiscrete(
			final RandomAccessibleInterval<T> rai,
			final AffineGet pixelToPhysical,
			final InterpolatorFactory<T, RandomAccessible<T>> interpolatorFactory,
			final String unit)
	{
		super( RealViews.transform( Views.interpolate( Views.extendZero(  rai ), interpolatorFactory), pixelToPhysical ) ,
				TransformUtils.transformRealInterval( rai, pixelToPhysical ),
				unit,
				rai );

		this.pixelToPhysical = pixelToPhysical;
	}

	public PhysicalImgFromDiscrete(
			final RandomAccessible<T> ra,
			final Interval interval,
			final AffineGet pixelToPhysical,
			final InterpolatorFactory<T, RandomAccessible<T>> interpolatorFactory,
			final String unit)
	{
		super(  RealViews.transform( Views.interpolate( ra, interpolatorFactory), pixelToPhysical ),
				TransformUtils.transformRealInterval( interval, pixelToPhysical ),
				unit,
				Views.interval(ra, interval) );

		this.pixelToPhysical = pixelToPhysical;
	}
	
}

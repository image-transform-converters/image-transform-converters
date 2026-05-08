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
package itc.transforms.elastix;

import java.util.Arrays;

import org.scijava.plugin.Parameter;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Util;

public class ElastixBSplineTransform extends ElastixTransform {

    // BSplineTransform specific
    @Parameter
    public Integer[] GridSize;

    @Parameter
    public Integer[] GridIndex;

    @Parameter
    public Double[] GridSpacing;

    @Parameter
    public Double[] GridOrigin;

    @Parameter
    public Double[] GridDirection;

    @Parameter
    public Integer BSplineTransformSplineOrder;

    @Parameter
    public Boolean UseCyclicTransform;

    /**
     * Returns a RandomAccessibleInterval of bspline coefficients
     * representing the displacements for the specified spatial coordinate.
     * 
     * @param coordinate the x(0), y(1), or z(2) coordinates
     * @return the bspline coefficients
     */
	public RandomAccessibleInterval<DoubleType> getBSplineCoefficients(final int coordinate) {
		long N = coefficientCountPerDimension();
		assert( N * GridSize.length == NumberOfParameters );

		long[] gridDims = Arrays.stream( GridSize ).mapToLong( x -> x ).toArray();
		FinalInterval gridInterval = new FinalInterval( gridDims );

		Img<DoubleType> coefImg = Util.getSuitableImgFactory(gridInterval, new DoubleType()).create(gridInterval);
		Cursor<DoubleType> c = coefImg.cursor();

		int i = (int)( coordinate * N );
		while( c.hasNext() )
			c.next().set( TransformParameters[ i++ ] );

		return coefImg;
	} 

    /**
     * Returns a RandomAccessibleInterval of bspline coefficients
     * representing the displacements for the specified spatial coordinate.
     * 
     * @param coordinate the x(0), y(1), or z(2) coordinates
     * @param factor by which to multiply every coefficient
     * @return the bspline coefficients
     */
	public RandomAccessibleInterval<DoubleType> getBSplineCoefficients(final int coordinate, final double factor )
	{
		long N = coefficientCountPerDimension();
		assert( N * GridSize.length == NumberOfParameters );

		long[] gridDims = Arrays.stream( GridSize ).mapToLong( x -> x ).toArray();
		FinalInterval gridInterval = new FinalInterval( gridDims );

		Img<DoubleType> coefImg = Util.getSuitableImgFactory(gridInterval, new DoubleType()).create(gridInterval);
		Cursor<DoubleType> c = coefImg.cursor();

		int i = (int)( coordinate * N );
		while( c.hasNext() )
			c.next().set( factor * TransformParameters[ i++ ] );

		return coefImg;
	} 

	/**
	 * Returns the number of bspline coefficients per dimension
	 * 
	 * @return the coefficient count
	 */
	public long coefficientCountPerDimension()
	{
		long N = 1;
		for( int d = 0; d < GridSize.length; d++ )
			N *= GridSize[ d ];

		return N;
	}

}

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
package itc.utilities;

import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.RealInterval;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.realtransform.Scale;
import net.imglib2.realtransform.Scale3D;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.Arrays;
import java.util.stream.DoubleStream;

public class TransformUtils {

	public static FinalRealInterval transformRealInterval(
			final RealInterval interval,
			final RealTransform xfm ) {

		int nd = interval.numDimensions();
		double[] pt = new double[nd];
		double[] min = new double[nd];
		double[] max = new double[nd];

		// transform min
		for (int d = 0; d < nd; d++)
			pt[d] = interval.realMin(d);

		xfm.apply(pt, min);

		// transform max
		for (int d = 0; d < nd; d++) {
			pt[d] = interval.realMax(d);
		}

		xfm.apply(pt, max);

		return new FinalRealInterval(min, max);
	}


	/**
	 * For example, if the transform is in micrometer units,
	 * but one needs it in millimeter units, one can use
	 * this function with scale = new double[]{ 0.001, 0.001, 0.001 }
	 *
	 * @param transform
	 * @param scale
	 * @return
	 */
	public static AffineTransform3D scaleAffineTransform3DUnits(
			AffineTransform3D transform,
			double[] scale )
	{
		AffineTransform3D scaledTransform = transform.copy();

		final double[] inverse = Arrays.stream( scale ).map( x -> 1.0 / x ).toArray();

		scaledTransform = scaledTransform.concatenate( new Scale( inverse ) );
		scaledTransform = scaledTransform.preConcatenate( new Scale( scale ) );

		return scaledTransform;
	}

	public static FinalInterval transformRealIntervalExpand(
			final RealInterval interval,
			final RealTransform xfm ) {
		
		int nd = interval.numDimensions();
		FinalRealInterval realTransformedInterval = transformRealInterval(interval, xfm );

		long[] min = new long[ nd ];
		long[] max = new long[ nd ];
		for( int i = 0; i < nd; i++ )
		{
			min[ i ] = (long) Math.floor( realTransformedInterval.realMin( i ));
			max[ i ] = (long) Math.ceil( realTransformedInterval.realMax( i ));
		}
			
		return new FinalInterval( min, max );
	}

	public static FinalInterval transformInterval( final Interval interval, final RealTransform xfm ) {

		int nd = interval.numDimensions();
		double[] pt = new double[nd];
		double[] ptxfm = new double[nd];

		long[] min = new long[nd];
		long[] max = new long[nd];

		// transform min
		for (int d = 0; d < nd; d++)
			pt[d] = interval.min(d);

		xfm.apply(pt, ptxfm);
		copyToLongFloor(ptxfm, min);

		// transform max
		for (int d = 0; d < nd; d++) {
			pt[d] = interval.max(d);
		}

		xfm.apply(pt, ptxfm);
		copyToLongCeil(ptxfm, max);

		return new FinalInterval(min, max);
	}

	public static void copyToLongFloor(final double[] src, final long[] dst) {

		for (int d = 0; d < src.length; d++)
			dst[d] = (long) Math.floor(src[d]);
	}

	public static void copyToLongCeil(final double[] src, final long[] dst) {
		
		for (int d = 0; d < src.length; d++)
			dst[d] = (long) Math.ceil(src[d]);
	}

	public static Scale3D getScaleTransform3D( double... spacing )
	{
		assert spacing.length == 3: "Input dimensions do not match or are not 3.";

		return new Scale3D( spacing );
	}

	public static Scale3D getPhysicalToPixelScaleTransform3D( double... spacing )
	{
		assert spacing.length == 3: "Input dimensions do not match or are not 3.";

		return new Scale3D( DoubleStream.of( spacing ).map( s -> 1.0 / s ).toArray() );
	}

	/**
	 * Constructs a rotation matrix from an axis and an angle.
	 * @param axis
	 * @param angle
	 * @return rotation matrix
	 */
	public static double[][] rotationMatrix( Vector3D axis, double angle )
	{
		final Rotation rotation = new Rotation(
				axis,
				angle,
				RotationConvention.VECTOR_OPERATOR );

		final double[][] matrix = rotation.getMatrix();

		return matrix;
		}

	/**
	 * TODO: This feels wrong, why does one have to multiply the translation extra?
	 *
	 * @param transform
	 * @param scale
	 * @return scaled transform
	 */
	public static AffineTransform3D scaleAffineTransform3D(
			AffineTransform3D transform,
			double[] scale )
	{

		final AffineTransform3D scaledTransform = transform.copy();

		// TODO: maybe preconcatenate here would avoid the extra
		// scaling of the translation???
		transform.concatenate( new Scale( scale ) );

		final double[] translation = transform.getTranslation();
		for ( int d = 0; d < 3; ++d )
		{
			translation[ d ] *= scale[ d ];
		}

		transform.setTranslation( translation );

		return transform;
	}

	/**
	 * Constructs an AffineTransform3D from a pure rotation.
	 *
	 * @param rotationMatrix
	 * @return
	 */
	public static AffineTransform3D rotationAffineTransform3D( double[][] rotationMatrix )
	{

		final AffineTransform3D rotationTransform = new AffineTransform3D();

		for ( int row = 0; row < 3; ++row )
			for ( int col = 0; col < 3; ++col )
				rotationTransform.set( rotationMatrix[ row ][ col ], row, col );

		return rotationTransform;
	}

	/**
	 * Constructs an AffineTransform3D, performing an
	 * rotation around a rotation center.
	 *
	 * @param rotation
	 * @param rotationCenter
	 * @return
	 */
	public static AffineTransform3D rotationAroundImageCenterTransform(
			double[][] rotation,
			double[] rotationCenter )
	{

		final AffineTransform3D rotationTransform = rotationAffineTransform3D( rotation );

		double[] translationFromCenterToOrigin = new double[ 3 ];
		double[] translationFromOriginToCenter = new double[ 3 ];

		for ( int d = 0; d < 3; ++d )
		{
			translationFromCenterToOrigin[ d ] = - rotationCenter[ d ];
			translationFromOriginToCenter[ d ] = + rotationCenter[ d ];
		}

		// move to rotation centre...
		final AffineTransform3D rotationAroundCenterTransform = new AffineTransform3D();
		rotationAroundCenterTransform.translate( translationFromCenterToOrigin );

		// ...rotate...
		rotationAroundCenterTransform.preConcatenate( rotationTransform );

		// ...and move back to the origin
		final AffineTransform3D transformOriginToCenter = new AffineTransform3D();
		transformOriginToCenter.translate( translationFromOriginToCenter );
		rotationAroundCenterTransform.preConcatenate( transformOriginToCenter );

		return rotationAroundCenterTransform;
	}

	public static String asStringBdvStyle( AffineTransform3D affineTransform3D )
	{

		String out = "";
		for ( int row = 0; row < 3; ++row )
			for ( int col = 0; col < 4; ++col )
				out += String.format( "%.4f",  affineTransform3D.get( row, col ) ) + " ";

		return out;
	}
}

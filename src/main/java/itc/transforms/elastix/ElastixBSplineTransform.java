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

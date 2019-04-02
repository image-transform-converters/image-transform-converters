package itc.transforms.elastix;

import org.scijava.plugin.Parameter;

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
}

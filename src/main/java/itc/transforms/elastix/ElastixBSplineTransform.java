package itc.transforms.elastix;

public class ElastixBSplineTransform extends ElastixTransform {
    // BSplineTransform specific
    public Integer[] GridSize;
    public Integer[] GridIndex;
    public Double[] GridSpacing;
    public Double[] GridOrigin;
    public Double[] GridDirection;
    public Integer BSplineTransformSplineOrder;
    public Boolean UseCyclicTransform;
}

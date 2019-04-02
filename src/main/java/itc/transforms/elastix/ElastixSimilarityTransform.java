package itc.transforms.elastix;

import org.scijava.plugin.Parameter;

public class ElastixSimilarityTransform extends ElastixTransform {
    // SimilarityTransform specific
    @Parameter
    public Double[] CenterOfRotationPoint;
}
